package com.griesba.brewery.beer.order.service.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
                     BeerOrderStatusEnum orderStatusEnum, String orderStatusCallbackUrl) {
        super(id, version, creationDate, lastModificationDate);
        this.customerRef = customerRef;
        this.customer = customer;
        this.beerOrderLines = beerOrderLines;
        this.orderStatusEnum = orderStatusEnum;
        this.orderStatusCallbackUrl = orderStatusCallbackUrl;
    }

    private String customerRef;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "beerOrder", cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    private Set<BeerOrderLine> beerOrderLines;

    private BeerOrderStatusEnum orderStatusEnum = BeerOrderStatusEnum.NEW;
    private String orderStatusCallbackUrl;



}
