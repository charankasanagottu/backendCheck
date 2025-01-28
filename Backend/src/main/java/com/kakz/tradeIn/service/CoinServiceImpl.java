package com.kakz.tradeIn.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakz.tradeIn.model.Coin;
import com.kakz.tradeIn.repository.CoinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.Optional;

/**
 * Service implementation for handling cryptocurrency-related operations.
 * This class provides methods to fetch coin data, market charts, detailed coin information,
 * and perform search operations related to coins using the CoinGecko API.
 */
@Service
public class CoinServiceImpl implements CoinService {

    /**
     * Repository interface for accessing and managing data related to `Coin` entities.
     *
     * This autowired instance allows for various CRUD operations such as
     * fetching, saving, updating, and deleting `Coin` records from the database.
     * It uses Spring Data JPA's `JpaRepository` to provide these functionalities.
     */
    @Autowired
    private CoinRepository coinRepository;

    /**
     * ObjectMapper instance used for converting Java objects to and from JSON.
     * This instance is injected by Spring's dependency injection framework.
     * It is commonly used for serializing and deserializing JSON data,
     * facilitating data transfer between the application and external services.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Fetches a list of coins from the CoinGecko API based on the specified page number.
     * Each page contains a fixed number of coins (10 in this case).
     *
     * @param page The page number to retrieve from the CoinGecko API.
     * @return A list of Coin objects representing the different cryptocurrencies.
     * @throws Exception if there is an error while fetching the coin data or processing the response.
     */
    @Override
    public List<Coin> getCoinList(int page) throws Exception {
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&per_page=10&page="+page;


        RestTemplate restTemplate = new RestTemplate();
        try {
            HttpHeaders headers = new HttpHeaders();
//            headers.set("x-cg-demo-api-key", API_KEY);


            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

//            System.out.println(response.getBody());
            List<Coin> coins = objectMapper.readValue(response.getBody(), new TypeReference<List<Coin>>() {});

            return coins;

        } catch (HttpClientErrorException | HttpServerErrorException | JsonProcessingException e) {
            System.err.println("Error: " + e);
            // Handle error accordingly
            throw new Exception("please wait for time because you are using free plan");
        }

    }

    /**
     * Fetches the market chart data for a specified coin over a given number of days.
     *
     * @param coinId the identifier of the coin for which the market chart data is to be retrieved
     * @param days the number of days for which the market chart data is to be retrieved
     * @return the market chart data as a JSON string
     * @throws Exception if an error occurs while fetching the market chart data due to rate limits or other issues
     */
    @Override
    public String getMarketChart(String coinId, int days) throws Exception {

        String url = "https://api.coingecko.com/api/v3/coins/"+coinId+"/market_chart?vs_currency=usd&per_page=10&days="+days;

        RestTemplate restTemplate = new RestTemplate();
        try {
            HttpHeaders headers = new HttpHeaders();

            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("Error: " + e);
            // Handle error accordingly
            throw new Exception("please wait for time because you are using free plan");
        }
    }

    /**
     * Retrieves detailed information about a specific cryptocurrency coin by its ID.
     *
     * @param coinId the unique identifier of the cryptocurrency coin.
     * @return a JSON string representing the detailed information of the specified cryptocurrency coin.
     * @throws Exception if there is an error during the API call or data processing.
     */
    @Override
    public String getCoinDetails(String coinId) throws Exception {
        String url = "https://api.coingecko.com/api/v3/coins/"+coinId;

        RestTemplate restTemplate = new RestTemplate();
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            Coin coin = new Coin();
            coin.setId(jsonNode.get("id").asText());
            coin.setName(jsonNode.get("name").asText());
            coin.setSymbol(jsonNode.get("symbol").asText());
            coin.setImage(jsonNode.get("image").get("large").asText());

            JsonNode marketData = jsonNode.get("market_data");

            coin.setCurrentPrice(marketData.get("current_price").get("usd").asDouble());
            coin.setMarketCap(marketData.get("market_cap").get("usd").asLong());
            coin.setMarketCapRank(jsonNode.get("market_cap_rank").asInt());
            coin.setTotalVolume(marketData.get("total_volume").get("usd").asLong());
            coin.setHigh24h(marketData.get("high_24h").get("usd").asDouble());
            coin.setLow24h(marketData.get("low_24h").get("usd").asDouble());
            coin.setPriceChange24h(marketData.get("price_change_24h").asDouble());
            coin.setPriceChangePercentage24h(marketData.get("price_change_percentage_24h").asDouble());
            coin.setMarketCapChange24h(marketData.get("market_cap_change_24h").asLong());
            coin.setMarketCapChangePercentage24h(marketData.get("market_cap_change_percentage_24h").asDouble());
            coin.setCirculatingSupply(marketData.get("circulating_supply").asLong());
            coin.setTotalSupply(marketData.get("total_supply").asLong());

            coinRepository.save(coin);
            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("Error: " + e);
            // Handle error accordingly
            throw new Exception("please wait for time because you are using free plan");
        }
    }

    /**
     * Retrieves a Coin entity by its ID.
     *
     * @param coinId the ID of the coin to be retrieved
     * @return the Coin entity with the specified ID
     * @throws Exception if the coin is not found
     */
    @Override
    public Coin findById(String coinId) throws Exception {
        Optional<Coin> coin = coinRepository.findById(coinId);
        if(coin.isEmpty()){

            throw new Exception("Coin not found");
        }
        else{
            return coin.get();
        }
    }

    /**
     * Searches for cryptocurrency information based on a provided keyword.
     *
     * @param keyWord The keyword used for searching cryptocurrencies.
     * @return A string response from the CoinGecko API containing the search results.
     */
    @Override
    public String searchCoin(String keyWord) {
        String baseUrl ="https://api.coingecko.com/api/v3/search?query="+keyWord;

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);


        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, String.class);

        System.out.println(response.getBody());

        return response.getBody();
    }

    /**
     * Retrieves the top 50 cryptocurrencies by market capitalization.
     * The method fetches data from the CoinGecko API.
     *
     * @return A JSON string containing the details of the top 50 cryptocurrencies by market capitalization.
     * @throws Exception If an error occurs while fetching the data from the API.
     */
    @Override
    public String getTop50CoinByMarketCap() throws Exception {
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&page=1&per_page=50";

        RestTemplate restTemplate = new RestTemplate();
        try {
            HttpHeaders headers = new HttpHeaders();
//            headers.set("x-cg-demo-api-key", API_KEY);

            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("Error: " + e);

            throw new Exception(e.getMessage());
            // Handle error accordingly
//            return null;
        }
    }

    /**
     * Fetches the current trending trading coins from the CoinGecko API.
     *
     * @return A JSON string containing the list of trending trading coins.
     * @throws Exception if there is an error while attempting to retrieve the data from the API.
     */
    @Override
    public String getTradingCoins() throws Exception {
        String url = "https://api.coingecko.com/api/v3/search/trending";

        RestTemplate restTemplate = new RestTemplate();
        try {
            HttpHeaders headers = new HttpHeaders();
//            headers.set("x-cg-demo-api-key", API_KEY);

            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("Error: " + e);
            // Handle error accordingly
//            return null;
            throw new Exception("Error: " + e.getMessage());
        }
    }
}
