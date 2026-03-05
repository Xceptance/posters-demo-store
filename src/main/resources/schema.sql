-- ========================================
-- Posters Demo Store — Schema
-- ========================================
-- This file documents the database schema.
-- Hibernate generates DDL from JPA entities (ddl-auto: update).
-- Table/column names match the @Table/@Column annotations.
-- ========================================

-- ----------------------------------------
-- Localization
-- ----------------------------------------

create table supported_language (
  id                            integer auto_increment not null,
  language_group                varchar(255),
  precise_name                  varchar(255),
  endonym                       varchar(255),
  precise_endonym               varchar(255),
  disambiguous_endonym          varchar(255),
  code                          varchar(255),
  fallback_code                 varchar(255),
  constraint pk_supported_language primary key (id)
);

create table default_text (
  id                            integer auto_increment not null,
  original_text                 varchar(4096),
  original_language_id          integer,
  constraint pk_default_text primary key (id)
);

create table translation (
  id                            integer auto_increment not null,
  original_text_id              integer not null,
  translation_language_id       integer,
  translation_text              varchar(4096),
  constraint pk_translation primary key (id)
);

-- ----------------------------------------
-- Product Catalog
-- ----------------------------------------

create table top_category (
  id                            integer auto_increment not null,
  name_id                       integer,
  constraint uq_top_category_name_id unique (name_id),
  constraint pk_top_category primary key (id)
);

create table sub_category (
  id                            integer auto_increment not null,
  name_id                       integer,
  top_category_id               integer,
  constraint uq_sub_category_name_id unique (name_id),
  constraint pk_sub_category primary key (id)
);

create table product (
  id                            integer auto_increment not null,
  name_id                       integer,
  description_detail_id         integer,
  description_overview_id       integer,
  image_url                     varchar(255),
  small_image_url               varchar(255),
  medium_image_url              varchar(255),
  large_image_url               varchar(255),
  original_image_url            varchar(255),
  show_in_carousel              boolean default false not null,
  sub_category_id               integer,
  top_category_id               integer,
  minimum_price                 decimal(10,2) not null,
  available_finishes            varchar(255) default 'matte,gloss',
  constraint uq_product_name_id unique (name_id),
  constraint uq_product_description_detail_id unique (description_detail_id),
  constraint uq_product_description_overview_id unique (description_overview_id),
  constraint pk_product primary key (id)
);

create table poster_size (
  id                            integer auto_increment not null,
  width                         integer not null,
  height                        integer not null,
  constraint pk_poster_size primary key (id)
);

create table product_poster_size (
  id                            integer auto_increment not null,
  product_id                    integer,
  size_id                       integer,
  finish                        varchar(255) default 'matte',
  price                         decimal(10,2) not null,
  constraint pk_product_poster_size primary key (id)
);

create table localized_price (
  id                            integer auto_increment not null,
  product_poster_size_id        integer not null,
  language_id                   integer not null,
  price                         decimal(10,2) not null,
  constraint pk_localized_price primary key (id)
);

-- ----------------------------------------
-- Customer & Authentication
-- ----------------------------------------

create table customer (
  id                            uuid not null,
  email                         varchar(255),
  password                      varchar(255),
  name                          varchar(255),
  first_name                    varchar(255),
  cart_id                       uuid,
  constraint uq_customer_email unique (email),
  constraint uq_customer_cart_id unique (cart_id),
  constraint pk_customer primary key (id)
);

-- ----------------------------------------
-- Addresses & Payment
-- ----------------------------------------

create table shipping_address (
  id                            integer auto_increment not null,
  name                          varchar(255),
  first_name                    varchar(255),
  company                       varchar(255),
  address_line                  varchar(255),
  city                          varchar(255),
  state                         varchar(255),
  country                       varchar(255),
  zip                           varchar(255),
  constraint pk_shipping_address primary key (id)
);

create table billing_address (
  id                            integer auto_increment not null,
  name                          varchar(255),
  first_name                    varchar(255),
  company                       varchar(255),
  address_line                  varchar(255),
  city                          varchar(255),
  state                         varchar(255),
  country                       varchar(255),
  zip                           varchar(255),
  constraint pk_billing_address primary key (id)
);

