package com.kakz.tradeIn.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakz.tradeIn.model.Coin;
import com.kakz.tradeIn.service.CoinService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coins")
public class CoinController {
    @Autowired
    private CoinService coinService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Retrieves a list of coins, optionally paginated by the provided page number.
     *
     * @param page the page number for pagination, optional
     * @return a ResponseEntity containing a list of Coin objects and an HTTP status of ACCEPTED
     * @throws Exception if an error occurs while fetching the coin list
     */
    @GetMapping
    @Operation(summary = "Retrieves a list of coins, optionally paginated by the provided page number")
    public ResponseEntity<List<Coin>> getCoinList(@RequestParam(required = false, value = "page") int page) throws Exception {
        List<Coin> coins= coinService.getCoinList(page);
        return new ResponseEntity<>(coins, HttpStatus.ACCEPTED);
    }

    /**
     * Retrieves the market chart data for a specific coin over a specified number of days.
     *
     * @param coinId the unique identifier of the coin
     * @param days the number of days for which the market chart data is required
     * @return a ResponseEntity containing the market chart data in JSON format and an HTTP status of OK
     * @throws Exception if an error occurs while fetching the market chart data
     */
    @GetMapping("/{coinId}/chart")
    @Operation(summary = "Retrieves the market chart data for a specific coin over a specified number")
    ResponseEntity<JsonNode> getMarketChart(
            @PathVariable String coinId,
            @RequestParam("days") int days
    ) throws Exception{
        String response = coinService.getMarketChart(coinId,days);
        JsonNode jsonNode = objectMapper.readTree(response);
        return new ResponseEntity<>(jsonNode, HttpStatus.OK);
    }

    /**
     * Searches for coins matching the specified keyword.
     *
     * @param keyword the keyword used to search for coins
     * @return a ResponseEntity containing a JsonNode with search results and an HTTP status of OK
     * @throws JsonProcessingException if an error occurs while processing the JSON response
     */
    @GetMapping("/search")
    @Operation(summary = "Searches for coins matching the specified keyword")
    ResponseEntity<JsonNode> searchCoin(@RequestParam("q") String keyword) throws JsonProcessingException {
        String coin=coinService.searchCoin(keyword);
        JsonNode jsonNode = objectMapper.readTree(coin);

        return ResponseEntity.ok(jsonNode);

    }
    /**
     * Retrieves the top 50 coins by market capitalization rank.
     *
     * @return a ResponseEntity containing a JsonNode with the top 50 coins by market cap and an HTTP status of OK
     * @throws Exception if an error occurs while fetching the coins data
     */
    @GetMapping("/top50")
    @Operation(summary = " Retrieves the top 50 coins by market capitalization rank.")
    ResponseEntity<JsonNode> getTop50CoinByMarketCapRank() throws Exception {
        String coin=coinService.getTop50CoinByMarketCap();
        JsonNode jsonNode = objectMapper.readTree(coin);

        return ResponseEntity.ok(jsonNode);

    }

    /**
     * Retrieves the list of currently trending coins.
     *
     * @return a ResponseEntity containing a JsonNode with the list of trending coins and an HTTP status of OK
     * @throws Exception if an error occurs while fetching the trending coins data
     */
    @GetMapping("/trading")
    @Operation(summary = "Retrieves the list of currently trending coins.")
    ResponseEntity<JsonNode> getTreadingCoin() throws Exception {
        String coin=coinService.getTradingCoins();
        JsonNode jsonNode = objectMapper.readTree(coin);
        return ResponseEntity.ok(jsonNode);

    }

    /**
     * Retrieves details for a specific coin by its ID.
     *
     * @param coinId the unique identifier of the coin whose details are to be fetched
     * @return a ResponseEntity containing the coin details in JSON format and an HTTP status of OK
     * @throws Exception if an error occurs while fetching the coin details
     */
    @GetMapping("/details/{coinId}")
    @Operation(summary = "Retrieves details for a specific coin by its ID.")
    ResponseEntity<JsonNode> getCoinDetails(@PathVariable String coinId) throws Exception {
        String coin=coinService.getCoinDetails(coinId);
        JsonNode jsonNode = objectMapper.readTree(coin);
        return ResponseEntity.ok(jsonNode);
    }

}
