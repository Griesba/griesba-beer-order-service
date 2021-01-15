package com.griesba.brewery.beer.order.service.services;

import com.griesba.brewery.beer.order.service.domain.BeerOrder;
import com.griesba.brewery.model.BeerOrderDto;
import com.griesba.brewery.model.events.ValidateOrderResult;

public interface BeerOrderManager {
    BeerOrder newBeerOrder(BeerOrder beerOrder);

    void processValidationResult(ValidateOrderResult validateOrderResult);

    void beerOrderAllocationPendingInventory(BeerOrderDto beerOrderDto);

    void beerOrderAllocationSucceeded(BeerOrderDto beerOrderDto);

    void beerOderAllocationFailed(BeerOrderDto beerOrderDto);
}
