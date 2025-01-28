package com.kakz.tradeIn.repository;

import com.kakz.tradeIn.model.Asset;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AssetRepositoryImpl implements AssetRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Asset> findByUserId(Long userId) {
        String sql = "SELECT * FROM asset WHERE user_id = :userId";
        Query query = entityManager.createNativeQuery(sql, Asset.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public Asset findByUserIdAndCoinId(Long userId, String coinId) {
        String sql = "SELECT * FROM asset WHERE user_id = :userId AND coin_id = :coinId";
        Query query = entityManager.createNativeQuery(sql, Asset.class);
        query.setParameter("userId", userId);
        query.setParameter("coinId", coinId);
        return (Asset) query.getSingleResult();
    }

    @Override
    public Asset findByIdAndUserId(Long assetId, Long userId) {
        String sql = "SELECT * FROM asset WHERE id = :assetId AND user_id = :userId";
        Query query = entityManager.createNativeQuery(sql, Asset.class);
        query.setParameter("assetId", assetId);
        query.setParameter("userId", userId);
        return (Asset) query.getSingleResult();
    }
}
