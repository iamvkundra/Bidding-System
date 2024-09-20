package com.intuit.auction.service.repositories;

import com.intuit.auction.service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}
