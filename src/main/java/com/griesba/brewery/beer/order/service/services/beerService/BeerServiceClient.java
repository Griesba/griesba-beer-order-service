package com.griesba.brewery.beer.order.service.services.beerService;

import com.griesba.brewery.model.BeerDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;


@Slf4j
@ConfigurationProperties(prefix = "griesba.brewery", ignoreUnknownFields = false)
@Component
public class BeerServiceClient {

    private static final String BEER_PATH_V1 = "/api/v1/beer/";
    public static final String BEER_UPC_SERVICE_PATH = "/api/v1/beer/beerUPC/";
    private static final String BEER_UPC_PATH_V1 = BEER_UPC_SERVICE_PATH + "{upc}";

    private String beerServiceHost;

    private RestTemplate restTemplate;

    public BeerServiceClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public void setBeerServiceHost(String beerServiceHost){
        this.beerServiceHost = beerServiceHost;
    }

    public BeerDto getBeerById(UUID beerId)  {
        String url = beerServiceHost + BEER_PATH_V1 + beerId.toString();
        try {

            return restTemplate.getForObject(url, BeerDto.class);

        }catch (Exception e) {
            log.error("Get beer by id failed {}", e.getMessage());
            return null;
        }

    }

    public BeerDto getBeerByUpc(String upc) {
        String url = beerServiceHost + BEER_UPC_PATH_V1;
       try {

           log.info("Querying {} with upc {}", url, upc);

           ResponseEntity<BeerDto> responseEntity = restTemplate.exchange(
                   url,
                   HttpMethod.GET,
                   null,
                   new ParameterizedTypeReference<BeerDto>(){},
                   upc);
           return responseEntity.getBody();
       }catch (Exception e) {
           log.error("Get beer by upc failed {}", e.getMessage());
           return null;
       }

    }
}
