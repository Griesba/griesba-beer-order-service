package com.griesba.brewery.beer.order.service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.griesba.brewery.beer.order.service.domain.BeerOrder;
import com.griesba.brewery.beer.order.service.domain.BeerOrderLine;
import com.griesba.brewery.beer.order.service.domain.BeerOrderStatusEnum;
import com.griesba.brewery.beer.order.service.domain.Customer;
import com.griesba.brewery.beer.order.service.repository.BeerOrderRepository;
import com.griesba.brewery.beer.order.service.repository.CustomerRepository;
import com.griesba.brewery.beer.order.service.services.beerService.BeerServiceClient;
import com.griesba.brewery.model.BeerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestPropertySource(properties = "app.scheduling.enable=false")
@ExtendWith(WireMockExtension.class)
@SpringBootTest
class BeerOrderManagerImplIT {

    @Autowired
    BeerOrderManager beerOrderManager;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerOrderService beerOrderService;

    @Autowired
    WireMockServer wireMockServer;

    Customer testCustomer;

    @Autowired
    ObjectMapper objectMapper;

    UUID beerId = UUID.randomUUID();

    @TestConfiguration
    static class RestTemplateBuilderProvider {

        @Bean(destroyMethod = "stop")
        public WireMockServer wireMockServer() {
            WireMockServer server = with(wireMockConfig().port(8083));
            server.start();
            return server;
        }
    }

    @BeforeEach
    void setUp() {

        testCustomer = customerRepository.save(
                Customer.builder()
                        .customerName("Test customer")
                        .build());
    }

    @Test
    public void testNewToAllocated() throws JsonProcessingException, InterruptedException {
        BeerDto beerDto = new BeerDto.BeerDtoBuilder<>().withId(beerId).withUpc("12345").build();

        wireMockServer.stubFor(
                WireMock.get(BeerServiceClient.BEER_UPC_SERVICE_PATH + "12345")
                        .willReturn(WireMock.okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();

        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

       // wait for JMS processing to complete
        await().atMost(Duration.ofMillis(1495)).untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();

            assertEquals(BeerOrderStatusEnum.ALLOCATION_PENDING, foundOrder.getOrderStatusEnum());
        });

        //await().atMost(Duration.ofMillis(1400)).until( () -> BeerOrderStatusEnum.ALLOCATION_PENDING == beerOrderRepository.findById(beerDto.getId()).get().getOrderStatusEnum());

        await().atMost(Duration.ofMillis(1500)).untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            BeerOrderLine beerOrderLine = foundOrder.getBeerOrderLines().iterator().next();

            assertEquals(beerOrderLine.getOrderQuantity(), beerOrderLine.getAllocatedQuantity());
        });



        BeerOrder  savedBeerOrder2 = beerOrderRepository.findById(savedBeerOrder.getId()).get();

        assertNotNull(savedBeerOrder2);

        assertEquals(BeerOrderStatusEnum.ALLOCATED, savedBeerOrder2.getOrderStatusEnum());

        savedBeerOrder2.getBeerOrderLines().forEach(beerOrderLine -> {
            assertEquals(beerOrderLine.getOrderQuantity(), beerOrderLine.getAllocatedQuantity());
        });
    }

    private BeerOrder createBeerOrder() {
        BeerOrder beerOrder = BeerOrder.builder()
                .customer(testCustomer)
                .build();

        Set<BeerOrderLine> lines = new HashSet<>();
        lines.add(BeerOrderLine.builder()
                .beerId(beerId)
                .upc("12345")
                .beerOrder(beerOrder)
                .orderQuantity(1)
                .build());
        beerOrder.setBeerOrderLines(lines);
        return beerOrder;
    }
}