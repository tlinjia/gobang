package main.Socket.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.application.Platform;
import main.Utils.Formatter;
import main.Utils.Status;

/**
 * 服务端处理
 * Created by lin on 2017/3/8/0008.
 */
public class BuildServerHandler extends ChannelInboundHandlerAdapter implements MessageHandler{
    private static boolean existClient = false;
    private static Channel channel;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(existClient){
            ctx.writeAndFlush(Unpooled.copiedBuffer(Formatter.constructPackage("@busy:exist connection!")));
            return;
        }
        channel = ctx.channel();
        ctx.writeAndFlush(Unpooled.copiedBuffer(Formatter.constructPackage("@name:"+Status.getName())));  // @* -- 消息类型
        Platform.runLater(()->{  //服务端默认先黑棋
            Status.switchFlagProperty().set(true);
            Status.setChessFlag(true);
        });
        System.out.println(ctx.channel().remoteAddress()+"已连接");
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

    public static void setExistClient(boolean existClient) {
        BuildServerHandler.existClient = existClient;
    }

    public static boolean isExistClient() {
        return existClient;
    }

    public static Channel getChannel() {
        return channel;
    }
}
