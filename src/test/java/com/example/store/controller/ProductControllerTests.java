package com.example.store.controller;

import com.example.store.entity.Order;
import com.example.store.entity.Product;
import com.example.store.mapper.ProductMapper;
import com.example.store.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private ProductMapper productMapper;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setDescription("Test Product");
        product.setOrders(List.of());
    }

    @Test
    void shouldCreateProduct() throws Exception {
        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        when(productMapper.productToProductDTO(product))
                .thenReturn(productDTO(1L, "Test Product", List.of()));

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "description": "Test Product"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test Product"))
                .andExpect(jsonPath("$.orders").isArray());
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        when(productRepository.findAll())
                .thenReturn(List.of(product));

        when(productMapper.productsToProductDTOs(List.of(product)))
                .thenReturn(List.of(
                        productDTO(1L, "Test Product", List.of(10L, 20L))
                ));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Test Product"))
                .andExpect(jsonPath("$[0].orders[0]").value(10))
                .andExpect(jsonPath("$[0].orders[1]").value(20));
    }

    @Test
    void shouldGetProductById() throws Exception {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productMapper.productToProductDTO(product))
                .thenReturn(productDTO(1L, "Test Product", List.of(10L)));

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test Product"))
                .andExpect(jsonPath("$.orders[0]").value(10));
    }

    @Test
    void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/products/999"))
                .andExpect(status().isNotFound());
    }

    private com.example.store.dto.ProductDTO productDTO(
            Long id,
            String description,
            List<Long> orders
    ) {
        com.example.store.dto.ProductDTO dto =
                new com.example.store.dto.ProductDTO();

        dto.setId(id);
        dto.setDescription(description);
        dto.setOrders(orders);

        return dto;
    }
}