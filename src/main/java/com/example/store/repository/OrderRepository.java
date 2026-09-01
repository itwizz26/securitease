package com.example.store.repository;

import com.example.store.entity.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"customer", "products"})
    @Override
    List<Order> findAll();

    @EntityGraph(attributePaths = {"customer", "products"})
    @Override
    Optional<Order> findById(Long id);
}
