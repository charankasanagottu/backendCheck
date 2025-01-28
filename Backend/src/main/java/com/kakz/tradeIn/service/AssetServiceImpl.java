package com.kakz.tradeIn.service;

import com.kakz.tradeIn.model.Asset;
import com.kakz.tradeIn.model.Coin;
import com.kakz.tradeIn.model.User;
import com.kakz.tradeIn.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AssetServiceImpl is a service implementation that provides methods for managing user assets.
 */
@Service
public class AssetServiceImpl implements AssetService{
    @Autowired
    private AssetRepository assetRepository;

    /**
     * Creates a new asset for the specified user with the given coin and quantity.
     *
     * @param user The user owning the asset.
     * @param coin The coin associated with the asset.
     * @param quantity The quantity of the coin.
     * @return The created and saved Asset object.
     */
    @Override
    public Asset createAsset(User user, Coin coin, double quantity) {
        Asset asset = new Asset();
        asset.setUser(user);
        asset.setCoin(coin);
        asset.setQuantity(quantity);
        asset.setBuyPrice(coin.getCurrentPrice());

        return assetRepository.save(asset);
    }

    /**
     * Retrieves an Asset by its ID.
     *
     * @param assetId the ID of the asset to retrieve.
     * @return the Asset with the given ID.
     * @throws Exception if the asset cannot be found.
     */
    @Override
    public Asset getAssetById(Long assetId) throws Exception {

        return assetRepository.findById(assetId)
                .orElseThrow(()-> new Exception("Asset not found"));
    }

    /**
     * Retrieves an Asset belonging to a specific user and identified by its ID.
     *
     * @param userId the ID of the user to whom the asset belongs
     * @param assetId the ID of the asset to be retrieved
     * @return the Asset owned by the specified user and identified by the specified ID, or null if not found
     */
    @Override
    public Asset getAssetByUserAndId(Long userId, Long assetId) {

        return null;
    }

    /**
     * Retrieves a list of assets owned by a specific user.
     *
     * @param userId the ID of the user whose assets are to be retrieved.
     * @return a list of assets owned by the user.
     */
    @Override
    public List<Asset> getUsersAssets(Long userId) {
        return assetRepository.findByUserId(userId);
    }

    /**
     * Updates the quantity of an existing asset.
     *
     * @param assetId The ID of the asset to be updated.
     * @param quantity The new quantity to set for the asset.
     * @return The updated Asset object.
     * @throws Exception If the asset is not found.
     */
    @Override
    public Asset updateAsset(Long assetId, double quantity) throws Exception {
        Asset asset =getAssetById(assetId);
        asset.setQuantity(quantity);
        return assetRepository.save(asset);
    }

    /**
     * Finds an Asset by the user's ID and the coin's ID.
     *
     * @param userId ID of the user who owns the asset.
     * @param coinId ID of the coin associated with the asset.
     * @return The Asset associated with the given user ID and coin ID.
     * @throws Exception if an error occurs while retrieving the asset.
     */
    @Override
    public Asset findAssetByUserIdAndCoinId(Long userId, String coinId) throws Exception {
        return assetRepository.findByUserIdAndCoinId(userId, coinId);
    }

    /**
     * Deletes the asset from the repository based on the provided asset ID.
     *
     * @param assetId the ID of the asset to be deleted
     * @throws Exception if the asset could not be found or deleted
     */
    @Override
    public void deleteAsset(Long assetId) throws Exception {
        assetRepository.deleteById(assetId);
    }
}
