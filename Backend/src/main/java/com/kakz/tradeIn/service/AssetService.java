package com.kakz.tradeIn.service;

import com.kakz.tradeIn.model.Asset;
import com.kakz.tradeIn.model.Coin;
import com.kakz.tradeIn.model.User;

import java.util.List;

/**
 * AssetService is an interface that provides methods for managing user assets.
 */
public interface AssetService {
    Asset createAsset(User user, Coin coin, double quantity);

    Asset getAssetById(Long assetId) throws Exception;

    Asset getAssetByUserAndId(Long userId,Long assetId);

    List<Asset> getUsersAssets(Long userId);

    Asset updateAsset(Long assetId,double quantity) throws Exception;

    Asset findAssetByUserIdAndCoinId(Long userId,String coinId) throws Exception;

    void deleteAsset(Long assetId) throws Exception;

}
