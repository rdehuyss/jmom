package org.jmom.core.model.interfacing;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

public enum HardwareDependency {

    RXTX_COMM_PORT;

    private static Map<HardwareDependency, CountDownLatch> dependencyMap = Maps.newHashMap();


    public static void waitAndTakeLock(HardwareDependency dependency) {
        synchronized (dependency) {
            System.out.println("HardwareDependency: Requested lock for " + dependency.name());
            if (dependencyMap.containsKey(dependency)) {
                try {
                    dependencyMap.get(dependency).await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            dependencyMap.put(dependency, new CountDownLatch(1));
            System.out.println("HardwareDependency: Took lock for " + dependency.name());
        }
    }

    public static void finishLock(HardwareDependency dependency) {
        System.out.println("HardwareDependency: Released lock for " + dependency.name());
        CountDownLatch remove = dependencyMap.remove(dependency);
        remove.countDown();
    }
}
