package com.griesba.brewery.beer.order.service.web.model;

import com.griesba.brewery.beer.order.service.domain.BeerOrder;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class BeerOrderPageList extends PageImpl<BeerOrderDto> {
    public BeerOrderPageList(List<BeerOrderDto> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public BeerOrderPageList(List<BeerOrderDto> content) {
        super(content);
    }
}
