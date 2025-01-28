package com.kakz.tradeIn.service;

import com.kakz.tradeIn.domain.PaymentMethod;
import com.kakz.tradeIn.domain.PaymentOrderStatus;
import com.kakz.tradeIn.model.PaymentOrder;
import com.kakz.tradeIn.model.User;
import com.kakz.tradeIn.repository.PaymentOrderRepository;
import com.kakz.tradeIn.response.PaymentResponse;
import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the PaymentService interface. This class provides services for creating and managing
 * payment orders, including integration with payment gateways like Razorpay and Stripe.
 */
@Service
public class PaymentServiceImpl implements PaymentService{

    /**
     * Repository interface for performing CRUD operations on PaymentOrder entities.
     * Extends JpaRepository to leverage Spring Data JPA functionalities.
     */
    @Autowired
    private PaymentOrderRepository paymentOrderRepository;

    /**
     * The secret key used for authenticating with the Stripe API.
     * This key is typically required for making authorized requests
     * to Stripe's backend services.
     *
     * The value is injected from the application properties using
     * the key "stripe.api.key".
     */
    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    /**
     * Represents the API key used to authenticate requests to the Razorpay service.
     * The value is injected from the application's configuration properties.
     */
    @Value("${razorpay.api.key}")
    private String razorApiKey;

    /**
     * Holds the secret API key for accessing the Razorpay service.
     * This value is injected from the application's configuration properties.
     */
    @Value("${razorpay.api.secret}")
    private String apiSecretKey;


    /**
     * Creates a new payment order with the given user, amount, and payment method.
     * The newly created order will have its status set to PENDING.
     *
     * @param user the user placing the order
     * @param amount the amount for the payment order
     * @param paymentMethod the method of payment (e.g., RAZORPAY, STRIPE)
     * @return the created PaymentOrder instance
     */
    @Override
    public PaymentOrder createPaymentOrder(User user,
                                           Long amount,
                                           PaymentMethod paymentMethod) {
        PaymentOrder order = new PaymentOrder();
        order.setPaymentMethod(paymentMethod);
        order.setUser(user);
        order.setAmount(amount);
        order.setStatus(PaymentOrderStatus.PENDING);

        return paymentOrderRepository.save(order);
    }

    /**
     * Retrieves a PaymentOrder by its unique identifier.
     *
     * @param paymentOrderId the unique identifier of the PaymentOrder to retrieve
     * @return the PaymentOrder associated with the specified identifier
     * @throws Exception if no PaymentOrder is found with the given identifier
     */
    @Override
    public PaymentOrder getPaymentOrderById(Long paymentOrderId) throws Exception {
        Optional<PaymentOrder> paymentOrderOptional = paymentOrderRepository.findById(paymentOrderId);
        if(paymentOrderOptional.isEmpty()){
            throw new Exception("PaymentOrder is not found with id " + paymentOrderId);
        }
        return paymentOrderOptional.get();
    }

    /**
     * Processes the payment order by verifying the payment status with the payment provider.
     *
     * @param paymentOrder the payment order to be processed
     * @param paymentId the payment identifier used to fetch payment details from the provider
     * @return true if the payment order was successfully processed, false otherwise
     * @throws RazorpayException if there is an error while interacting with the Razorpay API
     */
    @Override
    public boolean proceedPaymentOrder(PaymentOrder paymentOrder,
                                       String paymentId) throws RazorpayException {
        if(paymentOrder.getStatus() ==null){
            paymentOrder.setStatus(PaymentOrderStatus.PENDING);
        }
        if(paymentOrder.getStatus().equals(PaymentOrderStatus.PENDING)){

            if(paymentOrder.getPaymentMethod().equals(PaymentMethod.RAZORPAY)){

                RazorpayClient razorpayClient = new RazorpayClient(razorApiKey,apiSecretKey);
                Payment payment = razorpayClient.payments.fetch(paymentId);

                Integer amount = payment.get("amount");
                String status = payment.get("status");

                if(status.equals("captured") ){
                    paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
                    return true;
                }

                paymentOrder.setStatus(PaymentOrderStatus.FAILURE);
                paymentOrderRepository.save(paymentOrder);

                return false;
            }
            paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);

            paymentOrderRepository.save(paymentOrder);
            return true;
        }

        return false;
    }

    /**
     * Creates a Razorpay payment link for a given user and order.
     *
     * @param user The user for whom the payment link is being created.
     * @param amount The amount for the payment (in smallest currency unit, e.g., paise for INR).
     * @param orderId The ID of the order associated with the payment link.
     * @return A PaymentResponse containing the payment URL.
     * @throws RazorpayException If there is an error creating the payment link.
     */
    @Override
    public PaymentResponse createRazorpayPaymentLink(User user, Long amount,
                                                     Long orderId) throws RazorpayException {
        Long Amount = amount*100;
        try {
            // Instantiate a Razorpay client with your key ID and secret
            RazorpayClient razorpay = new RazorpayClient(razorApiKey, apiSecretKey);

            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount",amount);
            paymentLinkRequest.put("currency","INR");


            // Create a JSON object with the customer details
            JSONObject customer = new JSONObject();
            customer.put("name",user.getFullName());

            customer.put("email",user.getEmail());
            paymentLinkRequest.put("customer",customer);

            // Create a JSON object with the notification settings
            JSONObject notify = new JSONObject();
            notify.put("email",true);
            paymentLinkRequest.put("notify",notify);

            // Set the reminder settings
            paymentLinkRequest.put("reminder_enable",true);

            // Set the callback URL and method
            paymentLinkRequest.put("callback_url","http://localhost:5173/wallet/?order_id="+orderId);
            paymentLinkRequest.put("callback_method","get");

            // Create the payment link using the paymentLink.create() method
            PaymentLink payment = razorpay.paymentLink.create(paymentLinkRequest);

            String paymentLinkId = payment.get("id");
            String paymentLinkUrl = payment.get("short_url");

            PaymentResponse res=new PaymentResponse();
            res.setPayment_url(paymentLinkUrl);


            return res;
        } catch (RazorpayException e) {

            System.out.println("Error creating payment link: " + e.getMessage());
            throw new RazorpayException(e.getMessage());
        }
    }

    /**
     * Creates a Stripe payment link for a given user, amount, and order ID.
     *
     * @param user The user for whom the payment link is being created.
     * @param amount The amount to be paid.
     * @param orderId The ID of the order associated with the payment.
     * @return PaymentResponse containing the URL for the payment.
     * @throws StripeException if there is any error during the creation of the payment session.
     */
    @Override
    public PaymentResponse createStripPaymentLink(User user,
                                                  Long amount,
                                                  Long orderId) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:5173/wallet?order_id="+orderId)
                .setCancelUrl("http://localhost:5173/payment/cancel")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("inr")
                                .setUnitAmount(amount*100)
                                .setProductData(SessionCreateParams
                                        .LineItem
                                        .PriceData
                                        .ProductData
                                        .builder()
                                        .setName("Top up wallet")
                                        .build()
                                ).build()
                        ).build()
                ).build();

        Session session = Session.create(params);

        System.out.println("session _____ " + session);

        PaymentResponse res = new PaymentResponse();
        res.setPayment_url(session.getUrl());

        return res;
    }
}
