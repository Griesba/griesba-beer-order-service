package com.griesba.brewery.beer.order.service.services.listeners;

import com.griesba.brewery.beer.order.service.services.BeerOrderManager;
import com.griesba.brewery.model.events.ValidateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import static com.griesba.brewery.beer.order.service.config.JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderValidationResultListener {

    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = VALIDATE_ORDER_RESPONSE_QUEUE)
    public void listener(ValidateOrderResult validateOrderResult){

        log.debug("Validation result for JMS listener" + validateOrderResult.getOrderId());

        beerOrderManager.processValidationResult(validateOrderResult);
    }
}
