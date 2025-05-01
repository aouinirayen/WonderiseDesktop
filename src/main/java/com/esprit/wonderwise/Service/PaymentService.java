package com.esprit.wonderwise.Service;

import com.esprit.wonderwise.Model.offre;
import com.esprit.wonderwise.Model.reservation;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

public class PaymentService {

    static {
        Stripe.apiKey = "sk_test_51QCAHpBDW3LkkcbKFv9eNqLWddEC9JLkjAoZkB6VC6e52cHjojXS5vlEoWlZQWCeeAaN67bpZllUrR9RWehULuup00lCYHx3bX";
    }

    public String createCheckoutSession(reservation res, offre offer) throws StripeException {
        if (offer == null) {
            throw new IllegalArgumentException("Offer cannot be null");
        }
        if (res == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        if (res.getNombrePersonne() <= 0) {
            throw new IllegalArgumentException("Number of people must be greater than 0");
        }
        if (offer.getPrix() <= 0) {
            throw new IllegalArgumentException("Offer price must be greater than 0");
        }
        if (res.getId() <= 0) {
            throw new IllegalArgumentException("Reservation ID must be valid");
        }

        double exchangeRate = 0.32;
        double amountInUSD = offer.getPrix() * res.getNombrePersonne() * exchangeRate;
        long amountInCents = (long) (amountInUSD * 100);

        if (amountInCents <= 0) {
            throw new IllegalArgumentException("Total amount must be greater than 0");
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/success?session_id={CHECKOUT_SESSION_ID}&reservation_id=" + res.getId())
                .setCancelUrl("http://localhost:8080/cancel?reservation_id=" + res.getId())
                .setCurrency("usd")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity((long) res.getNombrePersonne())
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount((long) (offer.getPrix() * exchangeRate * 100)) // Price per person in cents
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(offer.getTitre() + " - " + offer.getPays())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);
        System.out.println("Stripe session created: " + session.getId() + ", URL: " + session.getUrl());
        return session.getUrl(); // Return the full checkout URL
    }

    public Session verifyPayment(String sessionId) throws StripeException {
        return Session.retrieve(sessionId);
    }
}