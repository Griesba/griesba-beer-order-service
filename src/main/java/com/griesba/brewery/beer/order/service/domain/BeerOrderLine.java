package com.griesba.brewery.beer.order.service.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
public class BeerOrderLine extends BaseEntity {

    @ManyToOne
    private BeerOrder beerOrder;

    private UUID beerId;

    private String upc;

    private int orderQuantity;

    private int quantityOnHand = 0;

    private int allocatedQuantity = 0;

    @Builder
    public BeerOrderLine(UUID id, Long version, Timestamp creationDate, Timestamp lastModificationDate,
                         BeerOrder beerOrder, UUID beerId, String upc, int quantityOnHand, int allocatedQuantity,
                         int orderQuantity) {
        super(id, version, creationDate, lastModificationDate);
        this.beerOrder = beerOrder;
        this.beerId = beerId;
        this.upc = upc;
        this.quantityOnHand = quantityOnHand;
        this.allocatedQuantity = allocatedQuantity;
        this.orderQuantity = orderQuantity;
    }
}
