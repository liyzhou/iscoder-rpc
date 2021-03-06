package com.github.leeyazhou.scf.server.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.leeyazhou.scf.core.exception.ExceptionProtocol;
import com.github.leeyazhou.scf.core.util.StringUtil;
import com.github.leeyazhou.scf.protocol.entity.PlatformType;
import com.github.leeyazhou.scf.protocol.sdp.HandclaspProtocol;
import com.github.leeyazhou.scf.protocol.sfp.Protocol;
import com.github.leeyazhou.scf.server.contract.context.ExecFilterType;
import com.github.leeyazhou.scf.server.contract.context.Global;
import com.github.leeyazhou.scf.server.contract.context.SCFContext;
import com.github.leeyazhou.scf.server.contract.context.SCFResponse;
import com.github.leeyazhou.scf.server.contract.context.SecureContext;
import com.github.leeyazhou.scf.server.contract.context.ServerType;
import com.github.leeyazhou.scf.server.util.ExceptionUtil;
import com.github.leeyazhou.scf.server.util.SecureKeyUtil;

public class HandclaspFilter implements IFilter {

  private static final Logger logger = LoggerFactory.getLogger(HandclaspFilter.class);

  @Override
  public int getPriority() {
    // TODO Auto-generated method stub
    return 0;
  }

  /**
   * 权限认证filter
   */
  @Override
  public void filter(SCFContext context) throws Exception {

    Protocol protocol = context.getScfRequest().getProtocol();
    if (protocol.getPlatformType() == PlatformType.Java && context.getServerType() == ServerType.TCP) {// java
                                                                                                       // 客户端支持权限认证
      SCFResponse response = new SCFResponse();
      Global global = Global.getSingleton();
      // 是否启用权限认证
      if (Global.getSingleton().getGlobalSecureIsRights()) {
        SecureContext sc = global.getGlobalSecureContext(context.getChannel().getNettyChannel());
        // 判断当前channel是否通过认证
        if (!sc.isRights()) {
          // 没有通过认证
          if (protocol != null && protocol.getSdpEntity() instanceof HandclaspProtocol) {
            SecureKeyUtil sk = new SecureKeyUtil();
            HandclaspProtocol handclaspProtocol = (HandclaspProtocol) protocol.getSdpEntity();
            /**
             * 接收 客户端公钥
             */
            if ("1".equals(handclaspProtocol.getType())) {
              sk.initRSAkey();
              // 客户端发送公钥数据
              String clientPublicKey = handclaspProtocol.getData();
              if (null == clientPublicKey || "".equals(clientPublicKey)) {
                logger.warn("get client publicKey warn!");
              }
              // java 客户端
              if (protocol.getPlatformType() == PlatformType.Java) {
                // 服务器生成公/私钥,公钥传送给客户端
                sc.setServerPublicKey(sk.getStringPublicKey());
                sc.setServerPrivateKey(sk.getStringPrivateKey());
                sc.setClientPublicKey(clientPublicKey);
                handclaspProtocol.setData(sk.getStringPublicKey());// 服务器端公钥
              }
              // net客户端
              if (protocol.getPlatformType() == PlatformType.Dotnet) {
                // 服务器生成公/私钥,公钥传送给客户端
                // String abcd = sk.getStringPrivateKey();
                // sc.setServerPublicKey(sk.getStringPublicKey());
                // sc.setServerPrivateKey(sk.getStringPrivateKey());
                // sc.setClientPublicKey(clientPublicKey);
                // handclaspProtocol.setData(sk.getStringPublicKey());//服务器端公钥
              }

              protocol.setSdpEntity(handclaspProtocol);
              response.setResponseBuffer(protocol.toBytes());
              context.setScfResponse(response);
              this.setInvokeAndFilter(context);
              logger.info("send server publieKey sucess!");
            }
            /**
             * 接收权限文件
             */
            else if ("2".equals(handclaspProtocol.getType())) {
              // 客户端加密授权文件
              String clientSecureInfo = handclaspProtocol.getData();
              if (null == clientSecureInfo || "".equals(clientSecureInfo)) {
                logger.warn("get client secureKey warn!");
              }
              // 授权文件客户端原文(服务器私钥解密)
              String sourceInfo = sk.decryptByPrivateKey(clientSecureInfo, sc.getServerPrivateKey());
              // 校验授权文件是否相同
              // 判断是否合法,如果合法服务器端生成DES密钥，通过客户端提供的公钥进行加密传送给客户端
              if (global.containsSecureMap(sourceInfo)) {
                logger.info("secureKey is ok!");
                String desKey = StringUtil.getRandomNumAndStr(8);
                // 设置当前channel属性
                sc.setDesKey(desKey);
                sc.setRights(true);
                handclaspProtocol.setData(sk.encryptByPublicKey(desKey, sc.getClientPublicKey()));
                protocol.setSdpEntity(handclaspProtocol);
                response.setResponseBuffer(protocol.toBytes());
                context.setScfResponse(response);
              } else {
                logger.error("It's bad secureKey!");
                this.ContextException(context, protocol, response, "授权文件错误!");
              }
              this.setInvokeAndFilter(context);
            } else {
              // 权限认证 异常情况
              logger.error("权限认证异常!");
              this.ContextException(context, protocol, response, "权限认证 异常!");
            }
            response = null;
            sk = null;
            handclaspProtocol = null;
          } else {
            // 客户端没有启动权限认证
            logger.error("客户端没有启用权限认证!");
            this.ContextException(context, protocol, response, "客户端没有启用权限认证!");
          }
        }
      } else {
        if (protocol != null && protocol.getSdpEntity() instanceof HandclaspProtocol) {
          // 异常--当前服务器没有启动权限认证
          logger.error("当前服务没有启用权限认证!");
          this.ContextException(context, protocol, response, "当前服务没有启用权限认证!");
        }
      }
    }
  }

  public void ContextException(SCFContext context, Protocol protocol, SCFResponse response, String message) throws Exception {
    ExceptionProtocol ep = ExceptionUtil.createError(new Exception());
    ep.setErrorMsg(message);
    protocol.setSdpEntity(ep);
    response.setResponseBuffer(protocol.toBytes());
    context.setScfResponse(response);
    this.setInvokeAndFilter(context);
  }

  public void setInvokeAndFilter(SCFContext context) {
    context.setExecFilter(ExecFilterType.None);
    context.setDoInvoke(false);
  }
}
