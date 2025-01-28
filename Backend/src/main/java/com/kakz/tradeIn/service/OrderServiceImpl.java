package com.kakz.tradeIn.service;

import com.kakz.tradeIn.domain.OrderStatus;
import com.kakz.tradeIn.domain.OrderType;
import com.kakz.tradeIn.model.*;
import com.kakz.tradeIn.repository.OrderItemRepository;
import com.kakz.tradeIn.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 */
@Service
public class OrderServiceImpl implements OrderService{

    /**
     * The OrderRepository instance used for performing CRUD operations
     * on Order entities in the underlying database. This repository
     * allows for retrieving, storing, updating, and deleting Order
     * records as well as custom queries like finding orders by user ID.
     */
    @Autowired
    private OrderRepository orderRepository;

    /**
     * An instance of WalletService that handles operations related to user wallets
     * such as retrieving wallet information, handling balance updates, and processing
     * payments and transfers.
     */
    @Autowired
    private WalletService walletService;

    /**
     * The OrderItemRepository is a Spring Data repository that provides JPA-based
     * data access functionality for the OrderItem entity.
     * <p>
     * It provides methods for performing CRUD operations, and integration
     * with the persistence context including custom functionality if extended.
     */
    @Autowired
    private OrderItemRepository orderItemRepository;

    /**
     * Service for managing user assets.
     * This service includes operations such as creating, retrieving, updating,
     * and deleting assets.
     */
    @Autowired
    private AssetService assetService;


    /**
     * Creates an order based on the given user, order item, and order type.
     *
     * @param user the user who is creating the order
     * @param orderItem the item being ordered
     * @param orderType the type of order (BUY or SELL)
     * @return the created order
     */
    @Override
    public Order createOrder(User user, OrderItem orderItem, OrderType orderType) {

        double price = orderItem.getCoin().getCurrentPrice()*orderItem.getQuantity();

        Order order = new Order();
        order.setOrderType(orderType);
        order.setOrderItem(orderItem);
        order.setPrice(BigDecimal.valueOf(price));
        order.setUser(user);
        order.setTimestamp(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        return orderRepository.save(order);
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param id the ID of the order to be retrieved
     * @return the Order associated with the specified ID
     * @throws Exception if no order is found with the specified ID
     */
    @Override
    public Order getOrderById(Long id) throws Exception {
        return  orderRepository
                .findById(id)
                .orElseThrow(
                        ()-> new Exception("Order not found"));
    }

    /**
     * Retrieves all orders for a given user, filtered by order type and asset symbol.
     *
     * @param userId      the ID of the user whose orders are to be retrieved
     * @param orderType   the type of orders to filter (e.g., BUY, SELL)
     * @param assetSymbol the symbol of the asset to filter orders by
     * @return a list of orders for the specified user, filtered according to the provided parameters
     */
    @Override
    public List<Order> getAllOrdersofUser(Long userId, OrderType orderType, String assetSymbol) {

        return orderRepository.findByUserId(userId);
    }

    /**
     * Creates an OrderItem using the provided parameters and saves it into the repository.
     *
     * @param coin the coin for which the order item is created
     * @param quantity the quantity of the coin
     * @param buyPrice the buying price of the coin
     * @param sellPrice the selling price of the coin
     * @return the saved OrderItem
     */
    public OrderItem createOrderItem(Coin coin,double quantity,
                                      double buyPrice,double sellPrice){
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(quantity);
        orderItem.setBuyPrice(buyPrice);
        orderItem.setSellPrice(sellPrice);
        orderItem.setCoin(coin);
        return orderItemRepository.save(orderItem);
    }

    /**
     * Initiates the process to buy a specified quantity of a given coin for a user.
     * The method will first validate the quantity, create order items, initiate payment,
     * update wallet balances, and then either create or update the user's asset.
     *
     * @param coin The coin that is being bought.
     * @param quantity The quantity of the coin to be bought.
     * @param user The user who is buying the coin.
     * @return The completed order with updated status and order items.
     * @throws Exception if the quantity is less than zero or if there are issues with payment or asset updates.
     */
    @Transactional
    public Order buyAsset(Coin coin, double quantity,User user) throws Exception {
        if(quantity< 0){
            throw new Exception("Quantity must be more than zero");
        }
        double price = coin.getCurrentPrice();
        OrderItem orderItem = createOrderItem(coin, quantity, price,0);
        Order order = createOrder(user,orderItem,OrderType.BUY);
        orderItem.setOrder(order);

        walletService.payOrderPayment(order, user);
        order.setStatus(OrderStatus.SUCCESS);
        order.setOrderType(OrderType.BUY);
        Order saveOrder = orderRepository.save(order);
//        Create Asset
        Asset oldAsset = assetService.findAssetByUserIdAndCoinId(order.getUser().getId(),
                order.getOrderItem().getCoin().getId());
        if(oldAsset == null){
            assetService.createAsset(user,
                    orderItem.getCoin(),
                    orderItem.getQuantity());
        }
        else{
            assetService.updateAsset(oldAsset.getId(),quantity);
        }
        return saveOrder;
    }

    /**
     * Sells a specified quantity of a given coin from the user's assets.
     * If the quantity is greater than the available quantity, or the asset is not found, an exception is thrown.
     *
     * @param coin the coin to be sold
     * @param quantity the amount of the coin to be sold
     * @param user the user selling the coin
     * @return the created order for the sale
     * @throws Exception if the quantity is less than or equal to zero, the asset is not found, or there is insufficient quantity to sell
     */
    @Transactional
    public Order sellAsset(Coin coin, double quantity,User user) throws Exception {
        if(quantity<= 0){
            throw new Exception("Quantity must be more than zero");
        }
        double sellPrice = coin.getCurrentPrice();

        Asset assetToSell =assetService.findAssetByUserIdAndCoinId(
                user.getId(),
                coin.getId());

        if(assetToSell != null) {

            OrderItem orderItem = createOrderItem(coin,
                    quantity,
                    assetToSell.getBuyPrice(),
                    sellPrice);

        Order order = createOrder(user,
                orderItem,
                OrderType.SELL);

        orderItem.setOrder(order);

        if(assetToSell.getQuantity()>= quantity){
            order.setStatus(OrderStatus.SUCCESS);
            order.setOrderType(OrderType.SELL);
            Order savedOrder = orderRepository.save(order);
            walletService.payOrderPayment(order,user);

            Asset updatedAsset = assetService.updateAsset(
                    assetToSell.getId(),
                    -quantity);
            if(updatedAsset.getQuantity()*coin.getCurrentPrice()<=1 ){
                assetService.deleteAsset(updatedAsset.getId());
            }
            return savedOrder;
        }

//        Create Asset
        throw new Exception("Insufficient Quantity to Sell Asset");
        }
        throw new Exception("Asset Not Found");
    }

    /**
     * Processes an order by determining whether it is a buy or sell transaction.
     * Depending on the order type, it delegates to the appropriate method to handle the transaction.
     *
     * @param coin the cryptocurrency coin involved in the order
     * @param quantity the quantity of the coin to be processed
     * @param orderType the type of the order (BUY or SELL)
     * @param user the user placing the order
     * @return the processed order
     * @throws Exception if the order type is invalid or any errors occur during order processing
     */
    @Override
    @Transactional
    public Order processOrder(Coin coin, double quantity,
                              OrderType orderType, User user) throws Exception {
        if(orderType == OrderType.BUY){
            return buyAsset(coin, quantity,user);
        }
        else if(orderType == OrderType.SELL){
            return sellAsset(coin, quantity,user);
        }
        else{
            throw  new Exception("Invalid Order Type");
        }
    }
}
