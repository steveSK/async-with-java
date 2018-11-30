package javaasync.shops.api.model;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static javaasync.shops.api.util.DelayUtils.delay;

public class Shop {


    private final String shopName;
    private final Random random = new Random();


    public Shop(String shopName){
        this.shopName = shopName;
    }

    public String getPrice(String product) {
        double price = calculatePrice(product);
        Discount.Code code = Discount.Code.values()[
                random.nextInt(Discount.Code.values().length)];
        return String.format("%s:%.2f:%s", shopName, price, code);
    }

    private double calculatePrice(String product) {
        Random r = new Random();
        delay();
        return r.nextDouble() * product.charAt(0) + product.charAt(1);
    }

    public String getShopName() {
        return shopName;
    }


}
