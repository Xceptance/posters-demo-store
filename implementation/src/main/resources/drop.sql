-- ========================================
-- Posters Demo Store — Drop Schema
-- ========================================
-- Drops all tables in dependency-safe order.
-- Mirrors schema.sql table names.
-- ========================================

-- Drop foreign key constraints first

-- Join tables
alter table orders_order_lineitems drop constraint if exists fk_ooli_order;
alter table orders_order_lineitems drop constraint if exists fk_ooli_lineitem;
alter table customer_orders drop constraint if exists fk_co_customer;
alter table customer_orders drop constraint if exists fk_co_order;
alter table customer_credit_cards drop constraint if exists fk_ccc_customer;
alter table customer_credit_cards drop constraint if exists fk_ccc_card;
alter table customer_billing_addresses drop constraint if exists fk_cba_customer;
alter table customer_billing_addresses drop constraint if exists fk_cba_address;
alter table customer_shipping_addresses drop constraint if exists fk_csa_customer;
alter table customer_shipping_addresses drop constraint if exists fk_csa_address;

-- Order line items
alter table order_lineitems drop constraint if exists fk_order_lineitems_product;
alter table order_lineitems drop constraint if exists fk_order_lineitems_size;
drop index if exists ix_order_lineitems_product;
drop index if exists ix_order_lineitems_size;

-- Orders
alter table orders drop constraint if exists fk_orders_shipping_address;
alter table orders drop constraint if exists fk_orders_billing_address;
alter table orders drop constraint if exists fk_orders_credit_card;
alter table orders drop constraint if exists fk_orders_customer;
drop index if exists ix_orders_shipping_address;
drop index if exists ix_orders_billing_address;
drop index if exists ix_orders_credit_card;
drop index if exists ix_orders_customer;

-- Cart line items
alter table cart_lineitems drop constraint if exists fk_cart_lineitems_product;
alter table cart_lineitems drop constraint if exists fk_cart_lineitems_cart;
alter table cart_lineitems drop constraint if exists fk_cart_lineitems_size;
drop index if exists ix_cart_lineitems_product;
drop index if exists ix_cart_lineitems_cart;
drop index if exists ix_cart_lineitems_size;

-- Carts
alter table carts drop constraint if exists fk_carts_customer;
alter table carts drop constraint if exists fk_carts_shipping_address;
alter table carts drop constraint if exists fk_carts_billing_address;
alter table carts drop constraint if exists fk_carts_credit_card;
drop index if exists ix_carts_shipping_address;
drop index if exists ix_carts_billing_address;
drop index if exists ix_carts_credit_card;

-- Customer
alter table customer drop constraint if exists fk_customer_cart;

-- Localized pricing
alter table localized_price drop constraint if exists fk_lp_product_poster_size;
alter table localized_price drop constraint if exists fk_lp_language;
drop index if exists ix_localized_price_pps;
drop index if exists ix_localized_price_language;

-- Product poster sizes
alter table product_poster_size drop constraint if exists fk_pps_product;
alter table product_poster_size drop constraint if exists fk_pps_size;
drop index if exists ix_product_poster_size_product;
drop index if exists ix_product_poster_size_size;

-- Products
alter table product drop constraint if exists fk_product_name;
alter table product drop constraint if exists fk_product_description_detail;
alter table product drop constraint if exists fk_product_description_overview;
alter table product drop constraint if exists fk_product_sub_category;
alter table product drop constraint if exists fk_product_top_category;
drop index if exists ix_product_sub_category;
drop index if exists ix_product_top_category;

-- Categories
alter table sub_category drop constraint if exists fk_sub_category_name;
alter table sub_category drop constraint if exists fk_sub_category_top_category;
drop index if exists ix_sub_category_top_category;
alter table top_category drop constraint if exists fk_top_category_name;

-- Translations
alter table translation drop constraint if exists fk_translation_original_text;
alter table translation drop constraint if exists fk_translation_language;
drop index if exists ix_translation_original_text;
drop index if exists ix_translation_language;

-- Default text
alter table default_text drop constraint if exists fk_default_text_original_language;
drop index if exists ix_default_text_original_language;

-- ----------------------------------------
-- Drop all tables
-- ----------------------------------------

drop table if exists orders_order_lineitems;
drop table if exists customer_orders;
drop table if exists customer_credit_cards;
drop table if exists customer_billing_addresses;
drop table if exists customer_shipping_addresses;

drop table if exists order_lineitems;
drop table if exists orders;
drop table if exists cart_lineitems;
drop table if exists carts;

drop table if exists localized_price;
drop table if exists product_poster_size;
drop table if exists product;
drop table if exists poster_size;

drop table if exists sub_category;
drop table if exists top_category;

drop table if exists credit_card;
drop table if exists billing_address;
drop table if exists shipping_address;

drop table if exists customer;

drop table if exists translation;
drop table if exists default_text;
drop table if exists supported_language;
