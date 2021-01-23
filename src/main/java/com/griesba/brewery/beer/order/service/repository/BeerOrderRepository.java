package com.griesba.brewery.beer.order.service.repository;

import com.griesba.brewery.beer.order.service.domain.BeerOrder;
import com.griesba.brewery.beer.order.service.domain.Customer;
import com.griesba.brewery.beer.order.service.domain.BeerOrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.UUID;

public interface BeerOrderRepository extends JpaRepository<BeerOrder, UUID> {

    Page<BeerOrder> findAllByCustomer(Customer customer, Pageable pageable);

    // h2 does not handle uuid very well
    //@Lock(LockModeType.PESSIMISTIC_WRITE)
    //BeerOrder findAllById(UUID uuid);
}
