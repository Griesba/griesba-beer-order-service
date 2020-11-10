package com.griesba.brewery.beer.order.service.bootstrap;

import com.griesba.brewery.beer.order.service.domain.Customer;
import com.griesba.brewery.beer.order.service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class BeerOderBootStrap implements CommandLineRunner {

    private static final String TASTING_ROOM = "Tasting room";
    private static final String BEER_UPC1 = "8-0823490818-12";
    private static final String BEER_UPC2 = "8-0823490818-13";
    private static final String BEER_UPC3 = "8-0823490818-14";

    private CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        loadCustomerData();
    }

    private void loadCustomerData() {
        if (customerRepository.count() == 0) {
            customerRepository.save(Customer.builder()
                    .customerName(TASTING_ROOM)
                    .apiKey(UUID.randomUUID())
                    .build());
        }
    }
}