create table credit_card (
  id                            integer auto_increment not null,
  card_number                   varchar(255),
  name                          varchar(255),
  exp_month                     integer not null,
  exp_year                      integer not null,
  constraint pk_credit_card primary key (id)
);

-- ----------------------------------------
-- Shopping Cart
-- ----------------------------------------

create table carts (
  id                            uuid not null,
  customer_id                   uuid,
  shipping_address_id           integer,
  billing_address_id            integer,
  credit_card_id                integer,
  shipping_costs                decimal(10,2) not null,
  sub_total_price               decimal(10,2) not null,
  tax                           decimal(10,2) not null,
  total_tax_price               decimal(10,2) not null,
  total_price                   decimal(10,2) not null,
  constraint uq_carts_customer_id unique (customer_id),
  constraint pk_carts primary key (id)
);

create table cart_lineitems (
  id                            integer auto_increment not null,
  product_id                    integer,
  cart_id                       uuid,
  product_count                 integer not null,
  finish                        varchar(255),
  size_id                       integer,
  price                         decimal(10,2) not null,
  constraint pk_cart_lineitems primary key (id)
);

-- ----------------------------------------
-- Orders
-- ----------------------------------------

create table orders (
  id                            uuid not null,
  order_date                    timestamp,
  shipping_address_id           integer,
  billing_address_id            integer,
  shipping_costs                decimal(10,2) not null,
  sub_total_costs               decimal(10,2) not null,
  total_tax_costs               decimal(10,2) not null,
  tax                           decimal(10,2) not null,
  total_costs                   decimal(10,2) not null,
  credit_card_id                integer,
  customer_id                   uuid,
  constraint pk_orders primary key (id)
);

create table order_lineitems (
  id                            integer auto_increment not null,
  product_id                    integer,
  product_count                 integer not null,
  finish                        varchar(255),
  size_id                       integer,
  price                         decimal(10,2) not null,
  constraint pk_order_lineitems primary key (id)
);

-- ========================================
-- JPA join tables (managed by Hibernate)
-- ========================================

-- Customer → ShippingAddress (OneToMany via join table)
create table customer_shipping_addresses (
  customer_id                   uuid not null,
  shipping_addresses_id         integer not null
);

-- Customer → BillingAddress (OneToMany via join table)
create table customer_billing_addresses (
  customer_id                   uuid not null,
  billing_addresses_id          integer not null
);

-- Customer → CreditCard (OneToMany via join table)
create table customer_credit_cards (
  customer_id                   uuid not null,
  credit_cards_id               integer not null
);

-- Customer → Order (OneToMany via join table)
create table customer_orders (
  customer_id                   uuid not null,
  orders_id                     uuid not null
);

-- Order → OrderProduct (OneToMany via join table)
create table orders_order_lineitems (
  order_id                      uuid not null,
  products_id                   integer not null
);

-- ========================================
-- Indexes
-- ========================================

create index ix_default_text_original_language on default_text (original_language_id);
create index ix_translation_original_text on translation (original_text_id);
create index ix_translation_language on translation (translation_language_id);
create index ix_sub_category_top_category on sub_category (top_category_id);
create index ix_product_sub_category on product (sub_category_id);
create index ix_product_top_category on product (top_category_id);
create index ix_product_poster_size_product on product_poster_size (product_id);
create index ix_product_poster_size_size on product_poster_size (size_id);
create index ix_localized_price_pps on localized_price (product_poster_size_id);
create index ix_localized_price_language on localized_price (language_id);
create index ix_carts_shipping_address on carts (shipping_address_id);
create index ix_carts_billing_address on carts (billing_address_id);
create index ix_carts_credit_card on carts (credit_card_id);
create index ix_cart_lineitems_product on cart_lineitems (product_id);
create index ix_cart_lineitems_cart on cart_lineitems (cart_id);
create index ix_cart_lineitems_size on cart_lineitems (size_id);
create index ix_orders_shipping_address on orders (shipping_address_id);
create index ix_orders_billing_address on orders (billing_address_id);
create index ix_orders_credit_card on orders (credit_card_id);
create index ix_orders_customer on orders (customer_id);
create index ix_order_lineitems_product on order_lineitems (product_id);
create index ix_order_lineitems_size on order_lineitems (size_id);

