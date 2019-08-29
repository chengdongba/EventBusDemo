package com.dqchen.eventbusdemo.core;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DnEventBus {

    public static DnEventBus instance = new DnEventBus();
    private Map<Object, List<SubscribleMethod>> cacheMap;
    private Handler handler;
    private ExecutorService executorService;

    public static DnEventBus getDefault() {
        return instance;
    }

    public DnEventBus() {
        cacheMap = new HashMap<>();
        handler = new Handler(Looper.getMainLooper());
        executorService = Executors.newCachedThreadPool();
    }

    /**
     * 注册
     *
     * @param subscriber
     */
    public void register(Object subscriber) {
        Class<?> aClass = subscriber.getClass();//获取注册的类型
        List<SubscribleMethod> subscribleMethods = cacheMap.get(subscriber);
        if (subscribleMethods == null) {
            subscribleMethods = getSubscribleMethods(subscriber);
            cacheMap.put(subscriber, subscribleMethods);
        }
    }

    /**
     * 遍历能够接收事件的方法
     *
     * @param subscriber
     * @return
     */
    private List<SubscribleMethod> getSubscribleMethods(Object subscriber) {
        List<SubscribleMethod> list = new ArrayList<>();
        Class<?> aClass = subscriber.getClass();
        while (aClass != null) {

            //判断aClass是不是系统的类,如果是系统的类,不需要继续找父类
            String name = aClass.getName();
            if (name.startsWith("java.") ||
                    name.startsWith("javax.") ||
                    name.startsWith("android.") ||
                    name.startsWith("androidx.")) {
                break;
            }

            //拿到类里面所有的方法,遍历
            Method[] declaredMethods = aClass.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                //是否有注释
                DnSubscriber annotation = declaredMethod.getAnnotation(DnSubscriber.class);
                if (annotation == null) {
                    continue;
                }

                //方法参数是否合法
                Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
                if (parameterTypes.length != 1) {
                    throw new RuntimeException("eventbuss只能接收一个参数");
                }

                DnThreadMode dnThreadMode = annotation.threadMode();
                SubscribleMethod method = new SubscribleMethod(declaredMethod, parameterTypes[0], dnThreadMode);
                list.add(method);
            }
            aClass = aClass.getSuperclass();
        }
        return list;
    }

    /**
     * 取消注册
     * @param subscriber
     */
    public void unregister(Object subscriber){
        Class<?> aClass = subscriber.getClass();
        List<SubscribleMethod> subscribleMethods = cacheMap.get(subscriber);
        if (subscribleMethods!=null){
            cacheMap.remove(subscriber);
        }
    }

    /**
     * 发送事件
     * @param event
     */
    public void post(final Object event){
        //从cacheMap中找到相应的接收事件的类,方法,然后在对应的线程执行该方法
        Set<Object> keySet = cacheMap.keySet();
        Iterator<Object> iterator = keySet.iterator();
        while (iterator.hasNext()){
            final Object next = iterator.next();//拿到注册类
            List<SubscribleMethod> subscribleMethods = cacheMap.get(next);
            for (final SubscribleMethod subscribleMethod : subscribleMethods) {
                if (subscribleMethod.getEventType().isAssignableFrom(event.getClass())){
                    switch (subscribleMethod.getThreadMode()){
                        case MAIN:
                            if (Looper.myLooper()==Looper.getMainLooper()){
                                invoke(subscribleMethod,next,event);
                            }else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscribleMethod,next,event);
                                    }
                                });
                            }
                            break;
                        case ASYNC:
                            if (Looper.myLooper()==Looper.getMainLooper()){
                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscribleMethod,next,event);
                                    }
                                });
                            }else {
                                invoke(subscribleMethod,next,event);
                            }
                            break;
                        case POSTING:
                            break;
                    }
                }
            }
        }
    }

    private void invoke(SubscribleMethod subscribleMethod, Object next, Object event) {
        Method method = subscribleMethod.getMethod();
        try {
            method.invoke(next,event);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
