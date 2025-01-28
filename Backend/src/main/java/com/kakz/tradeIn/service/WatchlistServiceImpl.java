package com.kakz.tradeIn.service;

import com.kakz.tradeIn.model.Coin;
import com.kakz.tradeIn.model.User;
import com.kakz.tradeIn.model.Watchlist;
import com.kakz.tradeIn.repository.WatchlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing watchlists.
 * This class provides methods for creating, retrieving, and updating watchlists,
 * as well as adding or removing items from a user's watchlist.
 */
@Service
public class WatchlistServiceImpl implements WatchlistService {

    /**
     * Repository interface for managing Watchlist entity instances.
     * Provides methods to perform CRUD operations and query the Watchlist database.
     */
    @Autowired
    private WatchlistRepository  watchlistRepository;
    /**
     * Finds the watchlist for a specific user based on their user ID.
     *
     * @param userId the ID of the user whose watchlist is to be retrieved
     * @return the watchlist associated with the specified user
     * @throws Exception if the watchlist is not found for the given user ID
     */
    @Override
    public Watchlist findUserWatchlist(Long userId) throws Exception {
        Watchlist watchlist = watchlistRepository.findByUserId(userId);
        if(watchlist==null){
            throw new Exception("watchlist is not found");
//            return createWatchList(user);
        }
        return watchlist;
    }

    /**
     * Creates a new watchlist for a given user and saves it to the repository.
     *
     * @param user The user for whom the watchlist is to be created.
     * @return The created watchlist associated with the given user.
     */
    @Override
    public Watchlist createWatchList(User user) {
        Watchlist watchlist = new Watchlist();
        watchlist.setUser(user);

        return watchlistRepository.save(watchlist);
    }

    /**
     * Retrieves a Watchlist entity by its unique identifier.
     *
     * @param id the unique identifier of the Watchlist to be retrieved.
     * @return the Watchlist entity associated with the given id.
     * @throws Exception if a Watchlist with the specified id is not found.
     */
    @Override
    public Watchlist findById(Long id) throws Exception {

        return watchlistRepository
                .findById(id)
                .orElseThrow(
                        ()-> new Exception("Watchlist not found"));
    }

    /**
     * Adds a coin to the user's watchlist if it is not already present.
     * If the coin is already in the watchlist, it is removed.
     *
     * @param coin the coin to be added or removed from the watchlist
     * @param user the user whose watchlist is being modified
     * @return the coin that was added or removed
     * @throws Exception if the user's watchlist cannot be found
     */
    @Override
    public Coin addItemToWatchlist(Coin coin, User user) throws Exception {
        Watchlist watchlist = findUserWatchlist(user.getId());
        if(watchlist.getCoinList().contains(coin)){
            watchlist.getCoinList().remove(coin);
        }
        else{
            watchlist.getCoinList().add(coin);
        }
        watchlistRepository.save(watchlist);
        return coin;
    }
}
