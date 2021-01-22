package com.griesba.brewery.beer.order.service.services.component;

import com.griesba.brewery.beer.order.service.config.JmsConfig;
import com.griesba.brewery.model.events.AllocateOrderRequest;
import com.griesba.brewery.model.events.AllocationOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerAllocationListener {
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(Message msg) {
        AllocateOrderRequest request = (AllocateOrderRequest) msg.getPayload();
        boolean pendingInventory = false;
        boolean allocationError = false;
        boolean sendResponse = true;

/*
        if (request.getBeerOrderDto().getCustomerRef() != null) {
            if (request.getBeerOrderDto().getCustomerRef().equals("partial-allocation")) {
                //set allocation error
                pendingInventory = true;
            }   else if (request.getBeerOrderDto().getCustomerRef().equals("fail-allocation")) {
                //set pending inventory
                allocationError = true;
            } else if (request.getBeerOrderDto().getCustomerRef().equals("dont-allocate")) {
                sendResponse = false;
            }
        }
*/
        log.debug("Received allocation request beer  order for {}", request.getBeerOrderDto().getId());
        boolean finalPendingInventory = pendingInventory;

        request.getBeerOrderDto().getBeerOrderLines().forEach(beerOrderLineDto -> {
/*            if (finalPendingInventory) {
                beerOrderLineDto.setQuantityAllocated(beerOrderLineDto.getOrderQuantity() -1);
            } else {
                beerOrderLineDto.setQuantityAllocated(beerOrderLineDto.getOrderQuantity());
            }*/

            log.debug("Before beerOrderLineDto.getQuantityAllocated {}", beerOrderLineDto.getAllocatedQuantity());
            beerOrderLineDto.setAllocatedQuantity(beerOrderLineDto.getOrderQuantity());
            log.debug("After beerOrderLineDto.getQuantityAllocated {}", beerOrderLineDto.getAllocatedQuantity());
        });

        if (sendResponse) {
            log.debug("Beer order allocated for {}", request.getBeerOrderDto().getId());
            log.debug("Sending allocation response to ALLOCATE_ORDER_RESPONSE_QUEUE");
            jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE,
                    new AllocationOrderResponse.AllocationOrderResponseBuilder<>()
                            .withBeerOrderDto(request.getBeerOrderDto())
                            .withPendingInventory(pendingInventory)
                            .withAllocationError(allocationError)
                            .build());
        }
    }
}
