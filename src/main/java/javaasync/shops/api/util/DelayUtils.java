package javaasync.shops.api.util;

import java.util.Random;

public class DelayUtils {

    public static boolean RANDOM_DELAY_FLAG = false;

    private static Random r = new Random();

    public static void delay() {
        try {
            if(!RANDOM_DELAY_FLAG) {
                Thread.sleep(1000L);
            } else {
                Thread.sleep(500 + r.nextInt(2000));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
