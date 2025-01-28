package com.kakz.tradeIn.controller;

import com.kakz.tradeIn.model.Asset;
import com.kakz.tradeIn.model.User;
import com.kakz.tradeIn.service.AssetService;
import com.kakz.tradeIn.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The AssetController class is a REST controller for managing assets.
 * It provides endpoints for retrieving asset information based on asset ID,
 * coin ID, and user authentication.
 */
@RestController
@RequestMapping("/api/assets")
public class AssetController {
    /**
     * Service for performing operations on assets.
     * Provides methods*/
    private final AssetService assetService;
    /**
     * Service for managing user-related operations.
     *
     * This service provides*/
    @Autowired
    private UserService userService;

    /**
     * Constructs an AssetController with the provided AssetService.
     *
     * @param assetService the asset service to*/
    @Autowired
    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    /**
     * Retrieves an asset by its ID.
     *
     * @param assetId the ID of the asset to*/
    @GetMapping("/{assetId}")
    @Operation(summary = "Retrieves an asset by its ID")
    public ResponseEntity<Asset> getAssetById(@PathVariable Long assetId) throws Exception {
        Asset asset = assetService.getAssetById(assetId);
        return ResponseEntity.ok().body(asset);
    }

    /**
 * Retrieves an asset by user ID and coin ID.
 *
 * @param coinId The ID of the coin to retrieve the asset for.
 * @param jwt The JSON Web Token (JWT) used for authenticating the user.
 * @return A ResponseEntity containing the asset owned by the authenticated user for the specified coin.
 * @throws Exception If an error occurs while retrieving user profile or asset information.
 */
    @GetMapping("/coin/{coinId}/user")
    @Operation(summary = "Retrieves an asset by user ID and coin ID")
    public ResponseEntity<Asset> getAssetByUserIdAndCoinId(
            @PathVariable String coinId,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user=userService.findUserProfileByJwt(jwt);
        Asset asset = assetService.findAssetByUserIdAndCoinId(user.getId(), coinId);
        return ResponseEntity.ok().body(asset);
    }

    /**
     * Retrieves the list of assets for a given user based on their JWT token.
     *
     * @param jwt The JSON Web Token (JWT) used for authenticating the user.
     * @return A ResponseEntity containing the list of assets owned by the authenticated user.
     * @throws Exception If an error occurs while retrieving user profile or assets.
     */
    @GetMapping()
    @Operation(summary = " Retrieves the list of assets for a given user based on their JWT token.")
    public ResponseEntity<List<Asset>> getAssetsForUser(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user=userService.findUserProfileByJwt(jwt);
        List<Asset> assets = assetService.getUsersAssets(user.getId());
        return ResponseEntity.ok().body(assets);
    }
}
