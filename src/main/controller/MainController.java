package main.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import main.Utils.*;
import main.views.AlertWindow;
import main.views.ConfirmWindow;
import main.views.ConnectionWindow;
import main.views.Main;
import org.apache.commons.lang.StringEscapeUtils;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 主界面控制器
 * Created by lin on 2017/3/6/0006.
 */
public class MainController implements Initializable {
    private HashMap<Integer, Integer> enabledMap; //保存全局可落子的区域坐标,KEY---坐标,VALUE---当前行/列的第几个位置
    private ArrayList<Coordinate> disabledList; //保存已落子的坐标，key -- x, value -- y;
    private main.Utils.Stack<Integer> past = Status.getPast();


    private HashMap<Integer,Circle> chessMap = new HashMap<>(255); //保存棋盘中的棋子图案及对应索引
    private int[] board = Status.getChessboard(); //获取棋盘
    @FXML
    private TextArea chatArea;

    @FXML
    private TextField chatField;

    @FXML
    private Button connectBtn, disconnectBtn, readyBtn, compromiseBtn, backBtn, surrenderBtn, sendBtn;

    @FXML
    private Label count, win, winRate, otherName;

    @FXML
    private Pane chessboard;

    @FXML
    private void handlerConnection() {
        ConnectionWindow.getConnectionWindow().dispaly();
    }

    @FXML
    private void handlerDisConnection() {
        Status.getConnection().disconnection();
        new AlertWindow("连接已断开!\n请重新建立连接！").display(Main.getPrimaryStage(), false);
    }

    @FXML
    private void handlerReady() {
        Status.readyProperty().set(!Status.readyProperty().get());

    }

