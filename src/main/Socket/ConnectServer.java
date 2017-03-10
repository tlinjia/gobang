package main.Socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.application.Platform;
import main.Socket.handler.ConnectServerHandler;
import main.controller.MainController;
import main.Utils.Status;
import main.views.AlertWindow;
import main.views.ConnectionWindow;
import main.views.Main;

import java.net.InetAddress;

/**
 * 连接主机
 * Created by lin on 2017/3/8/0008.
 */
public class ConnectServer implements Connection{
    private InetAddress ip;
    private int port = 4747;  //默认端口4747
    private EventLoopGroup group;
    private ChannelFuture future;
    public ConnectServer(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public ConnectServer connect() {
        group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new ConnectServerHandler());
                    }
                });

        future = bootstrap.connect(ip,port);

        future.addListener(ChannelFuture -> {
            System.out.println("已连接主机:"+ip.getHostAddress());
            Platform.runLater(() -> {
                new AlertWindow("连接成功!").display(ConnectionWindow.getConStage(),true);
                Status.connectedProperty().set(true);
            });
        });

        future.channel().closeFuture().addListener((event) -> {
            System.out.println("已断开连接");
            Platform.runLater(()-> Status.connectedProperty().set(false));
        });
        return this;
    }

    @Override
    public void disconnection() {
        future.channel().close().addListener((event) -> {
            if (group != null)
                group.shutdownGracefully();
            System.out.println("连接已关闭!");
            Platform.runLater(()->{
                Main.getLoader().<MainController>getController().appendText("连接已断开!");
                Status.connectedProperty().set(false);
            });
        });
    }
}
