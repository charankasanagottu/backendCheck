package com.kakz.tradeIn.model;

import com.kakz.tradeIn.domain.OrderStatus;
import com.kakz.tradeIn.domain.OrderType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents an order in the system.
 * This entity is mapped to the 'orders' table in the database.
 *
 * An order contains details such as the user who placed the order,
 * the type and status of the order, the price at which the order
 * was placed, and the timestamp when the order was created.
 * Each order can be associated with one order item.
 */
@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;
    @Column(nullable = false)
    private OrderType orderType;
    @Column(nullable = false)
    private OrderStatus status;
    @Column(nullable = false)
    private BigDecimal price;
    private LocalDateTime timestamp = LocalDateTime.now();
    @OneToOne(mappedBy = "order",cascade = CascadeType.ALL)
    private OrderItem orderItem;
}
