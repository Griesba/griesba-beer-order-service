package com.griesba.brewery.beer.order.service.statemachine.action;

import com.griesba.brewery.beer.order.service.config.JmsConfig;
import com.griesba.brewery.beer.order.service.domain.BeerOrder;
import com.griesba.brewery.beer.order.service.domain.BeerOrderEventEnum;
import com.griesba.brewery.beer.order.service.domain.BeerOrderStatusEnum;
import com.griesba.brewery.beer.order.service.repository.BeerOrderRepository;
import com.griesba.brewery.beer.order.service.services.BeerOrderManagerImpl;
import com.griesba.brewery.beer.order.service.web.mappers.BeerOrderMapper;
import com.griesba.brewery.model.events.AllocateOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class AllocateOrderAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;
    private final JmsTemplate jmsTemplate;

    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {

        String beerOrderId = (String) stateContext.getMessage().getHeaders().get(BeerOrderManagerImpl.ORDER_ID_HEADER);

        log.info("Allocate ({}) order action for beerOrderId {}", stateContext.getSource().getId(), beerOrderId);

        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(UUID.fromString(beerOrderId));

        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_QUEUE,
                    new AllocateOrderRequest.AllocateOrderRequestBuilder()
                            .withBeerOrderDto(beerOrderMapper.beerOrderToDto(beerOrder))
                            .build());

            log.debug("Sent allocation request to ALLOCATE_ORDER_QUEUE for order Id " + beerOrderId);
        }, () -> log.error("Order nor found. Id " + beerOrderId));
    }
}
