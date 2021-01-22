package com.griesba.brewery.beer.order.service.repository;

import com.griesba.brewery.beer.order.service.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    List<Customer> findCustomerByCustomerNameLike(String customerName);
}
