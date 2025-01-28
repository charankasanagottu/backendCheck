package com.kakz.tradeIn.service;

import com.kakz.tradeIn.model.Coin;
import com.kakz.tradeIn.model.User;
import com.kakz.tradeIn.model.Watchlist;

public interface WatchlistService {
    Watchlist findUserWatchlist(Long userId) throws Exception;

    Watchlist createWatchList(User user);

    Watchlist findById(Long id) throws Exception;

    Coin addItemToWatchlist(Coin coin, User user) throws Exception;
}
