-- Create product table
CREATE TABLE product (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL
);

-- Create order-product relationship table
CREATE TABLE order_product (
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,

    CONSTRAINT pk_order_product
        PRIMARY KEY (order_id, product_id),

    CONSTRAINT fk_order_product_order
        FOREIGN KEY (order_id)
        REFERENCES "order" (id),

    CONSTRAINT fk_order_product_product
        FOREIGN KEY (product_id)
        REFERENCES product (id)
);

-- The primary key already indexes (order_id, product_id).
-- Add a separate index for lookups in the reverse direction.
CREATE INDEX idx_order_product_product_id
    ON order_product (product_id);