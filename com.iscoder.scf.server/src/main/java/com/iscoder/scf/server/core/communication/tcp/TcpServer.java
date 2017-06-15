package com.iscoder.scf.server.core.communication.tcp;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iscoder.scf.server.contract.context.Global;
import com.iscoder.scf.server.core.communication.Server;
import com.iscoder.scf.server.core.handler.Handler;

/**
 * start netty server
 * 
 */
public class TcpServer implements Server {
  private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);

  public TcpServer() {

  }

  /**
   * netty ServerBootstrap
   */
  private static final ServerBootstrap bootstrap = new ServerBootstrap();

  /**
   * record all channel
   */
  static final ChannelGroup allChannels = new DefaultChannelGroup("SCF-SockerServer");

  /**
   * invoker handle
   */
  static Handler handler = null;

  /**
   * start netty server
   */
  @Override
  public void start() throws Exception {
    logger.info("loading invoker...");
    String invoker = Global.getSingleton().getServiceConfig().getString("scf.proxy.invoker.implement");
    handler = (Handler) Class.forName(invoker).newInstance();
    logger.info("initing server...");
    initSocketServer();
  }

  /**
   * stop netty server
   */
  @Override
  public void stop() throws Exception {
    logger.info("----------------------------------------------------");
    logger.info("-- socket server closing...");
    logger.info("-- channels count : " + allChannels.size());

    ChannelGroupFuture future = allChannels.close(); // close all channel
    logger.info("-- closing all channels...");
    future.awaitUninterruptibly(); // wait for channel close
    logger.info("-- closed all channels...");
    bootstrap.getFactory().releaseExternalResources(); // release external
                                                       // resources
    logger.info("-- released external resources");
    logger.info("-- close success !");
    logger.info("----------------------------------------------------");
  }

  /**
   * 初始化socket server
   * 
   * @throws Exception
   */
  private void initSocketServer() throws Exception {
    final boolean tcpNoDelay = true;
    logger.info("-- socket server config --");
    logger.info("-- listen ip: " + Global.getSingleton().getServiceConfig().getString("scf.server.tcp.listenIP"));
    logger.info("-- port: " + Global.getSingleton().getServiceConfig().getInt("scf.server.tcp.listenPort"));
    logger.info("-- tcpNoDelay: " + tcpNoDelay);
    logger.info("-- receiveBufferSize: " + Global.getSingleton().getServiceConfig().getInt("scf.server.tcp.receiveBufferSize"));
    logger.info("-- sendBufferSize: " + Global.getSingleton().getServiceConfig().getInt("scf.server.tcp.sendBufferSize"));
    logger.info("-- frameMaxLength: " + Global.getSingleton().getServiceConfig().getInt("scf.server.tcp.frameMaxLength"));
    logger.info("-- worker thread count: " + Global.getSingleton().getServiceConfig().getInt("scf.server.tcp.workerCount"));
    logger.info("--------------------------");

    logger.info(Global.getSingleton().getServiceConfig().getServiceName() + " SocketServer starting...");

    bootstrap.setFactory(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool(),
        Global.getSingleton().getServiceConfig().getInt("scf.server.tcp.workerCount")));

    TcpHandler handler = new TcpHandler();

    bootstrap.setPipelineFactory(
        new TcpPipelineFactory(handler, Global.getSingleton().getServiceConfig().getInt("scf.server.tcp.frameMaxLength")));
    bootstrap.setOption("child.tcpNoDelay", tcpNoDelay);
    bootstrap.setOption("child.receiveBufferSize", Global.getSingleton().getServiceConfig().getInt("scf.server.tcp.receiveBufferSize"));
    bootstrap.setOption("child.sendBufferSize", Global.getSingleton().getServiceConfig().getInt("scf.server.tcp.sendBufferSize"));

    try {
      InetSocketAddress socketAddress = new InetSocketAddress(Global.getSingleton().getServiceConfig().getString("scf.server.tcp.listenIP"),
          Global.getSingleton().getServiceConfig().getInt("scf.server.tcp.listenPort"));
      Channel channel = bootstrap.bind(socketAddress);
      allChannels.add(channel);
    } catch (Exception e) {
      logger.error("init socket server error", e);
      System.exit(1);
    }
  }
}