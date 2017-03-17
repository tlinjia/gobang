package main.Socket;

import javafx.application.Platform;
import main.Socket.handler.BuildServerHandler;
import main.Utils.Status;
import main.controller.MainController;
import main.views.AlertWindow;
import main.views.Main;

/**
 * 封装服务端-客户端共同操作
 * Created by lin on 2017/3/8/0008.
 */
public interface Connection {
    void disconnection();

    default void init(){
        Platform.runLater(()->{
            new AlertWindow(Status.otherNameProperty().get()+"已退出游戏，请重新建立或连接主机！").display(Main.getPrimaryStage(),false);
            Main.getLoader().<MainController>getController().appendText("连接已断开!");
            Status.connectedProperty().set(false);
            Status.buildServerProperty().set(false);
            Status.otherNameProperty().set("NONE");
            BuildServerHandler.setExistClient(false);
        });
        disconnection();
    }

    void sendMessage(String msg);

}
