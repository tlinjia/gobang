package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import views.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by lin on 2017/3/6/0006.
 */
public class MainController implements Initializable{
    @FXML
    private TextArea chatArea;

    @FXML
    private Button connectBtn,disconnectBtn,readyBtn,compromiseBtn,backBtn,surrenderBtn,sendBtn;

    @FXML
    private void handlerConnection(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/connection.fxml"));
            Stage conStage = new Stage();
            conStage.initModality(Modality.WINDOW_MODAL);
            conStage.initOwner(Main.getPrimaryStage());
            conStage.setScene(new Scene(root));
            conStage.setResizable(false);
            conStage.initStyle(StageStyle.UTILITY);
            conStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    }
}
