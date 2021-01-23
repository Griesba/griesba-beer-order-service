package com.griesba.brewery.beer.order.service.domain;

import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class  BeerOrder extends BaseEntity {


    @Builder
    public BeerOrder(UUID id, Long version, Timestamp creationDate, Timestamp lastModificationDate,
                     String customerRef, Customer customer, Set<BeerOrderLine> beerOrderLines,
                     BeerOrderStatusEnum orderStatus, String orderStatusCallbackUrl) {
        super(id, version, creationDate, lastModificationDate);
        this.customerRef = customerRef;
        this.customer = customer;
        this.beerOrderLines = beerOrderLines;
        this.orderStatus = orderStatus;
        this.orderStatusCallbackUrl = orderStatusCallbackUrl;
    }

    private String customerRef;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "beerOrder", cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    private Set<BeerOrderLine> beerOrderLines;

    @Enumerated(EnumType.STRING)
    private BeerOrderStatusEnum orderStatus = BeerOrderStatusEnum.NEW;

    private String orderStatusCallbackUrl;

    @Override
    public String toString() {
        return "BeerOrder{" +
                "customerRef='" + customerRef + '\'' +
                ", customer=" + customer +
                ", beerOrderLines=" + beerOrderLines +
                ", orderStatus=" + orderStatus +
                ", orderStatusCallbackUrl='" + orderStatusCallbackUrl + '\'' +
                '}';
    }
}
