package com.griesba.brewery.beer.order.service.web.mappers;

import com.griesba.brewery.beer.order.service.domain.BeerOrder;
import com.griesba.brewery.beer.order.service.services.beerService.BeerServiceClient;
import com.griesba.brewery.model.BeerOrderDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class BeerOrderMapperImpl implements BeerOrderMapper {

    private BeerOrderLineMapper beerOrderLineMapper;
    private DateMapper dateMapper;
    private BeerServiceClient beerServiceClient;

    public BeerOrderMapperImpl(BeerOrderLineMapper beerOrderLineMapper, DateMapper dateMapper, BeerServiceClient beerServiceClient) {
        this.beerOrderLineMapper = beerOrderLineMapper;
        this.dateMapper = dateMapper;
        this.beerServiceClient = beerServiceClient;
    }

    @Override
    public BeerOrderDto beerOrderToBeerOrderDto(BeerOrder beerOrder) {
        if (beerOrder == null) {
            return null;
        }
        return new BeerOrderDto.BeerOrderDtoBuilder()
                .withCustomerRef(beerOrder.getCustomerRef())
                .withCustomerId(beerOrder.getCustomer().getId())
                .withId(beerOrder.getId())
                .withCreationDate(dateMapper.asOffsetDateTime(beerOrder.getCreationDate()))
                .withLastModificationDate(dateMapper.asOffsetDateTime(beerOrder.getLastModificationDate()))
                .withBeerOrderLines(beerOrder.getBeerOrderLines().stream().map(beerOrderLineMapper::beerOrderLineToBeerOrderLineDto).collect(Collectors.toList()))
                .build();
    }

    @Override
    public BeerOrder beerOrderDtoToBeerOrder(BeerOrderDto beerOrderDto) {
        if (beerOrderDto == null) {
            return null;
        } else {
            return BeerOrder.builder()
                    .id(beerOrderDto.getId())
                    .customerRef(beerOrderDto.getCustomerRef())
                    .creationDate(dateMapper.asTimestamp(beerOrderDto.getCreationDate()))
                    .lastModificationDate(dateMapper.asTimestamp(beerOrderDto.getLastModificationDate()))
                    .beerOrderLines(beerOrderDto.getBeerOrderLines().stream().map(beerOrderLineMapper::beerOrderLineDtoToBeerOrderLine).collect(Collectors.toSet()))
                    .build();
        }
    }
}
