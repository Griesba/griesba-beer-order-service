package com.griesba.brewery.beer.order.service.web.mappers;

import com.griesba.brewery.beer.order.service.domain.BeerOrderLine;
import com.griesba.brewery.beer.order.service.services.beerService.BeerServiceClient;
import com.griesba.brewery.model.BeerDto;
import com.griesba.brewery.model.BeerOrderLineDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class BeerOderLineMapperDecorator implements BeerOrderLineMapper{
    BeerOrderLineMapper mapper;
    BeerServiceClient beerServiceClient;

    @Autowired
    public void setBeerServiceClient(BeerServiceClient beerServiceClient) {
        this.beerServiceClient = beerServiceClient;
    }

    @Autowired
    @Qualifier("delegate")
    public void setMapper(BeerOrderLineMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public BeerOrderLineDto beerOrderLineToDto(BeerOrderLine beerOrderLine){
        BeerOrderLineDto beerOrderLineDto = mapper.beerOrderLineToDto(beerOrderLine);

        BeerDto beerDto = beerServiceClient.getBeerByUpc(beerOrderLine.getUpc());

        if (beerDto != null) {
            beerOrderLineDto.setStyle(beerDto.getStyle());
            beerOrderLineDto.setBeerId(beerDto.getId());
            beerOrderLineDto.setVersion(beerDto.getVersion());
            beerOrderLineDto.setBeerName(beerDto.getName());
        }
        return beerOrderLineDto;
    }

    @Override
    public BeerOrderLine dtoToBeerOrderLine(BeerOrderLineDto beerOrderLineDto) {
        return mapper.dtoToBeerOrderLine(beerOrderLineDto);
    }
}
