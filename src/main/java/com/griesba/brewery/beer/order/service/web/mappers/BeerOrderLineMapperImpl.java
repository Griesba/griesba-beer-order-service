package com.griesba.brewery.beer.order.service.web.mappers;

import com.griesba.brewery.beer.order.service.domain.BeerOrderLine;
import com.griesba.brewery.beer.order.service.web.model.BeerOrderLineDto;
import org.springframework.stereotype.Component;

@Component
public class BeerOrderLineMapperImpl implements BeerOrderLineMapper {

    private DateMapper dateMapper;

    public BeerOrderLineMapperImpl(DateMapper dateMapper) {
        this.dateMapper = dateMapper;
    }

    @Override
    public BeerOrderLineDto beerOrderLineToBeerOrderLineDto(BeerOrderLine line) {
        if (line == null) {
            return null;
        }

        return BeerOrderLineDto.builder()
            .id(line.getId())
            .creationDate(this.dateMapper.asOffsetDateTime(line.getCreationDate()))
            .lastModificationDate(this.dateMapper.asOffsetDateTime(line.getLastModificationDate()))
            .beerId(line.getBeerId())
            .orderQuantity(line.getQuantityOnHand())
             .build();
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
            .quantityOnHand(dto.getOrderQuantity())
            .build();

    }
}
