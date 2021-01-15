package com.griesba.brewery.beer.order.service.services.listeners;

import com.griesba.brewery.beer.order.service.config.JmsConfig;
import com.griesba.brewery.beer.order.service.services.BeerOrderManager;
import com.griesba.brewery.model.BeerOrderDto;
import com.griesba.brewery.model.events.AllocationOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderRequestAllocationListener {

    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE)
    public void listen(AllocationOrderResponse allocationOrderResponse){

        log.debug("allocation response received on ALLOCATE_ORDER_RESPONSE_QUEUE");

        BeerOrderDto beerOrderDto = allocationOrderResponse.getBeerOrderDto();
        if (!allocationOrderResponse.getAllocationError() && !allocationOrderResponse.getPendingInventory()) {
            beerOrderManager.beerOrderAllocationSucceeded(beerOrderDto);
        } else if (!allocationOrderResponse.getAllocationError() && allocationOrderResponse.getPendingInventory()) {
            beerOrderManager.beerOrderAllocationPendingInventory(beerOrderDto);
        } else if (allocationOrderResponse.getAllocationError()) {
            beerOrderManager.beerOderAllocationFailed(beerOrderDto);
        }

    }
}
