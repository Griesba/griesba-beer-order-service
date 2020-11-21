package com.griesba.brewery.beer.order.service.web.mappers;

import com.griesba.brewery.beer.order.service.domain.BeerOrderLine;
import com.griesba.brewery.beer.order.service.services.beerService.BeerServiceClient;
import com.griesba.brewery.beer.order.service.web.model.BeerDto;
import com.griesba.brewery.beer.order.service.web.model.BeerOrderLineDto;
import com.griesba.brewery.beer.order.service.web.model.BeerOrderLineDtoBuilder;
import org.springframework.stereotype.Component;

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

        BeerOrderLineDto.BeerOrderLineDtoBuilder beerOrderLineDtoBuilder = BeerOrderLineDto.builder()
                .id(line.getId())
                .creationDate(this.dateMapper.asOffsetDateTime(line.getCreationDate()))
                .lastModificationDate(this.dateMapper.asOffsetDateTime(line.getLastModificationDate()))
                .upc(line.getUpc())
                .orderQuantity(line.getQuantityOnHand());

        if (beerDto != null) {
            beerOrderLineDtoBuilder
                    .style(beerDto.getStyle())
                    .beerId(beerDto.getId())
                    .version(beerDto.getVersion())
                    .beerName(beerDto.getName());
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
