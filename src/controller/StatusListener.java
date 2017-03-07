package controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.LinkedList;

/**
 * 保存整个游戏状态
 * Created by lin on 2017/3/7/0007.
 */
public class StatusListener {

    public final int BLACK_CHESS = 1; //黑棋为1

    public final int WHITE_CHESS = -1; //白棋为-1

    public static String name;  //自己的昵称

    public static String otherName; //对方的昵称

    private static boolean connect = false; //是否建立连接标志

    private static BooleanProperty connectProperty;

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

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        StatusListener.name = name;
    }

    public static String getOtherName() {
        return otherName;
    }

    public static void setOtherName(String otherName) {
        StatusListener.otherName = otherName;
    }

    public static boolean isConnect() {
        if(connectProperty != null){
            return connectProperty.get();
        }
        return connect;
    }

    public static void setConnect(boolean connect) {
        if(connectProperty != null){
            connectProperty.set(connect);
        }
        StatusListener.connect = connect;
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
        StatusListener.begin = begin;
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
        StatusListener.switchFlag = switchFlag;
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
        StatusListener.count = count;
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
        StatusListener.win = win;
    }

    public static int[] getChessboard() {
        return chessboard;
    }

    public static Stack<Integer> getPast() {
        return past;
    }


    public final BooleanProperty connectProperty() {
        if(connectProperty == null){
            connectProperty = new SimpleBooleanProperty(connect);
        }
        return connectProperty;
    }


    public final BooleanProperty beginProperty() {
        if(beginProperty == null){
            beginProperty = new SimpleBooleanProperty(begin);
        }
        return beginProperty;
    }


    public final BooleanProperty switchFlagProperty() {
        if(switchFlagProperty == null){
            switchFlagProperty = new SimpleBooleanProperty(switchFlag);
        }
        return switchFlagProperty;
    }

    public final IntegerProperty countProperty() {
        if(countProperty == null){
            countProperty = new SimpleIntegerProperty(count);
        }
        return countProperty;
    }


    public final IntegerProperty winProperty() {
        if(winProperty == null){
            winProperty = new SimpleIntegerProperty(win);
        }
        return winProperty;
    }

    static class Stack<T> {
        private LinkedList<T> storage = new LinkedList<T>();

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
