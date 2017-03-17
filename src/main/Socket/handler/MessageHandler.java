package main.Socket.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import javafx.application.Platform;
import main.Utils.Formatter;
import main.Utils.Status;
import main.controller.MainController;
import main.views.AlertWindow;
import main.views.ConfirmWindow;
import main.views.ConnectionWindow;
import main.views.Main;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.UnsupportedEncodingException;

/**
 * 封装消息处理流程
 * Created by lin on 2017/3/10/0010.
 */
public interface MessageHandler {
    MainController mainControl = Main.getLoader().getController();

    default void msgHandle(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        ByteBuf buf = (ByteBuf) msg;
        byte[] temp = new byte[buf.readableBytes()];
        buf.readBytes(temp);
        String message = new String(temp, "UTF-8");
        String[] temps = message.split(":", 2);
        String type = temps[0];
        String contents = temps[1];
        switch (type) {
            //昵称
            case "@name": {
                if (!BuildServerHandler.isExistClient()) {
                    if (Status.buildServerProperty().get())
                        BuildServerHandler.setExistClient(true);
                    Platform.runLater(() -> {
                        Status.otherNameProperty().set(contents);
                        Status.connectedProperty().set(true);
                        Main.getLoader().<MainController>getController().appendText(Status.otherNameProperty().get() + "进入了游戏！");
                    });
                }
                break;
            }
            //连接主机繁忙
            case "@busy": {
                ctx.writeAndFlush(Unpooled.copiedBuffer(Formatter.constructPackage("@interrup:server is busy"))).addListener((event) -> {
                    ctx.channel().close();
                    Platform.runLater(() -> {
                        new AlertWindow("远程主机正忙，请稍后重试！").display(ConnectionWindow.getConStage(), false);
                        Status.connectedProperty().set(false);
                    });
                });
                break;
            }
            case "@ready": {
                Platform.runLater(() -> {
                    if (contents.equals("ready")) {
                        Status.otherReadyProperty().set(true);
                        if (Status.readyProperty().get())   //双方准备，游戏开始
                            Status.beginProperty().set(true);
                    } else if (contents.equals("cancel")) {
                        Status.otherReadyProperty().set(false);
                    }
                });
                break;
            }
            //异常中断
            case "@interrup": {
                ctx.channel().close();
                break;
            }
            //正常断开连接
            case "@disconnection": {
                ctx.channel().close();
                Status.getConnection().init();
                break;
            }
            case "@compromise": {
                if (contents.equals("request")) {
                    Platform.runLater(() -> {
                        if (new ConfirmWindow(Status.getOtherName() + "请求平局，是否同意？").display(Main.getPrimaryStage())) {
                            mainControl.gameOver();
                            Status.getConnection().sendMessage("@compromise:agree");
                        } else {
                            Status.getConnection().sendMessage("@compromise:refuse");
                        }
                    });
                } else if (contents.equals("agree")) {
                    Platform.runLater(() -> mainControl.gameOver());
                } else if (contents.equals("refuse")) {
                    Platform.runLater(() -> new AlertWindow("对方拒绝了您的请求。").display(Main.getPrimaryStage(), false));
                }
                break;
            }
            case "@surrender": {
                Platform.runLater(() -> mainControl.gameOver(Status.isChessFlag()));
                break;
            }
            case "@move": {
                int index = Integer.parseInt(contents.split(",")[0]);
                int value = Integer.parseInt(contents.split(",")[1]);
                Status.getChessboard()[index] = value; //更新棋盘
                Status.getPast().push(index);
                Platform.runLater(() -> {
                    mainControl.handleDisabledList(index,true);
                    mainControl.repaint();
                    if (mainControl.isOver(index)) {
                        mainControl.gameOver(!Status.isChessFlag());
                    }
                    Status.setSwitchFlag(!Status.isSwitchFlag());
                }); //重画棋盘
                break;
            }
            case "@back": {
                if (contents.equals("request")) {
                    Platform.runLater(() -> {
                        if (new ConfirmWindow(Status.getOtherName() + "请求悔棋，是否同意？").display(Main.getPrimaryStage())) {
                            int index = Status.getPast().pop();
                            Status.getConnection().sendMessage("@back:agree");
                            Status.setSwitchFlag(!Status.isSwitchFlag());
                            Status.getChessboard()[index] = 0;
                            mainControl.getChessboard().getChildren().remove(mainControl.getChessMap().get(index));
                            mainControl.handleDisabledList(index,false);
                            mainControl.getChessMap().remove(index);
                            mainControl.repaint();
                        } else {
                            Status.getConnection().sendMessage("@back:refuse");
                        }
                    });
                } else if (contents.equals("agree")) {
                    Platform.runLater(() -> {
                        int index = Status.getPast().pop();
                        Status.getChessboard()[index] = 0;
                        Status.setSwitchFlag(!Status.isSwitchFlag());
                        mainControl.getChessboard().getChildren().remove(mainControl.getChessMap().get(index));
                        mainControl.handleDisabledList(index,false);
                        mainControl.getChessMap().remove(index);
                        mainControl.repaint();
                    });
                } else if (contents.equals("refuse")) {
                    Platform.runLater(() -> {
                        new AlertWindow("对方拒绝了您的请求。").display(Main.getPrimaryStage(), false);
                    });
                }
                break;
            }
            case "@msg":{
                Platform.runLater(()->{
                    mainControl.appendText(String.join(":",Status.otherNameProperty().get(), StringEscapeUtils.unescapeJava(contents)));
                });
                break;
            }
        }
    }


}
