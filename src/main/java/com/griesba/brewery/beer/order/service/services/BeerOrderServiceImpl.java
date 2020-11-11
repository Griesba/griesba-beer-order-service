package com.griesba.brewery.beer.order.service.services;

import com.griesba.brewery.beer.order.service.domain.BeerOrder;
import com.griesba.brewery.beer.order.service.domain.Customer;
import com.griesba.brewery.beer.order.service.domain.OrderStatusEnum;
import com.griesba.brewery.beer.order.service.repository.BeerOrderRepository;
import com.griesba.brewery.beer.order.service.repository.CustomerRepository;
import com.griesba.brewery.beer.order.service.web.mappers.BeerOrderMapper;
import com.griesba.brewery.beer.order.service.web.model.BeerOrderDto;
import com.griesba.brewery.beer.order.service.web.model.BeerOrderPageList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BeerOrderServiceImpl implements BeerOrderService {
    private BeerOrderRepository beerOrderRepository;
    private CustomerRepository customerRepository;
    private BeerOrderMapper beerOrderMapper;
    private ApplicationEventPublisher applicationPushBuilder;

    public BeerOrderServiceImpl(BeerOrderRepository beerOrderRepository, CustomerRepository customerRepository,
                                BeerOrderMapper beerOrderMapper, ApplicationEventPublisher applicationEventPublisher) {
        this.beerOrderRepository = beerOrderRepository;
        this.customerRepository = customerRepository;
        this.beerOrderMapper = beerOrderMapper;
        this.applicationPushBuilder = applicationEventPublisher;
    }

    @Override
    public BeerOrderPageList listOrders(UUID customerId, Pageable pageable) {
        Optional<Customer> optCustomer = customerRepository.findById(customerId);
        if (optCustomer.isPresent()) {
            Page<BeerOrder> beerOrderPage = beerOrderRepository.findAllByCustomer(optCustomer.get(), pageable);
            return new BeerOrderPageList(
                    beerOrderPage.stream().map(beerOrderMapper::beerOrderToBeerOrderDto).collect(Collectors.toList()),
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
            BeerOrder beerOrder = beerOrderMapper.beerOrderDtoToBeerOrder(beerOrderDto);
            beerOrder.setId(null);
            beerOrder.setCustomer(optCustomer.get());
            beerOrder.setOrderStatusEnum(OrderStatusEnum.NEW);
            beerOrder.getBeerOrderLines().forEach(line -> line.setBeerOrder(beerOrder));

            BeerOrder savedBeerOrder = beerOrderRepository.saveAndFlush(beerOrder);

            log.debug("Saved Beer Order: " + beerOrder.getId());

            return beerOrderMapper.beerOrderToBeerOrderDto(savedBeerOrder);
        }

        log.debug("Customer {} not found", customerId);
        throw new RuntimeException("Customer " + customerId + " not found");
    }

    @Override
    public BeerOrderDto getBeerOrder(UUID customerId, UUID orderId) {
        return beerOrderMapper.beerOrderToBeerOrderDto(getOrder(customerId, orderId));
    }

    @Override
    public void pickUpOrder(UUID customerId, UUID orderId) {
        BeerOrder beerOrder = getOrder(customerId, orderId);
        beerOrder.setOrderStatusEnum(OrderStatusEnum.PICKED_UP);
        beerOrderRepository.save(beerOrder);
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
