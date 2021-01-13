package com.griesba.brewery.beer.order.service.statemachine.action;

import com.griesba.brewery.beer.order.service.config.JmsConfig;
import com.griesba.brewery.beer.order.service.domain.BeerOrderEventEnum;
import com.griesba.brewery.beer.order.service.domain.BeerOrderStatusEnum;
import com.griesba.brewery.beer.order.service.services.BeerOrderManagerImpl;
import com.griesba.brewery.model.events.AllocationFailureEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Component
public class AllocationFailureAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final JmsTemplate jmsTemplate;

    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {
        String beerOrderId = (String) stateContext.getMessage().getHeaders().get(BeerOrderManagerImpl.ORDER_ID_HEADER);

        jmsTemplate.convertAndSend(
                JmsConfig.ALLOCATE_FAILURE_QUEUE,
                new AllocationFailureEvent.AllocationFailureEventBuilder<>()
                        .withOrderId(UUID.fromString(beerOrderId))
                        .build()
        );

        log.debug("Send allocation failure message to queue for order Id " + beerOrderId);
    }
}
