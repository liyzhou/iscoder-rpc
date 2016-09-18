package com.chaboshi.scf.server.filter;

import java.util.Collection;

import com.chaboshi.scf.server.contract.context.Global;
import com.chaboshi.scf.server.contract.context.SCFContext;
import com.chaboshi.scf.server.contract.context.StopWatch;
import com.chaboshi.scf.server.contract.context.StopWatch.PerformanceCounter;
import com.chaboshi.scf.server.contract.filter.IFilter;
import com.chaboshi.scf.server.contract.log.ILog;
import com.chaboshi.scf.server.contract.log.LogFactory;
import com.chaboshi.scf.server.util.UDPClient;

/**
 * A filter for record execute time
 * 
 * @author Service Platform Architecture Team (spat@58.com)
 * 
 *         <a href="http://blog.58.com/spat/">blog</a>
 *         <a href="http://www.58.com">website</a>
 * 
 */
public class ExecuteTimeFilter implements IFilter {

  private static int minRecordTime = 200;

  private static String serviceName;

  private static UDPClient udpClient = null;

  private static ILog logger = LogFactory.getLogger(ExecuteTimeFilter.class);

  static {
    try {
      String ip = Global.getSingleton().getServiceConfig().getString("scf.log.udpserver.ip");
      int port = Global.getSingleton().getServiceConfig().getInt("scf.log.udpserver.port");
      minRecordTime = Global.getSingleton().getServiceConfig().getInt("scf.log.exectime.limit");
      serviceName = Global.getSingleton().getServiceConfig().getString("scf.service.name");

      if (ip == null || port <= 0) {
        logger.error("upd ip is null or port is null");
      } else {
        udpClient = UDPClient.getInstrance(ip, port, "utf-8");
      }
    } catch (Exception ex) {
      logger.error("init ExecuteTimeFilter error", ex);
    }
  }

  @Override
  public void filter(SCFContext context) throws Exception {
    StopWatch sw = context.getStopWatch();
    Collection<PerformanceCounter> pcList = sw.getMapCounter().values();
    for (PerformanceCounter pc : pcList) {
      if (pc.getExecuteTime() > minRecordTime) {
        StringBuilder sbMsg = new StringBuilder();
        sbMsg.append(serviceName);
        sbMsg.append("--");
        sbMsg.append(pc.getKey());
        sbMsg.append("--time: ");
        sbMsg.append(pc.getExecuteTime());

        sbMsg.append(" [fromIP: ");
        sbMsg.append(sw.getFromIP());
        sbMsg.append(";localIP: ");
        sbMsg.append(sw.getLocalIP() + "]");

        udpClient.send(sbMsg.toString());
      }
    }
  }

  @Override
  public int getPriority() {
    // TODO Auto-generated method stub
    return 0;
  }
}