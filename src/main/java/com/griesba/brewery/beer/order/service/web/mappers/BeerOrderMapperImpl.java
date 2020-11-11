package com.griesba.brewery.beer.order.service.web.mappers;

import com.griesba.brewery.beer.order.service.domain.BeerOrder;
import com.griesba.brewery.beer.order.service.web.model.BeerOrderDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class BeerOrderMapperImpl implements BeerOrderMapper {

    private BeerOrderLineMapper beerOrderLineMapper;
    private DateMapper dateMapper;

    public BeerOrderMapperImpl(BeerOrderLineMapper beerOrderLineMapper, DateMapper dateMapper) {
        this.beerOrderLineMapper = beerOrderLineMapper;
        this.dateMapper = dateMapper;
    }

    @Override
    public BeerOrderDto beerOrderToBeerOrderDto(BeerOrder beerOrder) {
        if (beerOrder == null) {
            return null;
        }
        return BeerOrderDto.builder()
                .customerRef(beerOrder.getCustomerRef())
                .customerId(beerOrder.getCustomer().getId())
                .id(beerOrder.getId())
                .creationDate(dateMapper.asOffsetDateTime(beerOrder.getCreationDate()))
                .lastModificationDate(dateMapper.asOffsetDateTime(beerOrder.getLastModificationDate()))
                .beerOrderLines(beerOrder.getBeerOrderLines().stream().map(beerOrderLineMapper::beerOrderLineToBeerOrderLineDto).collect(Collectors.toList()))
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
