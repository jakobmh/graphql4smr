package graphql4smr.lib;

import java.util.concurrent.ThreadLocalRandom;

public class SleepUtil {

    public static void delaystatic(){
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public static void delayrandom(){
        int min = 10;
        int max = 50;
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(min, max + 1));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
