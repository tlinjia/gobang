package main.views;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 封装确认框
 * Created by lin on 2017/3/14/0014.
 */
public class ConfirmWindow {
    private String message;

    public ConfirmWindow(String message) {
        this.message = message;
    }

    /**
     *
     * @param parent
     * 父窗口
     * @return
     * 是否确认
     */
    public boolean display(Stage parent){
        Stage confirmStage = new Stage();
        final boolean[] result = {false};

        Label label = new Label(message);
        label.setWrapText(true);
        label.setMaxWidth(200);
        label.setLayoutX(55);
        label.setLayoutY(45);

        Button button1 = new Button("确定");
        Button button2 = new Button("取消");
        button1.setOnAction((event) ->{
            result[0] = true;
            confirmStage.hide();
        });
        button2.setOnAction((event) ->{
            result[0] = false;
            confirmStage.hide();
        });
        button1.setLayoutX(75);
        button1.setLayoutY(70);
        button2.setLayoutX(145);
        button2.setLayoutY(70);

        AnchorPane layout = new AnchorPane();
        layout.getChildren().addAll(label,button1,button2);
        layout.setMinHeight(110);
        layout.setMinWidth(250);

        confirmStage.setScene(new Scene(layout));
        confirmStage.initModality(Modality.WINDOW_MODAL);
        confirmStage.initOwner(parent);
        confirmStage.setResizable(false);
        confirmStage.initStyle(StageStyle.UNDECORATED);

        confirmStage.setX(parent.getX() + ((parent.getWidth() - 200) / 2));
        confirmStage.setY(parent.getY() + ((parent.getHeight() - 100) / 2));
        confirmStage.showAndWait();
        return result[0];
    }
}
