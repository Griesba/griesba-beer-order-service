package com.griesba.brewery.beer.order.service.services;

import com.griesba.brewery.beer.order.service.domain.BeerOrder;
import com.griesba.brewery.beer.order.service.domain.Customer;
import com.griesba.brewery.beer.order.service.domain.BeerOrderStatusEnum;
import com.griesba.brewery.beer.order.service.repository.BeerOrderRepository;
import com.griesba.brewery.beer.order.service.repository.CustomerRepository;
import com.griesba.brewery.beer.order.service.web.mappers.BeerOrderMapper;
import com.griesba.brewery.model.BeerOrderDto;
import com.griesba.brewery.model.BeerOrderPageList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BeerOrderServiceImpl implements BeerOrderService {
    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;
    private final BeerOrderMapper beerOrderMapper;
    private final ApplicationEventPublisher applicationPushBuilder;
    private final BeerOrderManager beerOrderManager;


    @Override
    public BeerOrderPageList listOrders(UUID customerId, Pageable pageable) {
        Optional<Customer> optCustomer = customerRepository.findById(customerId);
        if (optCustomer.isPresent()) {
            Page<BeerOrder> beerOrderPage = beerOrderRepository.findAllByCustomer(optCustomer.get(), pageable);
            return new BeerOrderPageList(
                    beerOrderPage.stream().map(beerOrderMapper::beerOrderToDto).collect(Collectors.toList()),
                    PageRequest.of(beerOrderPage.getPageable().getPageNumber(), beerOrderPage.getPageable().getPageSize()),
                    beerOrderPage.getTotalElements());
        }
        return null;
    }

    //@Transactional
    @Override
    public BeerOrderDto placeOrder(UUID customerId, BeerOrderDto beerOrderDto) {
        Optional<Customer> optCustomer = customerRepository.findById(customerId);

        if (optCustomer.isPresent()) {
            BeerOrder beerOrder = beerOrderMapper.dtoToBeerOrder(beerOrderDto);
            beerOrder.setId(null);
            beerOrder.setCustomer(optCustomer.get());
            beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);
            beerOrder.getBeerOrderLines().forEach(line -> line.setBeerOrder(beerOrder));

            BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

            log.debug("Saved Beer Order: {} ", beerOrder);

            return beerOrderMapper.beerOrderToDto(savedBeerOrder);
        }

        log.debug("Customer {} not found", customerId);
        throw new RuntimeException("Customer " + customerId + " not found");
    }

    @Override
    public BeerOrderDto getBeerOrder(UUID customerId, UUID orderId) {
        return beerOrderMapper.beerOrderToDto(getOrder(customerId, orderId));
    }

    @Override
    public void pickUpOrder(UUID customerId, UUID orderId) {
        beerOrderManager.beerOrderPickup(orderId);
    }

    private BeerOrder getOrder(UUID customerId, UUID orderId) {
        Optional<Customer> optCustomer = customerRepository.findById(customerId);

        if (optCustomer.isPresent()) {
            Optional<BeerOrder> optBeerOrder = beerOrderRepository.findById(orderId);
            if (optBeerOrder.isPresent()) {
                BeerOrder beerOrder = optBeerOrder.get();
                if (beerOrder.getCustomer().getId().equals(customerId)) {
                    return beerOrder;
                }
            }
            throw new RuntimeException("Beer order " + orderId + " not found.");
        }
        throw new RuntimeException("Customer for " + customerId + " not found.");
    }
}
