package com.spring.app.airBnb.strategy;

import com.spring.app.airBnb.entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PricingService {

    public BigDecimal calculatePricingStrategy(Inventory inventory){

        PricingStrategy pricingStrategy = new BasePricingStrategy();

        //apply additional strategy
        pricingStrategy = new SurgePricingStrategy(pricingStrategy);
        pricingStrategy = new OccupancyPricingStrategy(pricingStrategy);
        pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);
        pricingStrategy = new HolidayPricingStrategy(pricingStrategy);

        return pricingStrategy.calculatePrice(inventory);
    }

    public BigDecimal calculateTotalPrice(List<Inventory> inventoryList){
               return inventoryList.stream()
                .map(this::calculatePricingStrategy)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
