CREATE TABLE users (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    email VARCHAR(320) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    role VARCHAR(50) NOT NULL,
    active BIT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB;

CREATE TABLE customers (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    first_name VARCHAR(120),
    last_name VARCHAR(120),
    dni VARCHAR(16),
    email VARCHAR(320),
    phone VARCHAR(50),
    notes VARCHAR(500),
    CONSTRAINT uk_customers_dni UNIQUE (dni),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE categories (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    active BIT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE products (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    sku VARCHAR(50) NOT NULL,
    isbn VARCHAR(20),
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    price DECIMAL(19, 2) NOT NULL,
    active BIT(1) NOT NULL DEFAULT 1,
    category_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT uk_products_sku UNIQUE (sku),
    CONSTRAINT uk_products_isbn UNIQUE (isbn)
) ENGINE=InnoDB;

CREATE INDEX idx_products_sku ON products (sku);
CREATE INDEX idx_products_title ON products (title);

CREATE TABLE inventory_movements (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    movement_type VARCHAR(20) NOT NULL,
    quantity INT NOT NULL,
    movement_date DATETIME(6) NOT NULL,
    reference VARCHAR(120),
    notes VARCHAR(500),
    product_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_inventory_movements_product FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB;

CREATE TABLE reservations (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    code VARCHAR(60) NOT NULL,
    status VARCHAR(20) NOT NULL,
    reservation_date DATETIME(6) NOT NULL,
    expiration_date DATETIME(6),
    total_amount DECIMAL(19, 2) NOT NULL,
    notes VARCHAR(500),
    customer_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_reservations_customer FOREIGN KEY (customer_id) REFERENCES customers (id),
    CONSTRAINT uk_reservations_code UNIQUE (code)
) ENGINE=InnoDB;

CREATE INDEX idx_reservations_code ON reservations (code);

CREATE TABLE reservation_items (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    reservation_id BINARY(16) NOT NULL,
    product_id BINARY(16) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(19, 2) NOT NULL,
    total_price DECIMAL(19, 2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_reservation_items_reservation FOREIGN KEY (reservation_id) REFERENCES reservations (id) ON DELETE CASCADE,
    CONSTRAINT fk_reservation_items_product FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB;

CREATE TABLE sales (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    status VARCHAR(20) NOT NULL,
    sale_date DATETIME(6) NOT NULL,
    total_amount DECIMAL(19, 2) NOT NULL,
    tax_amount DECIMAL(19, 2),
    discount_amount DECIMAL(19, 2),
    notes VARCHAR(500),
    customer_id BINARY(16),
    cashier_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_sales_customer FOREIGN KEY (customer_id) REFERENCES customers (id),
    CONSTRAINT fk_sales_cashier FOREIGN KEY (cashier_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE INDEX idx_sales_created_at ON sales (created_at);

CREATE TABLE sale_items (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    sale_id BINARY(16) NOT NULL,
    product_id BINARY(16) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(19, 2) NOT NULL,
    total_price DECIMAL(19, 2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_sale_items_sale FOREIGN KEY (sale_id) REFERENCES sales (id) ON DELETE CASCADE,
    CONSTRAINT fk_sale_items_product FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB;

CREATE TABLE audit_logs (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    entity_name VARCHAR(150) NOT NULL,
    entity_id VARCHAR(64) NOT NULL,
    action VARCHAR(50) NOT NULL,
    performed_by VARCHAR(320),
    performed_at DATETIME(6) NOT NULL,
    details VARCHAR(2000),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