-- ========================================
-- Foreign Keys
-- ========================================

-- Localization FKs
alter table default_text add constraint fk_default_text_original_language foreign key (original_language_id) references supported_language (id);
alter table translation add constraint fk_translation_original_text foreign key (original_text_id) references default_text (id);
alter table translation add constraint fk_translation_language foreign key (translation_language_id) references supported_language (id);

-- Category FKs
alter table top_category add constraint fk_top_category_name foreign key (name_id) references default_text (id);
alter table sub_category add constraint fk_sub_category_name foreign key (name_id) references default_text (id);
alter table sub_category add constraint fk_sub_category_top_category foreign key (top_category_id) references top_category (id);

-- Product FKs
alter table product add constraint fk_product_name foreign key (name_id) references default_text (id);
alter table product add constraint fk_product_description_detail foreign key (description_detail_id) references default_text (id);
alter table product add constraint fk_product_description_overview foreign key (description_overview_id) references default_text (id);
alter table product add constraint fk_product_sub_category foreign key (sub_category_id) references sub_category (id);
alter table product add constraint fk_product_top_category foreign key (top_category_id) references top_category (id);

-- Product sizing & pricing FKs
alter table product_poster_size add constraint fk_pps_product foreign key (product_id) references product (id);
alter table product_poster_size add constraint fk_pps_size foreign key (size_id) references poster_size (id);
alter table localized_price add constraint fk_lp_product_poster_size foreign key (product_poster_size_id) references product_poster_size (id);
alter table localized_price add constraint fk_lp_language foreign key (language_id) references supported_language (id);

-- Customer FKs
alter table customer add constraint fk_customer_cart foreign key (cart_id) references carts (id);

-- Cart FKs
alter table carts add constraint fk_carts_customer foreign key (customer_id) references customer (id);
alter table carts add constraint fk_carts_shipping_address foreign key (shipping_address_id) references shipping_address (id);
alter table carts add constraint fk_carts_billing_address foreign key (billing_address_id) references billing_address (id);
alter table carts add constraint fk_carts_credit_card foreign key (credit_card_id) references credit_card (id);

-- Cart line items FKs
alter table cart_lineitems add constraint fk_cart_lineitems_product foreign key (product_id) references product (id);
alter table cart_lineitems add constraint fk_cart_lineitems_cart foreign key (cart_id) references carts (id);
alter table cart_lineitems add constraint fk_cart_lineitems_size foreign key (size_id) references poster_size (id);

-- Order FKs
alter table orders add constraint fk_orders_shipping_address foreign key (shipping_address_id) references shipping_address (id);
alter table orders add constraint fk_orders_billing_address foreign key (billing_address_id) references billing_address (id);
alter table orders add constraint fk_orders_credit_card foreign key (credit_card_id) references credit_card (id);
alter table orders add constraint fk_orders_customer foreign key (customer_id) references customer (id);

-- Order line items FKs
alter table order_lineitems add constraint fk_order_lineitems_product foreign key (product_id) references product (id);
alter table order_lineitems add constraint fk_order_lineitems_size foreign key (size_id) references poster_size (id);

-- Join table FKs
alter table customer_shipping_addresses add constraint fk_csa_customer foreign key (customer_id) references customer (id);
alter table customer_shipping_addresses add constraint fk_csa_address foreign key (shipping_addresses_id) references shipping_address (id);
alter table customer_billing_addresses add constraint fk_cba_customer foreign key (customer_id) references customer (id);
alter table customer_billing_addresses add constraint fk_cba_address foreign key (billing_addresses_id) references billing_address (id);
alter table customer_credit_cards add constraint fk_ccc_customer foreign key (customer_id) references customer (id);
alter table customer_credit_cards add constraint fk_ccc_card foreign key (credit_cards_id) references credit_card (id);
alter table customer_orders add constraint fk_co_customer foreign key (customer_id) references customer (id);
alter table customer_orders add constraint fk_co_order foreign key (orders_id) references orders (id);
alter table orders_order_lineitems add constraint fk_ooli_order foreign key (order_id) references orders (id);
alter table orders_order_lineitems add constraint fk_ooli_lineitem foreign key (products_id) references order_lineitems (id);
