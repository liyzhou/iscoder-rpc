package com.github.leeyazhou.scf.server.deploy.bytecode;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.leeyazhou.scf.core.loader.DynamicClassLoader;
import com.github.leeyazhou.scf.server.contract.context.IProxyFactory;
import com.github.leeyazhou.scf.server.contract.context.IProxyStub;

/**
 * 
 */
public class CreateManager {

  private static Logger logger = LoggerFactory.getLogger(CreateManager.class);

  public IProxyFactory careteProxy(String serviceRootPath, DynamicClassLoader classLoader) {
    IProxyFactory proxyFactory = null;
    try {
      ContractInfo serviceContract = ScanClass.getContractInfo(serviceRootPath + "/", classLoader);

      long time = System.currentTimeMillis();
      logger.info("proxy class buffer creater finish!!!");
      ClassFile cfProxyFactory = new ProxyFactoryCreater().createProxy(classLoader, serviceContract, time);
      logger.info("proxy factory buffer creater finish!!!");
      List<ClassFile> localProxyList = new ProxyClassCreater().createProxy(classLoader, serviceContract, time);
      logger.info("localProxyList : " + localProxyList);
      List<IProxyStub> localProxyAry = new ArrayList<IProxyStub>(localProxyList.size());
      for (ClassFile cf : localProxyList) {
        Class<?> cls = classLoader.findClass(cf.getClsName(), cf.getClsByte(), null);
        logger.info("dynamic load class:" + cls.getName());
        localProxyAry.add((IProxyStub) cls.newInstance());
      }

      Class<?> proxyFactoryCls = classLoader.findClass(cfProxyFactory.getClsName(), cfProxyFactory.getClsByte(), null);
      Constructor<?> constructor = proxyFactoryCls.getConstructor(List.class);
      proxyFactory = (IProxyFactory) constructor.newInstance(localProxyAry);
      logger.info("crate ProxyFactory instance : " + proxyFactory);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return proxyFactory;
  }
}
