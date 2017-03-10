package main.Utils;

import javafx.beans.property.*;
import main.Socket.Connection;

import java.util.LinkedList;

/**
 * 保存整个游戏状态
 * Created by lin on 2017/3/7/0007.
 */
public class Status {

    public final int BLACK_CHESS = 1; //黑棋为1

    public final int WHITE_CHESS = -1; //白棋为-1

    private static String name;  //自己的昵称

    private static String otherName; //对方的昵称

    private static SimpleStringProperty otherNameProperty;


    private static boolean connected = false; //是否建立连接标志

    private static BooleanProperty connectedProperty;

    private static boolean begin = false; //游戏是否开始标志

    private static BooleanProperty beginProperty;

    private static int[] chessboard = new int[225]; //棋盘状态，初始化为0,保存15*15个位置的状态

    private static boolean switchFlag = true; //轮转标志，true时黑棋下，false时白棋下

    private static BooleanProperty switchFlagProperty;

    private static Stack<Integer> past = new Stack<>(); //保存走过的步数，用于悔棋

    private static int count = 0; //保存总共盘数

    private static IntegerProperty countProperty;

    private static int win = 0; //保存赢的盘数

    private static IntegerProperty winProperty;

    private static BooleanProperty buildServerProperty;

    private static Connection connection;

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Status.name = name;
    }

    public static String getOtherName() {
        if(otherNameProperty != null){
            return otherNameProperty.get();
        }
        return otherName;
    }

    public static void setOtherName(String otherName) {
        Status.otherName = otherName;
    }

    public static boolean isConnected() {
        if(connectedProperty != null){
            return connectedProperty.get();
        }
        return connected;
    }

    public static void setConnected(boolean connected) {
        if(connectedProperty != null){
            connectedProperty.set(connected);
        }
        Status.connected = connected;
    }

    public static boolean isBegin() {
        if(beginProperty != null){
            return beginProperty.get();
        }
        return begin;
    }

    public static void setBegin(boolean begin) {
        if(beginProperty != null){
            beginProperty.set(begin);
        }
        Status.begin = begin;
    }

    public static boolean isSwitchFlag() {
        if(switchFlagProperty != null){
            return switchFlagProperty.get();
        }
        return switchFlag;
    }

    public static void setSwitchFlag(boolean switchFlag) {
        if(switchFlagProperty != null){
            switchFlagProperty.set(switchFlag);
        }
        Status.switchFlag = switchFlag;
    }

    public static int getCount() {
        if(countProperty != null){
            return countProperty.get();
        }
        return count;
    }

    public static void setCount(int count) {
        if(countProperty != null){
            countProperty.set(count);
        }
        Status.count = count;
    }

    public static int getWin() {
        if(winProperty != null){
            return winProperty.get();
        }
        return win;
    }

    public static void setWin(int win) {
        if(winProperty != null){
            winProperty.set(win);
        }
        Status.win = win;
    }

    public static int[] getChessboard() {
        return chessboard;
    }

    public static Stack<Integer> getPast() {
        return past;
    }


    public static final BooleanProperty connectedProperty() {
        if(connectedProperty == null){
            connectedProperty = new SimpleBooleanProperty(connected);
        }
        return connectedProperty;
    }


    public static final BooleanProperty beginProperty() {
        if(beginProperty == null){
            beginProperty = new SimpleBooleanProperty(begin);
        }
        return beginProperty;
    }


    public static final BooleanProperty switchFlagProperty() {
        if(switchFlagProperty == null){
            switchFlagProperty = new SimpleBooleanProperty(switchFlag);
        }
        return switchFlagProperty;
    }

    public static final IntegerProperty countProperty() {
        if(countProperty == null){
            countProperty = new SimpleIntegerProperty(count);
        }
        return countProperty;
    }


    public static final IntegerProperty winProperty() {
        if(winProperty == null){
            winProperty = new SimpleIntegerProperty(win);
        }
        return winProperty;
    }

    public static final StringProperty otherNameProperty(){
        if(otherNameProperty == null){
            otherNameProperty = new SimpleStringProperty(otherName);
        }
        return otherNameProperty;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection(Connection connection) {
        Status.connection = connection;
    }

    public static final BooleanProperty buildServerProperty(){
        if(buildServerProperty == null){
            buildServerProperty = new SimpleBooleanProperty();
        }
        return buildServerProperty;
    }

    static class Stack<T> {
        private LinkedList<T> storage = new LinkedList<>();

        public void push(T v) {
            storage.addFirst(v);
        }

        public T peek() {
            return storage.getFirst();
        }

        public T pop() {
            return storage.removeFirst();
        }

        public boolean isEmpty() {
            return storage.isEmpty();
        }
    }
}
