package com.griesba.brewery.beer.order.service.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class BeerDto  implements Serializable {

    private static final long serialVersionUID = -5531835456870278606L;

    private UUID id;
    private String name;
    private Long version;
    private String style;
    private String upc;
    private double price;
    private Integer quantityToBrew;
    private Integer minOnHand;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    private OffsetDateTime createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    private OffsetDateTime modificationDate;

    private Integer quantityOnHand;

    @Builder
    public BeerDto(UUID id, String name, Long version, String style, String upc,
                   double price, Integer quantityToBrew, Integer minOnHand, OffsetDateTime createdDate,
                   OffsetDateTime modificationDate, Integer quantityOnHand) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.style = style;
        this.upc = upc;
        this.price = price;
        this.quantityToBrew = quantityToBrew;
        this.minOnHand = minOnHand;
        this.createdDate = createdDate;
        this.modificationDate = modificationDate;
        this.quantityOnHand = quantityOnHand;
    }
}
