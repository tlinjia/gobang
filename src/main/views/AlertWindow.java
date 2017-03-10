package main.views;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 * 弹出框
 * Created by lin on 2017/3/8/0008.
 */
public class AlertWindow {
    private String message;

    public AlertWindow(String message) {
        this.message = message;
    }

    /**
     * 父窗口
     * @PARAM paren
     *
     * 关闭弹出框时是否隐藏父窗口
     * @PARAM shouldHideParen
     */
    public void display(Stage parent,boolean shouldHideParent) {
        Stage alertStage = new Stage();

        int rows = this.message.length() / 12; //一行大约12个字，用于输出居中

        Label label = new Label(message);
        label.setWrapText(true);
        label.setMaxWidth(200);
        label.setLayoutX(rows < 1 ? (200 - this.message.length() * 11) / 2 : 40);
        label.setLayoutY(20);
        label.setMaxWidth(120);

        Button button = new Button("确定");
        button.setOnAction((event) ->{
                    alertStage.close();
                    if(shouldHideParent) parent.hide() ;
        });
        button.setLayoutX(82);
        button.setLayoutY(rows  > 1 ? label.getLayoutY() + rows *12 + 30 : label.getLayoutY() + 30);

        AnchorPane layout = new AnchorPane();
        layout.getChildren().addAll(label,button);
        layout.setMinHeight(100);
        layout.setMinWidth(200);

        alertStage.setScene(new Scene(layout));

        alertStage.initModality(Modality.WINDOW_MODAL);
        alertStage.initOwner(parent);
        alertStage.setResizable(false);
        alertStage.initStyle(StageStyle.UNDECORATED);
        alertStage.show();
    }

}
