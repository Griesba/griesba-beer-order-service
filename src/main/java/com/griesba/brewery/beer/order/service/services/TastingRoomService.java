package com.griesba.brewery.beer.order.service.services;

import com.griesba.brewery.beer.order.service.bootstrap.BeerOrderBootStrap;
import com.griesba.brewery.beer.order.service.domain.Customer;
import com.griesba.brewery.beer.order.service.repository.BeerOrderRepository;
import com.griesba.brewery.beer.order.service.repository.CustomerRepository;
import com.griesba.brewery.model.BeerOrderDto;
import com.griesba.brewery.model.BeerOrderLineDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public class TastingRoomService {
    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;
    private final BeerOrderService beerOrderService;
    private final List<String> UPCs = new ArrayList<>(3);

    public TastingRoomService(BeerOrderRepository beerOrderRepository, CustomerRepository customerRepository, BeerOrderService beerOrderService) {
        this.beerOrderRepository = beerOrderRepository;
        this.customerRepository = customerRepository;
        this.beerOrderService = beerOrderService;

        UPCs.add(BeerOrderBootStrap.BEER_UPC1);
        UPCs.add(BeerOrderBootStrap.BEER_UPC2);
        UPCs.add(BeerOrderBootStrap.BEER_UPC3);
    }


    @Transactional
    @Scheduled(fixedRate = 2000)//every 2 seconds
    public void placeTastingRoomOrder() {
        List<Customer> customers = customerRepository.findCustomerByCustomerNameLike(BeerOrderBootStrap.TASTING_ROOM);
        if (customers.size() == 1) {
            placeOrder(customers.get(0));
        } else {
            log.error("Too many or few customer tasting room.");
        }
    }

    private void placeOrder(Customer customer) {
        String upc = getRandomUPC();

        BeerOrderLineDto beerOrderLineDto = new BeerOrderLineDto.BeerOrderLineDtoBuilder()
                .withUpc(upc)
                .withOrderQuantity(new Random().nextInt(6))
                .build();

        List<BeerOrderLineDto> beerOrderLines = new ArrayList<>();
        beerOrderLines.add(beerOrderLineDto);

        BeerOrderDto beerOrder = new BeerOrderDto.BeerOrderDtoBuilder()
                .withCustomerId(customer.getId())
                .withCustomerRef(UUID.randomUUID().toString())
                .withBeerOrderLines(beerOrderLines)
                .build();

        beerOrderService.placeOrder(customer.getId(), beerOrder);
    }

    private String getRandomUPC() {
        return UPCs.get(new Random().nextInt(UPCs.size()));
    }
}
