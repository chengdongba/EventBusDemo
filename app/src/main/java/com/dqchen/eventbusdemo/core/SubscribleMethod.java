package com.dqchen.eventbusdemo.core;

import java.lang.reflect.Method;

public class SubscribleMethod {
    private Method method;//方法
    private Class<?> eventType;//事件类型
    private DnThreadMode threadMode;//线程类型

    public SubscribleMethod(Method method, Class<?> eventType, DnThreadMode threadMode) {
        this.method = method;
        this.eventType = eventType;
        this.threadMode = threadMode;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public void setEventType(Class<?> eventType) {
        this.eventType = eventType;
    }

    public DnThreadMode getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(DnThreadMode threadMode) {
        this.threadMode = threadMode;
    }
}
