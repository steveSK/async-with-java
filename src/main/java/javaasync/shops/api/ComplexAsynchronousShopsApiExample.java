package javaasync.shops.api;



import javaasync.shops.api.model.Discount;
import javaasync.shops.api.model.ExchangeEuroService;
import javaasync.shops.api.model.Quote;
import javaasync.shops.api.model.Shop;
import javaasync.shops.api.util.DelayUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static javaasync.shops.api.util.MethodUtils.methodPerformanceCheck;

public class ComplexAsynchronousShopsApiExample {


    static {
        DelayUtils.RANDOM_DELAY_FLAG = true;
    }

    private final ExchangeEuroService exchangeEuroService = new ExchangeEuroService();
    private final static String MY_COOL_PHONE = "MyPhone27S";


    private final List<Shop> shops = List.of(
            new Shop("BestPrice"),
            new Shop("LetsSaveBig"),
            new Shop("MyFavoriteShop"),
            new Shop("SuperShop"),
            new Shop("TheCheapestShop"));


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
                .map(shop -> shop.getPrice(product))
                .map(Quote::parse)
                .map(Discount::applyDiscount)
                .collect(toList());
    }

    public List<String> findPricesAsync(String product){
        List<CompletableFuture<String>> completableFutures = shops.stream().map(
                shop -> CompletableFuture.supplyAsync(() -> shop.getPrice(product),executor))
                .map(future -> future.thenApply(Quote::parse))
                .map(future -> future.thenCompose(quote -> CompletableFuture.supplyAsync(() -> Discount.applyDiscount(quote),executor)))
                .collect(toList());

        return completableFutures.stream()
                .map(CompletableFuture::join)
                .collect(toList());
    }

    public List<Double> findPricesParallelInUSD(String product) {
        List<CompletableFuture<Double>> completableFutures = shops.stream().map(
                shop -> CompletableFuture.supplyAsync(() -> shop.getPrice(product),executor)
                        .thenCombineAsync(CompletableFuture
                                .supplyAsync(() ->
                                        exchangeEuroService.getRate(ExchangeEuroService.Currency.USD)),(price,rate) -> rate * Integer.valueOf(price)))
                .collect(Collectors.toList());

        return completableFutures.stream()
                .map(CompletableFuture::join)
                .collect(toList());
    }

    public Stream<CompletableFuture<String>> findPricesStream(String product) {
        return shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(
                        () -> shop.getPrice(product), executor))
                .map(future -> future.thenApply(Quote::parse))
                .map(future -> future.thenCompose(quote ->
                        CompletableFuture.supplyAsync(
                                () -> Discount.applyDiscount(quote), executor)));
    }



    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        ComplexAsynchronousShopsApiExample casae = new ComplexAsynchronousShopsApiExample();


        System.out.println("execute sequentially");
        methodPerformanceCheck(casae.getClass().getMethod("findPrices",String.class),casae,new String[]{MY_COOL_PHONE});

        System.out.println("execute concurently");
        methodPerformanceCheck(casae.getClass().getMethod("findPricesAsync",String.class),casae,new String[]{MY_COOL_PHONE});

        System.out.println("combine two async operations");
        System.out.println(casae.findPricesParallelInUSD(MY_COOL_PHONE));

        System.out.println("execute concurently with accept");
        long start = System.nanoTime();
        CompletableFuture[] futures = casae.findPricesStream(MY_COOL_PHONE)
                .map(f -> f.thenAccept(
                        s -> System.out.println(s + " (done in " +
                                ((System.nanoTime() - start) / 1_000_000) + " msecs)")))
                .toArray(size -> new CompletableFuture[size]);
        CompletableFuture.allOf(futures).join();
        System.out.println("All shops have now responded in "
                + ((System.nanoTime() - start) / 1_000_000) + " msecs");

    }






}
