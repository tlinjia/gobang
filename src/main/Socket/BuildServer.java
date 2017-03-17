package main.Socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import javafx.application.Platform;
import main.Socket.handler.BuildServerHandler;
import main.Utils.Formatter;
import main.controller.MainController;
import main.Utils.Status;
import main.views.AlertWindow;
import main.views.ConnectionWindow;
import main.views.Main;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

/**
 * 建立主机
 * Created by lin on 2017/3/8/0008.
 */
public class BuildServer implements Connection {
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
                        socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN,1024,0,4,0,4,true));
                        socketChannel.pipeline().addLast(new BuildServerHandler());
                    }
                });
        future = bootstrap.bind(port);
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            future.addListener(channelFuture -> {
                if (!channelFuture.isSuccess()) {
                    Platform.runLater(()->new AlertWindow("端口被占用!请更换端口!").display(ConnectionWindow.getConStage(), false));
                } else {
                    System.out.println("创建主机成功，绑定端口号: " + port);
                    Platform.runLater(() -> {
                        new AlertWindow("创建成功，您的IP地址为：" + ip + "\n端口号为：" + port).display(ConnectionWindow.getConStage(), true);
                        Status.buildServerProperty().set(true);
                        Main.getLoader().<MainController>getController().appendText("创建成功\n您的IP地址为：" + ip + "\n端口号为：" + port);
                        Main.getLoader().<MainController>getController().appendText("正在等待客户端连接...");
                    });
                }
            });
        } catch (UnknownHostException e) {
            Platform.runLater(() -> {
                new AlertWindow("建立主机出错，请重试！").display(ConnectionWindow.getConStage(), false);
            });
        }

        future.channel().closeFuture().addListener(channelFuture -> {
            System.out.println("服务器已关闭！");
            Platform.runLater(() -> {
                Status.connectedProperty().set(false);
                Status.buildServerProperty().set(false);
            });
        });
        return this;
    }

    @Override
    public void disconnection() {
        if (Status.isConnected() && BuildServerHandler.getChannel() != null)
            BuildServerHandler.getChannel().writeAndFlush(Unpooled.copiedBuffer(Formatter.constructPackage("@disconnection:disconnect"))).addListener((event) -> {
                shutdownGracefully();
            });
        else {
            shutdownGracefully();
        }
        Platform.runLater(()->Status.otherNameProperty().set("NONE"));
        Status.setConnection(null);
    }

    public void shutdownGracefully() {
        if (bossGroup != null)
            bossGroup.shutdownGracefully();
        if (wokerGroup != null)
            wokerGroup.shutdownGracefully();
        BuildServerHandler.setExistClient(false);
    }

    @Override
    public void sendMessage(String msg) {
        BuildServerHandler.getChannel().writeAndFlush(Unpooled.copiedBuffer(Formatter.constructPackage(msg)));
    }


}
