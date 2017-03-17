package main.views;
/**
 * Created by lin on 2017/3/5/0005.
 * 主窗口
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.Socket.Connection;
import main.Utils.Status;

import java.io.IOException;


public class Main extends Application {

    private static Stage primaryStage;

    private static FXMLLoader loader;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        try {
            loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("Gobang");
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.setOnCloseRequest((event)->{
                Connection connection = Status.getConnection();
                if(connection != null) connection.disconnection();
                System.exit(0);
            });
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage(){
        return primaryStage;
    }

    public static FXMLLoader getLoader(){return loader;}

}
