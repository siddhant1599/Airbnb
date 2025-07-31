package com.spring.app.airBnb.service;

import com.spring.app.airBnb.entity.Booking;

public interface CheckoutService {

    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);
}
