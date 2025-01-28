package com.kakz.tradeIn.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Represents a cryptocurrency coin.
 *
 * This entity is mapped to a database table named "coins" and is used to store various
 * attributes about a cryptocurrency coin such as its current price, market capitalization,
 * volume, and historical data.
 *
 * Attributes:
 * - id: Unique identifier for the coin.
 * - symbol: Symbol representing the coin (e.g., BTC for Bitcoin).
 * - name: Full name of the coin.
 * - image: URL to an image representing the coin.
 * - currentPrice: Current trading price of the coin.
 * - marketCap: Market capitalization of the coin.
 * - marketCapRank: Ranking of the coin based on its market cap.
 * - fullyDilutedValuation: Fully diluted valuation of the coin.
 * - totalVolume: Total trading volume of the coin in the last 24 hours.
 * - high24h: Highest price of the coin in the last 24 hours.
 * - low24h: Lowest price of the coin in the last 24 hours.
 * - priceChange24h: Price change of the coin in the last 24 hours.
 * - priceChangePercentage24h: Percentage change in price over the last 24 hours.
 * - marketCapChange24h: Market capitalization change in the last 24 hours.
 * - marketCapChangePercentage24h: Percentage change in market capitalization over the last 24 hours.
 * - circulatingSupply: Current circulating supply of the coin.
 * - totalSupply: Total supply of the coin.
 * - maxSupply: Maximum supply of the coin.
 * - ath: All-time high price of the coin.
 * - athChangePercentage: Percentage change from the all-time high price.
 * - athDate: Date when the all-time high price was recorded.
 * - atl: All-time low price of the coin.
 * - atlChangePercentage: Percentage change from the all-time low price.
 * - atlDate: Date when the all-time low price was recorded.
 * - roi: Return on investment information for the coin (ignored in JSON serialization).
 * - lastUpdated: Last updated timestamp for the coin's data.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "coins")
public class Coin {

    @Id
    @JsonProperty("id")
    private String id;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("name")
    private String name;

    @JsonProperty("image")
    private String image;

    @JsonProperty("current_price")
    private double currentPrice;

    @JsonProperty("market_cap")
    private long marketCap;

    @JsonProperty("market_cap_rank")
    private int marketCapRank;

    @JsonProperty("fully_diluted_valuation")
    private long fullyDilutedValuation;

    @JsonProperty("total_volume")
    private long totalVolume;

    @JsonProperty("high_24h")
    private double high24h;

    @JsonProperty("low_24h")
    private double low24h;

    @JsonProperty("price_change_24h")
    private double priceChange24h;

    @JsonProperty("price_change_percentage_24h")
    private double priceChangePercentage24h;

    @JsonProperty("market_cap_change_24h")
    private long marketCapChange24h;

    @JsonProperty("market_cap_change_percentage_24h")
    private double marketCapChangePercentage24h;

    @JsonProperty("circulating_supply")
    private long circulatingSupply;

    @JsonProperty("total_supply")
    private long totalSupply;

    @JsonProperty("max_supply")
    private long maxSupply;

    @JsonProperty("ath")
    private double ath;

    @JsonProperty("ath_change_percentage")
    private double athChangePercentage;

    @JsonProperty("ath_date")
    private Date athDate;

    @JsonProperty("atl")
    private double atl;

    @JsonProperty("atl_change_percentage")
    private double atlChangePercentage;

    @JsonProperty("atl_date")
    private Date atlDate;

    @JsonProperty("roi")
    @JsonIgnore
    private String roi;

    @JsonProperty("last_updated")
    private Date lastUpdated;
}
