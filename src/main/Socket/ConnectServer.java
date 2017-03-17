package main.Socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.concurrent.Promise;
import javafx.application.Platform;
import main.Socket.handler.BuildServerHandler;
import main.Socket.handler.ConnectServerHandler;
import main.Utils.Formatter;
import main.controller.ConnectionController;
import main.controller.MainController;
import main.Utils.Status;
import main.views.AlertWindow;
import main.views.ConnectionWindow;
import main.views.Main;

import java.net.InetAddress;
import java.nio.ByteOrder;

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
                        socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN,1024,0,4,0,4,true));
                        socketChannel.pipeline().addLast(new ConnectServerHandler());
                    }
                });

        future = bootstrap.connect(ip,port);

        future.addListener(ChannelFuture -> {
            if(future.isSuccess()) {
                System.out.println("已连接主机:" + ip.getHostAddress());
                Platform.runLater(() -> {
                    new AlertWindow("连接成功!").display(ConnectionWindow.getConStage(), true);
                    Status.setConnection(this);
                    Status.connectedProperty().set(true);
                });
            }else{
                Platform.runLater(()->new AlertWindow("连接失败，请检查ip及端口是否正确！").display(ConnectionWindow.getConStage(),false));
            }
        });

        future.channel().closeFuture().addListener((event) -> {
            System.out.println("已关闭连接");
            Platform.runLater(()-> Status.connectedProperty().set(false));
        });
        return this;
    }

    @Override
    public void disconnection() {
        if(future.channel().isWritable())
            future.channel().writeAndFlush(Unpooled.copiedBuffer(Formatter.constructPackage("@disconnection:disconnect")));
        future.channel().closeFuture().addListener((event) -> {
            if (group != null)
                group.shutdownGracefully();
        });
        Status.setConnection(null);
    }

    @Override
    public void sendMessage(String msg) {
        future.channel().writeAndFlush(Unpooled.copiedBuffer(Formatter.constructPackage(msg)));
    }
}
