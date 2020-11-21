package com.griesba.brewery.beer.order.service.web.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BeerOrderLineDto extends BaseItem {
    private String upc;
    private String beerName;
    private UUID beerId;
    private String style;
    private Integer orderQuantity = 0;

    @Builder
    public BeerOrderLineDto(UUID id, Long version, OffsetDateTime creationDate, OffsetDateTime lastModificationDate,
                            String upc, String beerName, UUID beerId, Integer orderQuantity, String style) {
        super(id, version, creationDate, lastModificationDate);
        this.upc = upc;
        this.beerName = beerName;
        this.beerId = beerId;
        this.style = style;
        this.orderQuantity = orderQuantity;
    }
}
