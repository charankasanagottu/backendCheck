package com.kakz.tradeIn.repository;

import com.kakz.tradeIn.model.Coin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinRepository extends JpaRepository<Coin,String> {

}
