package com.ioyouyun.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bill on 2016/7/31.
 */
public class Subject {

    private Subject() {
    }

    // 饿汉式单例
    public static Subject getInstance() {
        return Holder.instance;
    }

    static class Holder {
        private static Subject instance = new Subject();
    }

    private List<Observer.CustomObserver> groupListObservers = new ArrayList<>();

    public void addGroupListObserver(Observer.CustomObserver observer) {
        if (!groupListObservers.contains(observer))
            groupListObservers.add(observer);
    }

    public void removeGroupListObserver(Observer.CustomObserver observer) {
        if (groupListObservers.contains(observer))
            groupListObservers.remove(observer);
    }

    public void notifyGroupListObservers() {
        for (Observer.CustomObserver observer : groupListObservers) {
            observer.update();
        }
    }
}
