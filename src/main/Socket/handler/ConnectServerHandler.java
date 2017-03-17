package main.Socket.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.application.Platform;
import main.Utils.Formatter;
import main.Utils.Status;

/**
 * 客户端处理
 * Created by lin on 2017/3/8/0008.
 */
public class ConnectServerHandler extends ChannelInboundHandlerAdapter implements MessageHandler{
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer(Formatter.constructPackage("@name:"+ Status.getName())));  // @* -- 消息类型
        Platform.runLater(()->{  //客户端默认先白棋
            Status.switchFlagProperty().set(true);
            Status.setChessFlag(false);
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        msgHandle(ctx,msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Status.getConnection().init();
        System.out.println(cause);
    }
}
