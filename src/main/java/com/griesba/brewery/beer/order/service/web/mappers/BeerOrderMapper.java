package com.griesba.brewery.beer.order.service.web.mappers;

import com.griesba.brewery.beer.order.service.domain.BeerOrder;
import com.griesba.brewery.beer.order.service.web.model.BeerOrderDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class, BeerOrderLineMapper.class})
public interface BeerOrderMapper {

    BeerOrderDto beerOrderToBeerOrderDto(BeerOrder beerOrder);

    BeerOrder beerOrderDtoToBeerOrder(BeerOrderDto beerOrderDto);
}
