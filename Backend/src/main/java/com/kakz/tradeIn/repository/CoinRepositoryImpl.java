package com.kakz.tradeIn.repository;

import com.kakz.tradeIn.model.Coin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CoinRepositoryImpl implements CoinRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(Coin coin) {
        if (findById(coin.getId()) == null) {
            entityManager.persist(coin); // Insert
        } else {
            entityManager.merge(coin);  // Update
        }
    }

    @Override
    public Coin findById(String id) {
        return entityManager.find(Coin.class, id);
    }

    @Override
    public List<Coin> findAll() {
        String sql = "SELECT * FROM coin";
        Query query = entityManager.createNativeQuery(sql, Coin.class);
        return query.getResultList();
    }

    @Override
    public void deleteById(String id) {
        Coin coin = findById(id);
        if (coin != null) {
            entityManager.remove(coin);
        }
    }
}
