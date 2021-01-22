package com.griesba.brewery.beer.order.service.statemachine.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.griesba.brewery.beer.order.service.config.JmsConfig;
import com.griesba.brewery.beer.order.service.domain.BeerOrder;
import com.griesba.brewery.beer.order.service.domain.BeerOrderEventEnum;
import com.griesba.brewery.beer.order.service.domain.BeerOrderStatusEnum;
import com.griesba.brewery.beer.order.service.repository.BeerOrderRepository;
import com.griesba.brewery.beer.order.service.web.mappers.BeerOrderMapper;
import com.griesba.brewery.model.BeerOrderDto;
import com.griesba.brewery.model.events.ValidateBeerOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.griesba.brewery.beer.order.service.services.BeerOrderManagerImpl.ORDER_ID_HEADER;

@Slf4j
@RequiredArgsConstructor
@Component
public class ValidateOrderRequestAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final BeerOrderRepository beerOrderRepository;
    private final JmsTemplate jmsTemplate;
    private final BeerOrderMapper beerOrderMapper;

    @Transactional
    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {
        String beerOrderId = (String) stateContext.getMessage().getHeaders().get(ORDER_ID_HEADER);

        log.info("Receive validate order request event for beerOrderId {}", beerOrderId);

        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(UUID.fromString(beerOrderId));

        ValidateBeerOrderRequest.ValidateBeerOrderRequestBuilder validateBORBuilder = new ValidateBeerOrderRequest.ValidateBeerOrderRequestBuilder();


        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            ValidateBeerOrderRequest beerOrderValidationRequest =  validateBORBuilder.withBeerOrderDto(beerOrderMapper.beerOrderToBeerOrderDto(beerOrder)).build();
            jmsTemplate.convertAndSend(
                    JmsConfig.VALIDATE_ORDER_QUEUE,
                    beerOrderValidationRequest
                    );

            try {
                log.debug("Send validation JMS request to queue for order Id {}", beerOrderId);
                log.info(new ObjectMapper().writeValueAsString(beerOrderValidationRequest));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        }, () -> log.error("Order nor found. Id " + beerOrderId));
    }
}
