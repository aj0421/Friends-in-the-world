package com.example.friends_in_the_world.Network;

import java.util.LinkedList;

public class Buffer<T> {
    private LinkedList<T> buffer = new LinkedList<T>();

    public synchronized void put(T element) {
        buffer.addLast(element);
        notifyAll();
    }

    public synchronized T get() throws InterruptedException {
        while (buffer.isEmpty()) {
            wait();
        }
        return buffer.removeFirst();
    }
}