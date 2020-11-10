package com.griesba.brewery.beer.order.service.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
public class BeerOder extends BaseEntity {

    @Builder
    public BeerOder(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate,
                    String customerRef, Customer customer, List<BeerOderLine> beerOderLines,
                    OrderStatusEnum orderStatusEnum, String orderStatusCallbackUrl) {
        super(id, version, createdDate, lastModifiedDate);
        this.customerRef = customerRef;
        this.customer = customer;
        this.beerOderLines = beerOderLines;
        this.orderStatusEnum = orderStatusEnum;
        this.orderStatusCallbackUrl = orderStatusCallbackUrl;
    }

    private String customerRef;

    @ManyToOne
    private Customer customer;
    private List<BeerOderLine> beerOderLines;
    private OrderStatusEnum orderStatusEnum = OrderStatusEnum.NEW;
    private String orderStatusCallbackUrl;
}
