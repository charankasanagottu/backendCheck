package com.kakz.tradeIn.controller;

import com.kakz.tradeIn.model.Coin;
import com.kakz.tradeIn.model.User;
import com.kakz.tradeIn.model.Watchlist;
import com.kakz.tradeIn.service.CoinService;
import com.kakz.tradeIn.service.UserService;
import com.kakz.tradeIn.service.WatchlistService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {
    private final WatchlistService watchlistService;
    private final UserService userService;

    @Autowired
    private CoinService coinService;

    @Autowired
    public WatchlistController(WatchlistService watchlistService,
                               UserService userService) {
        this.watchlistService = watchlistService;
        this.userService=userService;
    }

    /**
     * Retrieves the user's watchlist based on the provided JWT token.
     *
     * @param jwt the JSON Web Token used to authenticate and identify the user.
     * @return ResponseEntity containing the user's watchlist.
     * @throws Exception if there is any issue in fetching the user's profile or watchlist.
     */
    @GetMapping("/user")
    @Operation(summary = "Retrieves the user's watchlist based on the provided JWT token'")
    public ResponseEntity<Watchlist> getUserWatchlist(
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user=userService.findUserProfileByJwt(jwt);
        Watchlist watchlist = watchlistService.findUserWatchlist(user.getId());
        return ResponseEntity.ok(watchlist);

    }

//    @PostMapping("/create")
//    public ResponseEntity<Watchlist> createWatchlist(
//            @RequestHeader("Authorization") String jwt) throws  Exception {
//        User user=userService.findUserProfileByJwt(jwt);
//        Watchlist createdWatchlist = watchlistService.createWatchList(user);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdWatchlist);
//    }

    /**
     * Retrieves a watchlist by its identifier.
     *
     * @param watchlistId the identifier of the watchlist to retrieve.
     * @return ResponseEntity containing the watchlist.
     * @throws Exception if the watchlist cannot be found or any other issue occurs.
     */
    @GetMapping("/{watchlistId}")
    @Operation(summary = "Retrieves a watchlist by its identifier")
    public ResponseEntity<Watchlist> getWatchlistById(
            @PathVariable Long watchlistId) throws Exception {

        Watchlist watchlist = watchlistService.findById(watchlistId);
        return ResponseEntity.ok(watchlist);

    }

    /**
     * Adds a cryptocurrency coin to the authenticated user's watchlist.
     *
     * @param jwt the JSON Web Token used to authenticate and identify the user.
     * @param coinId the identifier of the coin to add to the watchlist.
     * @return ResponseEntity containing the added Coin.
     * @throws Exception if there is any issue in retrieving the user profile, finding the coin, or adding the coin to the watchlist.
     */
    @PatchMapping("/add/coin/{coinId}")
    @Operation(summary = "Adds  coin to the authenticated user's watchlist'")
    public ResponseEntity<Coin> addItemToWatchlist(
            @RequestHeader("Authorization") String jwt,
            @PathVariable String coinId) throws Exception {


        User user=userService.findUserProfileByJwt(jwt);
        Coin coin=coinService.findById(coinId);
        Coin addedCoin = watchlistService.addItemToWatchlist(coin, user);
        return ResponseEntity.ok(addedCoin);

    }
}

