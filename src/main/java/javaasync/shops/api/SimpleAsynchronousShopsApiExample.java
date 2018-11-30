package javaasync.shops.api;


import javaasync.shops.api.model.Shop;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static javaasync.shops.api.util.MethodUtils.methodPerformanceCheck;

public class SimpleAsynchronousShopsApiExample {


    private final static String MY_COOL_PHONE = "MyPhone27S";

    private final List<Shop> shops = List.of(
        new Shop("BestPrice"),
        new Shop("LetsSaveBig"),
        new Shop("MyFavoriteShop"),
        new Shop("SuperShop"),
        new Shop("TheCheapestShop"),
        new Shop("Amazon"));


    private final Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100),
                    new ThreadFactory() {
                        public Thread newThread(Runnable r) {
                            Thread t = new Thread(r);
                            t.setDaemon(true);
                            return t;
                        }
                    });

    public List<String> findPrices(String product) {
        return shops.stream()
                .map(shop -> String.format("%s price is %s",
                        shop.getShopName(), shop.getPrice(product)))
                .collect(toList());
    }


    public List<String> findPricesParallel(String product) {
        return shops.parallelStream()
                .map(shop -> String.format("%s price is %s",
                        shop.getShopName(), shop.getPrice(product)))
                .collect(toList());
    }

    public List<String> findPricesAsync(String product) {
        List<CompletableFuture<String>> priceFutures = shops.stream()
                .map( shop -> CompletableFuture.supplyAsync(
                () -> shop.getShopName() + " price is " + shop.getPrice(product),executor))
                .collect(Collectors.toList());

        return priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(toList());
    }




    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        SimpleAsynchronousShopsApiExample asae = new SimpleAsynchronousShopsApiExample();


        System.out.println("execute sequentially");
        methodPerformanceCheck(asae.getClass().getMethod("findPrices",String.class),asae,new String[]{MY_COOL_PHONE});


        System.out.println("execute in parallel with streams");
        methodPerformanceCheck(asae.getClass().getMethod("findPricesParallel",String.class),asae,new String[]{MY_COOL_PHONE});

        System.out.println("execute concurently with completable futures");
        methodPerformanceCheck(asae.getClass().getMethod("findPricesAsync",String.class),asae,new String[]{MY_COOL_PHONE});
    }

}
