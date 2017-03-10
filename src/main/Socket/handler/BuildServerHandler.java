package main.Socket.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import main.Utils.Status;

/**
 * 服务端处理
 * Created by lin on 2017/3/8/0008.
 */
public class BuildServerHandler extends ChannelInboundHandlerAdapter implements MessageHandler{
    private static boolean existClient = false;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(existClient){
            ctx.writeAndFlush(Unpooled.copiedBuffer("@busy".getBytes()));
            ctx.disconnect();
        }
        existClient = true;
        ctx.writeAndFlush(Unpooled.copiedBuffer(("@name:"+Status.getName()).getBytes()));  // @* -- 消息类型
        System.out.println(ctx.channel().remoteAddress()+"已连接");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        msgHandle(ctx,msg);
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    public static void setExistClient(boolean existClient) {
        BuildServerHandler.existClient = existClient;
    }


}
