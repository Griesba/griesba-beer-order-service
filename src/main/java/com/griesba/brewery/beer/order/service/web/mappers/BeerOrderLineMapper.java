package com.griesba.brewery.beer.order.service.web.mappers;

import com.griesba.brewery.beer.order.service.domain.BeerOrderLine;
import com.griesba.brewery.model.BeerOrderLineDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@DecoratedWith(BeerOderLineMapperDecorator.class)
@Mapper(uses = {DateMapper.class})
public interface BeerOrderLineMapper {

    BeerOrderLineMapper INSTANCE = Mappers.getMapper(BeerOrderLineMapper.class);

    BeerOrderLineDto beerOrderLineToDto(BeerOrderLine beerOrderLine);

    BeerOrderLine dtoToBeerOrderLine(BeerOrderLineDto beerOrderLineDto);
}
