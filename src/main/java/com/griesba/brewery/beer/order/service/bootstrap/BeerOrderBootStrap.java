package com.griesba.brewery.beer.order.service.bootstrap;

import com.griesba.brewery.beer.order.service.domain.Customer;
import com.griesba.brewery.beer.order.service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderBootStrap implements CommandLineRunner {

    public static final String TASTING_ROOM = "Tasting room";
    public static final String BEER_UPC1 = "0631234200036";
    public static final String BEER_UPC2 = "0631234300019";
    public static final String BEER_UPC3 = "0083783375213";

    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        loadCustomerData();
    }

    private void loadCustomerData() {
        if (customerRepository.count() == 0) {
            Customer customer = customerRepository.save(Customer.builder()
                    .customerName(TASTING_ROOM)
                    .apiKey(UUID.randomUUID())
                    .build());

            log.info("Tasting room Customer saved with id {}", customer.getId());
        }
    }
}
