package main.Utils;

/**
 * Created by lin on 2017/3/16/0016.
 * 链表实现栈
 */

import java.util.LinkedList;

public class Stack<T> {
    private LinkedList<T> storage = new LinkedList<>();

    public void push(T v) {
        storage.addFirst(v);
    }

    public void clear(){
        storage.clear();
    }

    public T peek() {
        return storage.getFirst();
    }

    public T pop() {
        return storage.removeFirst();
    }

    public int size(){
        return storage.size();
    }

    public boolean isEmpty() {
        return storage.isEmpty();
    }
}