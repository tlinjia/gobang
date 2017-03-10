package main.controller;

import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import main.Socket.BuildServer;
import main.Socket.ConnectServer;
import main.Utils.Status;
import main.views.AlertWindow;
import main.views.ConnectionWindow;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

/**
 * 连接界面控制器
 * Created by lin on 2017/3/8/0008.
 */
public class ConnectionController implements Initializable{

    @FXML
    TextField nameField, ipField, portField;

    @FXML
    Button buildBtn,connectBtn;

    @FXML
    private void buildHost() throws UnsupportedEncodingException {
        if (isEmpty(nameField)) {
            emptyOperation("name");
        } else if (nameField.getText().getBytes("GB2312").length > 8) {
            new AlertWindow("昵称不能超过8个字符!").display(ConnectionWindow.getConStage(),false);
        } else {
            Status.setName(nameField.getText());
            int port = 4747;
            boolean hasFault = false; //是否端口有误

            if (!isEmpty(portField)) {
                try {
                    port = Integer.parseInt(portField.getText());
                } catch (NumberFormatException e) {
                    new AlertWindow("  端口号输入错误!\n请输入1025~65534之间的整数!").display(ConnectionWindow.getConStage(),false);
                    hasFault = true;
                }
            }

            if (!hasFault) {
                Status.setConnection(new BuildServer().build(port));
            }

        }
    }

    @FXML
    private void connectHost() {
        if (isEmpty(nameField)) {
            emptyOperation("name");
        }else if(isEmpty(ipField)){
            emptyOperation("ip");
        }else{
            Status.setName(nameField.getText());
            boolean hasFault = false;
            InetAddress ip = null;
            int port = 4747;
            try {
                ip = InetAddress.getByName(ipField.getText());
                if(!isEmpty(portField)){
                    port = Integer.parseInt(portField.getText());
                }
            } catch (UnknownHostException e) {
                new AlertWindow("ip地址有误,请重试!").display(ConnectionWindow.getConStage(),false);
                hasFault = true;
            } catch (NumberFormatException e){
                new AlertWindow("  端口号输入错误!\n请输入1025~65534之间的整数!").display(ConnectionWindow.getConStage(),false);
                hasFault = true;
            }

            if(!hasFault && ip != null){
               Status.setConnection(new ConnectServer(ip,port).connect());
            }
        }
    }

    private boolean isEmpty(TextField field){
        if(field.getText().trim().length() <= 0) return true;
        return false;
    }

    private void emptyOperation(String name){
        switch (name){
            case "name" :{
                new AlertWindow("昵称不能为空!").display(ConnectionWindow.getConStage(),false);
                break;
            }
            case "port" :{

            }
            case "ip" :{
                new AlertWindow("ip地址不能为空!").display(ConnectionWindow.getConStage(),false);
                break;
            }

        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Status.buildServerProperty().addListener((event) -> {
            if(Status.buildServerProperty().get()){
                connectBtn.setDisable(false);
                buildBtn.setDisable(false);
            }else{
                connectBtn.setDisable(true);
                buildBtn.setDisable(true);
            }
        });
    }
}
