package com.github.leeyazhou.scf.client.proxy.builder;

import java.util.concurrent.ExecutorService;

import com.github.leeyazhou.scf.client.loadbalance.Server;
import com.github.leeyazhou.scf.client.loadbalance.component.ServerState;
import com.github.leeyazhou.scf.core.exception.ExceptionProtocol;
import com.github.leeyazhou.scf.core.exception.ThrowErrorHelper;
import com.github.leeyazhou.scf.core.exception.TimeoutException;
import com.github.leeyazhou.scf.protocol.entity.SDPType;
import com.github.leeyazhou.scf.protocol.sdp.ResponseProtocol;
import com.github.leeyazhou.scf.protocol.sfp.Protocol;

/**
 * a abstract class for description callback funcation
 */

public abstract class ReceiveHandler {

  private Server server;

  public void setServer(Server server) {
    this.server = server;
  }

  @SuppressWarnings("rawtypes")
  public void notify(final byte[] buffer, ExecutorService executor) throws Exception {
    InvokeResult<?> result = null;
    Protocol receiveP = Protocol.fromBytes(buffer);
    if (server.getState() == ServerState.Testing) {
      server.relive();
    }
    if (receiveP == null) {
      throw new Exception("userdatatype error!");
    }
    if (receiveP.getSDPType() == SDPType.Response) {
      ResponseProtocol rp = (ResponseProtocol) receiveP.getSdpEntity();
      result = new InvokeResult(rp.getResult(), rp.getOutpara());
    } else if (receiveP.getSDPType() == SDPType.Exception) {
      ExceptionProtocol ep = (ExceptionProtocol) receiveP.getSdpEntity();
      result = new InvokeResult(ThrowErrorHelper.throwServiceError(ep.getErrorCode(), ep.getErrorMsg()), null);
    } else if (receiveP.getSDPType() == SDPType.Reset) { /** 服务重启 */
      try {
        server.createReboot();
        return;
      } catch (Throwable e) {
        e.printStackTrace();
      }
    } else {
      result = new InvokeResult(new Exception("userdatatype error!"), null);
    }

    executor.execute(new ReturnWorks(result));
  }

  public abstract void callBack(Object obj);

  public void backException(Object obj) {
    if (obj instanceof TimeoutException) {
      try {
        throw (TimeoutException) obj;
      } catch (TimeoutException e) {
        e.printStackTrace();
      }
    }
  }

  class ReturnWorks implements Runnable {
    InvokeResult<?> result = null;

    public ReturnWorks(InvokeResult<?> result) {
      this.result = result;
    }

    @Override
    public void run() {
      if (result.getResult() != null) {
        callBack(result.getResult());
      }
    }
  }
}
