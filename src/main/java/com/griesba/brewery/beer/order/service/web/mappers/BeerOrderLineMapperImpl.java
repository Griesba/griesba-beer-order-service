package com.griesba.brewery.beer.order.service.web.mappers;

import com.griesba.brewery.beer.order.service.domain.BeerOrderLine;
import com.griesba.brewery.beer.order.service.services.beerService.BeerServiceClient;
import com.griesba.brewery.model.BeerDto;
import com.griesba.brewery.model.BeerOrderLineDto;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BeerOrderLineMapperImpl implements BeerOrderLineMapper {

    private DateMapper dateMapper;
    private BeerServiceClient beerServiceClient;

    public BeerOrderLineMapperImpl(DateMapper dateMapper, BeerServiceClient beerServiceClient) {
        this.dateMapper = dateMapper;
        this.beerServiceClient = beerServiceClient;
    }

    @Override
    public BeerOrderLineDto beerOrderLineToBeerOrderLineDto(BeerOrderLine line) {
        if (line == null) {
            return null;
        }

        BeerDto beerDto = beerServiceClient.getBeerByUpc(line.getUpc());

        BeerOrderLineDto.BeerOrderLineDtoBuilder beerOrderLineDtoBuilder = new BeerOrderLineDto.BeerOrderLineDtoBuilder<>()
                .withId(line.getId())
                .withCreationDate(this.dateMapper.asOffsetDateTime(line.getCreationDate()))
                .withLastModificationDate(this.dateMapper.asOffsetDateTime(line.getLastModificationDate()))
                .withUpc(line.getUpc())
                .withOrderQuantity(line.getQuantityOnHand());

        if (beerDto != null) {
            beerOrderLineDtoBuilder
                    .withStyle(beerDto.getStyle())
                    .withBeerId(beerDto.getId())
                    .withVersion(beerDto.getVersion())
                    .withBeerName(beerDto.getName());
        }

        return beerOrderLineDtoBuilder.build();
    }

    @Override
    public BeerOrderLine beerOrderLineDtoToBeerOrderLine(BeerOrderLineDto dto) {
        if (dto == null) {
            return null;
        }

        return BeerOrderLine.builder()
                .id(dto.getId())
                .creationDate(this.dateMapper.asTimestamp(dto.getCreationDate()))
                .lastModificationDate(this.dateMapper.asTimestamp(dto.getLastModificationDate()))
                .beerId(dto.getBeerId())
                .upc(dto.getUpc())
                .quantityOnHand(dto.getOrderQuantity())
                .build();
    }
}
