package main.Socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import javafx.application.Platform;
import main.Socket.handler.BuildServerHandler;
import main.controller.MainController;
import main.Utils.Status;
import main.views.AlertWindow;
import main.views.ConnectionWindow;
import main.views.Main;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 建立主机
 * Created by lin on 2017/3/8/0008.
 */
public class BuildServer implements Connection{
    private EventLoopGroup bossGroup;
    private EventLoopGroup wokerGroup;
    private ChannelFuture future;

    public BuildServer build(int port) {
        bossGroup = new NioEventLoopGroup();
        wokerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, wokerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true) //有数据就发送
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    public void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new BuildServerHandler());
                    }
                });
        future = bootstrap.bind(port);
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            future.addListener(channelFuture -> {
                System.out.println("创建主机成功，绑定端口号: " + port);
                Platform.runLater(() -> {
                    new AlertWindow("创建成功，您的IP地址为：" + ip + "\n端口号为：" + port).display(ConnectionWindow.getConStage(), true);
                    Status.buildServerProperty().set(true);
                    Main.getLoader().<MainController>getController().appendText("创建成功，正在等待客户端连接...");
                });

            });
        } catch (UnknownHostException e) {
            Platform.runLater(()->{
                new AlertWindow("建立主机出错，请重试！").display(ConnectionWindow.getConStage(),false);
            });
        }

        future.channel().closeFuture().addListener(channelFuture -> {
            System.out.println("服务器已关闭！");
            Platform.runLater(()-> {
                Status.connectedProperty().set(false);
                Status.buildServerProperty().set(false);
            });
        });
        return this;
    }

    @Override
    public void disconnection() {
        if (bossGroup != null)
            bossGroup.shutdownGracefully();
        if (wokerGroup != null)
            wokerGroup.shutdownGracefully();
        System.out.println("连接已关闭!");
        Platform.runLater(()->{
            Main.getLoader().<MainController>getController().appendText("连接已断开!");
            Status.connectedProperty().set(false);
        });
    }
}
