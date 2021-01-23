package com.griesba.brewery.beer.order.service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.griesba.brewery.beer.order.service.domain.BeerOrder;
import com.griesba.brewery.beer.order.service.domain.BeerOrderEventEnum;
import com.griesba.brewery.beer.order.service.domain.BeerOrderStatusEnum;
import com.griesba.brewery.beer.order.service.repository.BeerOrderRepository;
import com.griesba.brewery.beer.order.service.statemachine.OrderStateChangeInterceptor;
import com.griesba.brewery.model.BeerOrderDto;
import com.griesba.brewery.model.events.ValidateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class BeerOrderManagerImpl implements BeerOrderManager {

    public static final String ORDER_ID_HEADER = "order-id-header";

    private final StateMachineFactory<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachineFactory;
    private final BeerOrderRepository beerOrderRepository;
    private final OrderStateChangeInterceptor orderStateChangeInterceptor;
    //private final EntityManager entityManager;

    @Override
    public BeerOrder newBeerOrder(BeerOrder beerOrder) {
        beerOrder.setId(null);
        beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);

        BeerOrder savedBeerOder = beerOrderRepository.saveAndFlush(beerOrder);
        sendBeerOrderEvent(savedBeerOder, BeerOrderEventEnum.VALIDATE_ORDER);
        log.debug("State machine stated with event: {}", BeerOrderEventEnum.VALIDATE_ORDER);
        return savedBeerOder;
    }

    @Transactional
    @Override
    public void processValidationResult(ValidateOrderResult validateOrderResult) {
        UUID beerOrderId = validateOrderResult.getOrderId();
        boolean isValid = validateOrderResult.getIsValid();
        log.debug("Process Validation Result for beerOrderId: " + beerOrderId + " Valid? " + isValid);

        //entityManager.flush();// force entity manager to execute persist method

        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(beerOrderId);

        beerOrderOptional.ifPresentOrElse(beerOrder -> {

            if (isValid) {

                sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.VALIDATION_PASSED);
                //wait for status change
                //awaitForStatus(beerOrderId, BeerOrderStatusEnum.VALIDATED);

                //  interceptor saved the previous one and for hibernate the version of the two object are different
                // so this is the previous state one
                BeerOrder validatedBeerOrder = beerOrderRepository.findById(beerOrderId)
                        .orElseThrow( () -> new RuntimeException("beerOrderId:  " + beerOrderId + " not found"));

                log.debug("Validation passed for beerOrderId {}", validatedBeerOrder.getId());
                sendBeerOrderEvent(validatedBeerOrder, BeerOrderEventEnum.ALLOCATE_ORDER);
            } else {
                sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.VALIDATION_FAILED);
            }

        }, () -> log.error("Order not found for Id " + beerOrderId));
    }

    private void awaitForStatus(UUID beerOrderId, BeerOrderStatusEnum statusEnum) {

        AtomicBoolean found = new AtomicBoolean(false);
        AtomicInteger loopCount = new AtomicInteger(0);

        while (!found.get()) {
            if (loopCount.incrementAndGet() > 10) {
                found.set(true);
                log.debug("Loop Retries exceeded");
            }

            beerOrderRepository.findById(beerOrderId).ifPresentOrElse(beerOrder -> {
                if (beerOrder.getOrderStatus().equals(statusEnum)) {
                    found.set(true);
                    log.debug("Order Found");
                } else {
                    log.debug("Order Status Not Equal. Expected: " + statusEnum.name() + " Found: " + beerOrder.getOrderStatus().name());
                }
            }, () -> log.debug("Order Id Not Found"));

            if (!found.get()) {
                try {
                    log.debug("Sleeping for retry");
                    Thread.sleep(100);
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
    }

    @Transactional
    @Override
    public void beerOrderAllocationPendingInventory(BeerOrderDto beerOrderDto) {
        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(beerOrderDto.getId());

        log.debug("process order allocation pending");

        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.ALLOCATION_NO_INVENTORY);
            updateAllocationQty(beerOrderDto);
        }, () -> log.error("Order nor found. Id " + beerOrderDto.getId()));
    }

    @Transactional
    @Override
    public void beerOrderAllocationSucceeded(BeerOrderDto beerOrderDto) {
        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(beerOrderDto.getId());

        log.debug("process order allocation success");

        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.ALLOCATION_SUCCESS);
            updateAllocationQty(beerOrderDto);
        }, () -> log.error("Order nor found. Id " + beerOrderDto.getId()));
    }

    @Override
    public void beerOderAllocationFailed(BeerOrderDto beerOrderDto) {
        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(beerOrderDto.getId());

        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.ALLOCATION_FAILED);
        }, () -> log.error("Order nor found. Id " + beerOrderDto.getId()));
    }


    @Override
    public void beerOrderPickup(UUID beerOrderId) {
        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(beerOrderId);

        beerOrderOptional.ifPresentOrElse( beerOrder ->
                sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.BEER_ORDER_PICKED_UP)
                , () -> log.error("Order nor found for Id {}", beerOrderId));
    }

    private void sendBeerOrderEvent(BeerOrder beerOrder, BeerOrderEventEnum eventEnum) {
        StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> sm = build(beerOrder);

        Message msg = MessageBuilder.withPayload(eventEnum)
                .setHeader(ORDER_ID_HEADER, beerOrder.getId().toString())
                .build();

        sm.sendEvent(msg);
    }

    private StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> build(BeerOrder beerOrder) {
        StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> sm = stateMachineFactory.getStateMachine(beerOrder.getId());

        sm.stop();

        sm.getStateMachineAccessor()
                .doWithRegion(sma -> {
                    sma.addStateMachineInterceptor(orderStateChangeInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(beerOrder.getOrderStatus(), null, null, null));
                });

        sm.start();

        return sm;
    }

    private void updateAllocationQty(BeerOrderDto beerOrderDto) {
        Optional<BeerOrder> optionalBeerOrder = beerOrderRepository.findById(beerOrderDto.getId());
        log.debug("update allocation qty for {}", beerOrderDto.getId());

        optionalBeerOrder.ifPresentOrElse(allocatedOrder -> {
            log.debug("update allocation: beer order from db {}", allocatedOrder);


            log.debug("update allocation: beer order from jms {}", beerOrderDto);

            allocatedOrder.getBeerOrderLines().forEach(beerOrderLine -> {
                beerOrderDto.getBeerOrderLines().forEach(beerOrderLineDto -> {
                    if (beerOrderLine.getId().equals(beerOrderLineDto.getId())) {
                        log.debug("before beerOrderLine.getAllocatedQuantity {}", beerOrderLine.getAllocatedQuantity());
                        beerOrderLine.setAllocatedQuantity(beerOrderLineDto.getAllocatedQuantity());
                        log.debug("after beerOrderLine.getAllocatedQuantity {}", beerOrderLine.getAllocatedQuantity());
                    }
                });
            });
            beerOrderRepository.saveAndFlush(allocatedOrder);
        }, () -> log.error("Beer order not found: {}", beerOrderDto.getId()));
    }

}
