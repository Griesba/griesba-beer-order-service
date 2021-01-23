package com.griesba.brewery.beer.order.service.statemachine;

import com.griesba.brewery.beer.order.service.domain.BeerOrder;
import com.griesba.brewery.beer.order.service.domain.BeerOrderEventEnum;
import com.griesba.brewery.beer.order.service.domain.BeerOrderStatusEnum;
import com.griesba.brewery.beer.order.service.repository.BeerOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.griesba.brewery.beer.order.service.services.BeerOrderManagerImpl.ORDER_ID_HEADER;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStateChangeInterceptor extends StateMachineInterceptorAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final BeerOrderRepository beerOrderRepository;

    @Transactional
    @Override
    public void preStateChange(State<BeerOrderStatusEnum, BeerOrderEventEnum> state,
                               Message<BeerOrderEventEnum> message,
                               Transition<BeerOrderStatusEnum, BeerOrderEventEnum> transition,
                               StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachine) {
        log.debug("Pre-State Change");
        Optional.ofNullable(message).flatMap(msg -> Optional.ofNullable(String.class.cast(msg.getHeaders().getOrDefault(ORDER_ID_HEADER, ""))))
                .ifPresent(beerOrderId -> {
                    log.info("Saving state for order id: " + beerOrderId + " status:  "+ state.getId());

                    BeerOrder beerOrder = beerOrderRepository.getOne(UUID.fromString(beerOrderId));
                    beerOrder.setOrderStatus(state.getId());
                    //save and remove from memory
                    beerOrderRepository.saveAndFlush(beerOrder);
                });
    }
}

