package main.Socket.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import javafx.application.Platform;
import main.Utils.Status;
import main.controller.MainController;
import main.views.AlertWindow;
import main.views.ConnectionWindow;
import main.views.Main;

import java.io.UnsupportedEncodingException;

/**
 * 封装消息处理流程
 * Created by lin on 2017/3/10/0010.
 */
public interface MessageHandler {
    default void msgHandle(ChannelHandlerContext ctx,Object msg) throws UnsupportedEncodingException {
        ByteBuf buf = (ByteBuf)msg;
        byte[] temp = new byte[buf.readableBytes()];
        buf.readBytes(temp);
        String message = new String(temp,"UTF-8");
        String[] temps = message.split(":",2);
        String type = temps[0];
        String contents = temps[1];
        System.out.println(type);
        switch (type){
            case "@name":{
                Platform.runLater(()->{
                    Status.otherNameProperty().set(contents);
                    Status.connectedProperty().set(true);
                    Main.getLoader().<MainController>getController().appendText(Status.otherNameProperty().get() + "进入了游戏！");
                });
                break;
            }
            case "@busy":{
                ctx.channel().close();
                Platform.runLater(()->{
                    new AlertWindow("远程主机正忙，请稍后重试！").display(ConnectionWindow.getConStage(),false);
                    Status.connectedProperty().set(false);
                });
            }
        }
    }
}
