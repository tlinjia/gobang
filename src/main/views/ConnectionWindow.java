package main.views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * 客户端处理
 * Created by lin on 2017/3/8/0008.
 */
public class ConnectionWindow {

    private static ConnectionWindow window;

    private static Stage conStage;

    private ConnectionWindow(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/connection.fxml"));
            conStage = new Stage();
            conStage.initModality(Modality.WINDOW_MODAL);
            conStage.initOwner(Main.getPrimaryStage());
            conStage.setScene(new Scene(root));
            conStage.setResizable(false);
            conStage.initStyle(StageStyle.UTILITY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ConnectionWindow getConnectionWindow(){
        if(window == null){
            return new ConnectionWindow();
        }
        return window;
    }

    public void dispaly() {
            conStage.show();
    }


    public static Stage getConStage(){
        if(conStage == null){
            new ConnectionWindow();
        }
        return conStage;
    }
}
