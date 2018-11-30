package javaasync.shops.api.model;

import java.util.HashMap;
import java.util.Map;

public class ExchangeEuroService {

    private Map<Currency,Double> rateMap = new HashMap<>();


    public ExchangeEuroService(){
        rateMap.put(Currency.USD,1.18);
        rateMap.put(Currency.YEN,1245.0);
        rateMap.put(Currency.CZK,25.8);
    }


    public enum Currency{
        USD,
        YEN,
        CZK
    }


    public Double getRate(Currency currency) {
        return rateMap.get(currency);
    }

}
