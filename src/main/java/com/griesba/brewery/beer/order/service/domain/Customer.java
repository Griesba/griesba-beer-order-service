package com.griesba.brewery.beer.order.service.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
public class Customer extends BaseEntity {

    private String customerName;

    @Column(length = 36, columnDefinition = "varchar")
    private UUID apiKey;

    @OneToMany( mappedBy = "customer")
    private List<BeerOder> beerOrders;


    @Builder
    public Customer(UUID id, Long version, Timestamp createdDate,
                    Timestamp lastModifiedDate, String customerName,
                    UUID apiKey, List<BeerOder> beerOrders) {
        super(id, version, createdDate, lastModifiedDate);
        this.customerName = customerName;
        this.apiKey = apiKey;
        this.beerOrders = beerOrders;
    }
}
