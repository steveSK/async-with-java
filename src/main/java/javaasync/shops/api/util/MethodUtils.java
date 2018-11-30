package javaasync.shops.api.util;

import javaasync.shops.api.ComplexAsynchronousShopsApiExample;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodUtils {

    public static void methodPerformanceCheck(Method method, Object invoker, Object[] args) throws InvocationTargetException, IllegalAccessException {
        long start0 = System.nanoTime();
        System.out.println(method.invoke(invoker,args));
        long duration0 = (System.nanoTime() - start0) / 1_000_000;
        System.out.println("Done in " + duration0 + " msecs");

    }
}