    @FXML
    private void handlerCompromise() {
        Status.getConnection().sendMessage("@compromise:request");
        Timer timer = new Timer(true);
        int[] i = {10};
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (!Status.isBegin()) {
                        compromiseBtn.setDisable(true);
                        compromiseBtn.setText("和棋");
                        timer.cancel();
                    } else if (i[0] > 0) {
                        compromiseBtn.setDisable(true);
                        compromiseBtn.setText("和棋(" + i[0] + ")");
                        i[0]--;
                    } else {
                        compromiseBtn.setDisable(false);
                        compromiseBtn.setText("和棋");
                        timer.cancel();
                    }
                });
            }
        }, 0, 1000);
    }

    @FXML
    private void surrenderHandler() {
        if (new ConfirmWindow("确定要认输吗?").display(Main.getPrimaryStage())) {
            Status.getConnection().sendMessage("@surrender:surrender");
            gameOver(!Status.isChessFlag());
        }
    }

    @FXML
    private void backHandler(){
        Status.getConnection().sendMessage("@back:request");
        Timer timer = new Timer(true);
        int[] i = {10};
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (Status.isChessFlag() == Status.isSwitchFlag()) {
                        backBtn.setDisable(true);
                        backBtn.setText("悔棋");
                        timer.cancel();
                    } else if (i[0] > 0) {
                        backBtn.setDisable(true);
                        backBtn.setText("悔棋(" + i[0] + ")");
                        i[0]--;
                    } else {
                        backBtn.setDisable(false);
                        backBtn.setText("悔棋");
                        timer.cancel();
                    }
                });
            }
        }, 0, 1000);
    }


    @FXML
    private void boardMouseMoved(MouseEvent event) {
        if (Status.isBegin() && (Status.isSwitchFlag() == Status.isChessFlag()) && enabledMap.containsKey((int) event.getX())
                && enabledMap.containsKey((int) event.getY())                                      // 游戏已开始，且是己方落子
                && !disabledList.contains(new Coordinate((int) event.getX(), (int) event.getY()))) { // 当鼠标在可落子的区域切该区域没有其他旗子
            chessboard.setCursor(Cursor.CLOSED_HAND);
        } else {
            chessboard.setCursor(Cursor.DEFAULT);
        }
    }

    @FXML
    private void boardMouseClicked(MouseEvent event) {
        if (chessboard.getCursor().equals(Cursor.CLOSED_HAND)) {
            int index = enabledMap.get((int) event.getY()) * 15 + enabledMap.get((int) event.getX()); //棋盘坐标
            board[index] = Status.isChessFlag() ? Status.BLACK_CHESS : Status.WHITE_CHESS;
            Status.getConnection().sendMessage("@move:" + index + "," + board[index]);
            past.push(index);
            Status.setSwitchFlag(!Status.isSwitchFlag());
            handleDisabledList(index,true);
            chessboard.setCursor(Cursor.DEFAULT);
            repaint();
            if (isOver(index)) {
                gameOver(Status.isChessFlag());
            }
        }
    }

    @FXML
    private void sendHandler(){
        String text = chatField.getText().trim();
        if(text.length() > Status.MESSAGE_MAX_LENGTH){
            new AlertWindow("内容过长，请勿超过"+Status.MESSAGE_MAX_LENGTH+"字符。").display(Main.getPrimaryStage(),false);
        }
        else
            if(text.length() > 0){
            text = StringEscapeUtils.escapeJava(text);
            Status.getConnection().sendMessage("@msg:"+text);
            appendText(String.join(":",Status.getName(),StringEscapeUtils.unescapeJava(text)));
            chatField.setText("");
        }
    }

    @FXML
    private void chatFieldListener(KeyEvent event){  //监听回车键
        if(event.getCode().equals(KeyCode.ENTER)){
            if(!sendBtn.isDisable()){
                sendHandler();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        enabledMap = new HashMap<>();
        disabledList = new ArrayList<>();
        for (int i = 1; i < 30; i += 2) {
            for (int j = 18 * i - 8; j <= 18 * i + 8; j++) {
                enabledMap.put(j, i / 2);
            }
        }
        chatArea.setWrapText(true);
        chatArea.positionCaret(chatArea.getText().length());
        disconnectBtn.setDisable(true);
        readyBtn.setDisable(true);
        compromiseBtn.setDisable(true);
        backBtn.setDisable(true);
        surrenderBtn.setDisable(true);
        sendBtn.setDisable(true);

        Status.connectedProperty().addListener((event) -> {
            if (Status.isConnected()) {
                Status.winProperty().set(0);
                Status.countProperty().set(0);
                ConnectionWindow.getConStage().hide();
                readyBtn.setDisable(false);
                connectBtn.setDisable(true);
                disconnectBtn.setDisable(false);
                sendBtn.setDisable(false);
            } else {
                Status.beginProperty().set(false);
                Status.setReady(false);
                Status.setOtherReady(false);
                Status.setWin(0);
                Status.setCount(0);
                connectBtn.setDisable(false);
                disconnectBtn.setDisable(true);
                readyBtn.setDisable(true);
                sendBtn.setDisable(true);
                surrenderBtn.setDisable(true);
                backBtn.setDisable(true);
                compromiseBtn.setDisable(true);
                clearBoard();
            }
        });


        Status.countProperty().addListener((event) -> {
            count.setText(String.valueOf(Status.getCount()));
            if (Status.getCount() > 0) {
                float rate = Status.winProperty().getValue().floatValue() / Status.countProperty().getValue().floatValue();
                DecimalFormat decimalFormat = new DecimalFormat("##.##%");
                winRate.setText(decimalFormat.format(rate));
            } else
                winRate.setText("0");
        });

        Status.winProperty().addListener((event) -> {
            win.setText(String.valueOf(Status.getWin()));
        });

        Status.otherNameProperty().addListener((event) -> otherName.setText(Status.getOtherName()));

        Status.buildServerProperty().addListener((event) -> {
            if (Status.buildServerProperty().get()) {
                connectBtn.setDisable(true);
                disconnectBtn.setDisable(false);
            } else {
                connectBtn.setDisable(false);
                disconnectBtn.setDisable(true);
            }
        });

        Status.readyProperty().addListener((event) -> {
            if (Status.readyProperty().get()) {
                readyBtn.setText("取消准备");
                Status.getConnection().sendMessage("@ready:ready");
                Status.readyProperty().set(true);
                if (Status.otherReadyProperty().get()) {
                    Status.beginProperty().set(true);
                }
            } else {
                readyBtn.setText("准备");
                if (Status.getConnection() != null)
                    Status.getConnection().sendMessage("@ready:cancel");
                Status.readyProperty().set(false);
            }
        });

        Status.otherReadyProperty().addListener((event) -> {
            if (Status.otherReadyProperty().get()) {
                appendText(Status.otherNameProperty().get() + "已准备。");
            } else {
                if (Status.connectedProperty().get())
                    appendText(Status.otherNameProperty().get() + "取消准备。");
            }
        });

        Status.beginProperty().addListener((event) -> {
            if (Status.isBegin()) {
                readyBtn.setDisable(true);
                compromiseBtn.setDisable(false);
                backBtn.setDisable(true);
                surrenderBtn.setDisable(false);
                Status.switchFlagProperty().set(true);
                appendText("游戏开始。");
            } else {
                readyBtn.setDisable(false);
                Status.readyProperty().set(false);
                compromiseBtn.setDisable(true);
                backBtn.setDisable(true);
                surrenderBtn.setDisable(true);
            }
        });

        Status.switchFlagProperty().addListener((event) -> {
            if (Status.beginProperty().get() && (Status.switchFlagProperty().get() ^ Status.isChessFlag())) { //轮到对方轮次
                compromiseBtn.setDisable(false);
                if(past.isEmpty()){
                    backBtn.setDisable(true);
                }else{
                    backBtn.setDisable(false);
                }
            } else if (Status.beginProperty().get() && (Status.switchFlagProperty().get() == Status.isChessFlag())) { //轮到己方轮次
                compromiseBtn.setDisable(false);
                backBtn.setDisable(true);
            }
        });

    }


    public void appendText(String s) {
        chatArea.appendText(s + "\n");
    }

    public void repaint() {
        ImagePattern white = new ImagePattern(new Image("/image/white.jpg"));
        ImagePattern black = new ImagePattern(new Image("/image/black.jpg"));
        for (int i = 0; i < 225; i++) {
            if (board[i] == Status.WHITE_CHESS && !chessMap.containsKey(i)) {
                Circle circle = new Circle(14, white);
                circle.setLayoutX(18 + (i % 15) * 36);
                circle.setLayoutY(18 + (i / 15) * 36);
                chessboard.getChildren().add(circle);
                chessMap.put(i,circle);
            } else if (board[i] == Status.BLACK_CHESS && !chessMap.containsKey(i)) {
                Circle circle = new Circle(14, black);
                circle.setLayoutX(18 + (i % 15) * 36);
                circle.setLayoutY(18 + (i / 15) * 36);
                chessboard.getChildren().add(circle);
                chessMap.put(i,circle);
            }
        }
    }

    /**
     * 清空已落子区域
     */
    public void handleDisabledList() { //无参，清空
        disabledList.clear();
    }

    /**
     * 已落子区域处理
     * @param flag
     * true,添加坐标，false,删除坐标
     *
     * @param index
     * 添加/删除index到已落子区域
     */
    public void handleDisabledList(int index, boolean flag) { //带参，添加坐标
        int rows = index / 15;
        int columns = index % 15;
        for (int i = columns * 36 + 10; i <= columns * 36 + 26; i++) {
            for (int j = rows * 36 + 10; j <= rows * 36 + 26; j++) {
                if(flag) {
                    disabledList.add(new Coordinate(i, j));
                }
                else {
                    disabledList.remove(new Coordinate(i,j));
                }
            }
        }
    }

    /**
     * 游戏结束处理
     * 无参，平局
     */
    public void gameOver() { //无参，平局
        Status.countProperty().set(Status.countProperty().get() + 1);
        Status.beginProperty().set(false);
        Main.getLoader().<MainController>getController().appendText(Status.otherNameProperty().get() + "游戏结束：平局。");
        Status.setChessFlag(!Status.isChessFlag());
        new AlertWindow("游戏结束，平局。").display(Main.getPrimaryStage(), false);
        clearBoard();
        past.clear();
        chessMap.clear();
        handleDisabledList();
    }

    /**
     * 游戏结束处理
     *
     * @param winner 胜利方
     */
    public void gameOver(boolean winner) {
        if (winner == Status.isChessFlag()) {
            new AlertWindow("你赢得了胜利！").display(Main.getPrimaryStage(), false);
            Main.getLoader().<MainController>getController().appendText("你赢得了胜利。");
            Status.winProperty().set(Status.winProperty().get() + 1);
        } else {
            new AlertWindow("你输了！").display(Main.getPrimaryStage(), false);
            Main.getLoader().<MainController>getController().appendText("你输了。");
        }
        Status.countProperty().set(Status.countProperty().get() + 1);
        Status.setChessFlag(!Status.isChessFlag());
        Status.beginProperty().set(false);
        clearBoard();
        past.clear();
        chessMap.clear();
        handleDisabledList();
    }

    private void clearBoard() {  //清空棋盘
        for (int i = 0; i < 225; i++) {
            board[i] = 0;
        }
        chessboard.getChildren().clear();
    }

    /**
     * 判断游戏是否结束
     *
     * @param index 当前落子索引
     * @return
     */
    public boolean isOver(int index) {
        return levelSerach(index) || verticalSerach(index) || leftDiagonalSerach(index) || rightDiagonalSerach(index);
    }

    //横向查找,当前行内是否有落子颜色的五子连珠
    private boolean levelSerach(int index) {
        boolean result = false;
        int rows = index / 15;
        int flag = board[index];  //当前颜色
        int count = 0; // 连子个数
        for (int i = rows * 15; i < (rows + 1) * 15; i++) {  //rows * 15当前行的第一个元素
            if (board[i] == flag) {
                count++;
                if (count == 5) {
                    result = true;
                }
            } else {
                count = 0;
            }
        }
        return result;
    }

    //垂直查找
    private boolean verticalSerach(int index) {
        boolean result = false;
        int columns = index % 15;
        int flag = board[index];  //当前颜色
        int count = 0; // 连子个数
        for (int i = columns; i < 225; i += 15) {  //rows * 15当前行的第一个元素
            if (board[i] == flag) {
                count++;
                if (count == 5) {
                    result = true;
                }
            } else {
                count = 0;
            }
        }
        return result;
    }

    //自左向右斜向查找
    private boolean leftDiagonalSerach(int index) {
        boolean result = false;
        int rows = index / 15;
        int columns = index % 15;
        int initRows = rows >= columns ? rows - columns : 0;   //当前斜线初始点x  6
        int initColumns = rows >= columns ? 0 : columns - rows;//当前斜线初始点y  0
        int flag = board[index];  //当前颜色
        int count = 0; // 连子个数
        for (int i = initRows * 15 + initColumns; i <= (14 - initColumns) * 15 + initRows; i += 16) {
            //initrows * 15 斜线行的第一个元素
            //(15 - initColumns) * 15 + rows 斜线最后一个元素
            if (board[i] == flag) {
                count++;
                if (count == 5) {
                    result = true;
                }
            } else {
                count = 0;
            }
        }
        return result;
    }

    //自右向左斜向查找
    private boolean rightDiagonalSerach(int index) {
        boolean result = false;
        int rows = index / 15;
        int columns = index % 15;
        int initRows = rows + columns <= 14 ? 0 : rows + columns - 14;   //当前斜线初始点x
        int initColumns = rows + columns <= 14 ? columns + rows: 14;//当前斜线初始点y
        int flag = board[index];  //当前颜色
        int count = 0; // 连子个数
        for (int i = initRows * 15 + initColumns; i <= initColumns * 15 + initRows; i += 14) {
            //initrows * 15 斜线的第一个元素
            //initColumns * 15 + rows  斜线最后一个元素
            if (board[i] == flag) {
                count++;
                if (count == 5) {
                    result = true;
                }
            } else {
                count = 0;
            }
        }
        return result;
    }

    public Pane getChessboard() {
        return chessboard;
    }


    public HashMap<Integer, Circle> getChessMap() {
        return chessMap;
    }

    private class Coordinate {  //内部坐标类
        private int x, y;

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Coordinate)) return false;
            else if (x == ((Coordinate) obj).getX() && y == ((Coordinate) obj).getY()) {
                return true;
            }
            return false;
        }
    }
}
