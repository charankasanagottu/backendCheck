package com.kakz.tradeIn.controller;

import com.kakz.tradeIn.domain.OrderType;
import com.kakz.tradeIn.model.Coin;
import com.kakz.tradeIn.model.Order;
import com.kakz.tradeIn.model.User;
import com.kakz.tradeIn.request.CreateOrderRequest;
import com.kakz.tradeIn.service.CoinService;
import com.kakz.tradeIn.service.OrderService;
import com.kakz.tradeIn.service.UserService;
import com.kakz.tradeIn.service.WalletTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userSerivce;

    @Autowired
    private CoinService coinService;

//    @Autowired
//    private WalletTransactionService walletTransactionService;


//    private

//    @Autowired
//    public OrderController(OrderService orderService, UserService userSerivce,CoinService coinService) {
//        this.orderService = orderService;
//        this.userSerivce=userSerivce;
//        this.coinService = coinService;
//
//    }

    /**
     * Processes a payment for an order using the provided JWT for user authentication
     * and order details from the request.
     *
     * @param jwt  the JWT token for user authentication
     * @param req  the request object containing order details such as coin ID, quantity, and order type
     * @return a ResponseEntity containing the processed order
     * @throws Exception if an error occurs during the processing of the order
     */
    @PostMapping("/pay")
    @Operation(summary = "Processes a payment for an order using the provided JWT for user authentication")
    public ResponseEntity<Order> payOrderPayment(
            @RequestHeader("Authorization") String jwt,
            @RequestBody CreateOrderRequest req

    ) throws Exception {
        User user = userSerivce.findUserProfileByJwt(jwt);
        Coin coin =coinService.findById(req.getCoinId());

        Order order = orderService.processOrder(coin,req.getQuantity(),req.getOrderType(),user);

        return ResponseEntity.ok(order);

    }

    /**
     * Retrieves an order by its ID after verifying the user's authentication
     * via the provided JWT token. The retrieved order is only returned
     * if it belongs to the authenticated user.
     *
     * @param jwtToken the JSON Web Token used for user authentication
     * @param orderId the ID of the order to be retrieved
     * @return a ResponseEntity containing the order if the user is authorized or
     *         a ResponseEntity with HTTP status FORBIDDEN if the user is not authorized
     * @throws Exception if the JWT token is missing or there is an error during user or order retrieval
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "Retrieves an order by its ID after verifying the user's'")
    public ResponseEntity<Order> getOrderById(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable Long orderId
    ) throws Exception {
        if (jwtToken == null) {
            throw new Exception("token missing...");
        }

        User user = userSerivce.findUserProfileByJwt(jwtToken);

        Order order = orderService.getOrderById(orderId);
        if (order.getUser().getId() == (user.getId())) {
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Retrieves all orders for a user based on the provided JWT token. Optional filters for order type
     * and asset symbol can be applied.
     *
     * @param jwtToken the JSON Web Token used for user authentication
     * @param order_type the type of orders to filter by (optional)
     * @param asset_symbol the asset symbol to filter orders by (optional)
     * @return a ResponseEntity containing a list of orders for the authenticated user
     * @throws Exception if the JWT token is missing or if an error occurs during the retrieval process
     */
    @GetMapping()
    @Operation(summary = "Retrieves all orders for a user based on the provided JWT token")
    public ResponseEntity<List<Order>> getAllOrdersForUser(
            @RequestHeader("Authorization") String jwtToken,
            @RequestParam(required = false) OrderType order_type,
            @RequestParam(required = false) String asset_symbol
    ) throws Exception {
        if (jwtToken == null) {
            throw new Exception("token missing...");
        }

        Long userId = userSerivce.findUserProfileByJwt(jwtToken).getId();

        List<Order> userOrders = orderService.getAllOrdersofUser(userId,order_type,asset_symbol);
        return ResponseEntity.ok(userOrders);
    }
}
