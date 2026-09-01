package com.example.store.repository;

import com.example.store.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = "orders")
    @Override
    List<Product> findAll();

    @EntityGraph(attributePaths = "orders")
    @Override
    Optional<Product> findById(Long id);
}