package com.kakz.tradeIn.service;

import com.kakz.tradeIn.model.Coin;

import java.util.List;

public interface CoinService {
    List<Coin> getCoinList(int page) throws Exception;
    String getMarketChart(String coinId, int days) throws Exception;
    String getCoinDetails(String coinId) throws Exception;
    Coin findById(String coinId) throws Exception;
    String searchCoin(String keyWord);
    String getTop50CoinByMarketCap() throws Exception;
    String getTradingCoins() throws Exception;

}
