package com.griesba.brewery.beer.order.service.services.component;

import com.griesba.brewery.beer.order.service.config.JmsConfig;
import com.griesba.brewery.model.events.ValidateBeerOrderRequest;
import com.griesba.brewery.model.events.ValidateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderValidationListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
    public void listener(Message message){

        boolean isValid = true;
        boolean sendResponse = true;

        ValidateBeerOrderRequest request = (ValidateBeerOrderRequest) message.getPayload();
        log.debug("################ I run ##############");

        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE,
                new ValidateOrderResult.ValidateOrderResultBuilder()
                        .withIsValid(isValid)
                        .withOrderId(request.getBeerOrderDto().getId())
                        .build());
    }
}
