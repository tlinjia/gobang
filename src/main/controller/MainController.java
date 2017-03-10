package main.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import main.Utils.Status;
import main.views.ConnectionWindow;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 主界面控制器
 * Created by lin on 2017/3/6/0006.
 */
public class MainController implements Initializable {
    public static MainController mainController = new MainController();
    @FXML
    private TextArea chatArea;

    @FXML
    private TextField chatField;

    @FXML
    private Button connectBtn, disconnectBtn, readyBtn, compromiseBtn, backBtn, surrenderBtn, sendBtn;

    @FXML
    private Label count, win, winRate, otherName;

    @FXML
    private void handlerConnection() {
        ConnectionWindow.getConnectionWindow().dispaly();
    }

    @FXML
    private void handlerDisConnection(){
        appendText("asd");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chatArea.setWrapText(true);
        chatArea.positionCaret(chatArea.getText().length());
        disconnectBtn.setDisable(true);
        readyBtn.setDisable(true);
        compromiseBtn.setDisable(true);
        backBtn.setDisable(true);
        surrenderBtn.setDisable(true);
        sendBtn.setDisable(true);
        Status.beginProperty().addListener((event) -> {
            if (Status.isBegin()) {
                readyBtn.setDisable(false);
                compromiseBtn.setDisable(false);
                backBtn.setDisable(false);
                surrenderBtn.setDisable(false);
            } else {
                readyBtn.setDisable(true);
                compromiseBtn.setDisable(true);
                backBtn.setDisable(true);
                surrenderBtn.setDisable(true);
            }
        });

        Status.connectedProperty().addListener((event) -> {
            if (Status.isConnected()) {
                ConnectionWindow.getConStage().hide();
                readyBtn.setDisable(false);
                connectBtn.setDisable(true);
                disconnectBtn.setDisable(false);
                sendBtn.setDisable(false);
            } else {
                connectBtn.setDisable(false);
                disconnectBtn.setDisable(true);
                sendBtn.setDisable(true);
            }
        });


        Status.countProperty().addListener((event) -> {
            count.setText(String.valueOf(Status.getCount()));
            winRate.setText(String.valueOf(Status.winProperty().getValue().floatValue() / Status.countProperty().getValue().floatValue()));
        });

        Status.winProperty().addListener((event) ->  win.setText(String.valueOf(Status.getWin())));

        Status.otherNameProperty().addListener((event) -> otherName.setText(Status.getOtherName()));

        Status.buildServerProperty().addListener((event) -> {
            if(Status.buildServerProperty().get()){
                connectBtn.setDisable(true);
                disconnectBtn.setDisable(false);
            }else{
                connectBtn.setDisable(false);
                disconnectBtn.setDisable(true);
            }
        });

    }

    public TextArea getChatArea() {
        return chatArea;
    }

    public TextField getChatField() {
        return chatField;
    }

    public void appendText(String s){
        chatArea.appendText(s + "\n");
    }
}
