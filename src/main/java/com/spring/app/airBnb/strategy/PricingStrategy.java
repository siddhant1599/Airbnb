package com.spring.app.airBnb.strategy;

import com.spring.app.airBnb.entity.Inventory;
import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);
}
