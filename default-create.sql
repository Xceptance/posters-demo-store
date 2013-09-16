create table basket (
  id                        integer not null,
  customer_id               integer,
  total_price               double,
  constraint pk_basket primary key (id))
;

create table basket_product (
  id                        integer not null,
  product_id                integer,
  basket_id                 integer,
  count_product             integer,
  constraint pk_basket_product primary key (id))
;

create table billingAddress (
  id                        integer not null,
  name                      varchar(255),
  company                   varchar(255),
  address_line              varchar(255),
  city                      varchar(255),
  state                     varchar(255),
  country                   varchar(255),
  zip                       varchar(255),
  customer_id               integer,
  constraint pk_billingAddress primary key (id))
;

create table creditCard (
  id                        integer not null,
  card_number               varchar(255),
  name                      varchar(255),
  month                     integer,
  year                      integer,
  customer_id               integer,
  constraint pk_creditCard primary key (id))
;

create table customer (
  id                        integer not null,
  email                     varchar(255),
  password                  varchar(255),
  name                      varchar(255),
  first_name                varchar(255),
  basket_id                 integer,
  constraint pk_customer primary key (id))
;

create table deliveryAddress (
  id                        integer not null,
  name                      varchar(255),
  company                   varchar(255),
  address_line              varchar(255),
  city                      varchar(255),
  state                     varchar(255),
  country                   varchar(255),
  zip                       varchar(255),
  customer_id               integer,
  constraint pk_deliveryAddress primary key (id))
;

create table ordering (
  id                        integer not null,
  order_date                varchar(255),
  delivery_address_id       integer,
  billing_address_id        integer,
  shipping_costs            double,
  tax                       double,
  total_costs               double,
  credit_card_id            integer,
  customer_id               integer,
  constraint pk_ordering primary key (id))
;

create table order_product (
  id                        integer not null,
  product_id                integer,
  ordering_id               integer,
  count_product             integer,
  constraint pk_order_product primary key (id))
;

create table product (
  id                        integer not null,
  name                      varchar(255),
  url                       varchar(255),
  price                     double,
  description_detail        varchar(4096),
  description_overview      varchar(1024),
  image_url                 varchar(255),
  show_in_carousel          boolean,
  show_in_top_categorie     boolean,
  subCategory_id            integer,
  top_category_id           integer,
  constraint pk_product primary key (id))
;

create table subCategory (
  id                        integer not null,
  name                      varchar(255),
  url                       varchar(255),
  topCategory_id            integer,
  constraint pk_subCategory primary key (id))
;

create table category (
  id                        integer not null,
  name                      varchar(255),
  url                       varchar(255),
  constraint pk_category primary key (id))
;

create sequence basket_seq;

create sequence basket_product_seq;

create sequence billingAddress_seq;

create sequence creditCard_seq;

create sequence customer_seq;

create sequence deliveryAddress_seq;

create sequence ordering_seq;

create sequence order_product_seq;

create sequence product_seq;

create sequence subCategory_seq;

create sequence category_seq;

alter table basket add constraint fk_basket_customer_1 foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_basket_customer_1 on basket (customer_id);
alter table basket_product add constraint fk_basket_product_product_2 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_basket_product_product_2 on basket_product (product_id);
alter table basket_product add constraint fk_basket_product_basket_3 foreign key (basket_id) references basket (id) on delete restrict on update restrict;
create index ix_basket_product_basket_3 on basket_product (basket_id);
alter table billingAddress add constraint fk_billingAddress_customer_4 foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_billingAddress_customer_4 on billingAddress (customer_id);
alter table creditCard add constraint fk_creditCard_customer_5 foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_creditCard_customer_5 on creditCard (customer_id);
alter table customer add constraint fk_customer_basket_6 foreign key (basket_id) references basket (id) on delete restrict on update restrict;
create index ix_customer_basket_6 on customer (basket_id);
alter table deliveryAddress add constraint fk_deliveryAddress_customer_7 foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_deliveryAddress_customer_7 on deliveryAddress (customer_id);
alter table ordering add constraint fk_ordering_deliveryAddress_8 foreign key (delivery_address_id) references deliveryAddress (id) on delete restrict on update restrict;
create index ix_ordering_deliveryAddress_8 on ordering (delivery_address_id);
alter table ordering add constraint fk_ordering_billingAddress_9 foreign key (billing_address_id) references billingAddress (id) on delete restrict on update restrict;
create index ix_ordering_billingAddress_9 on ordering (billing_address_id);
alter table ordering add constraint fk_ordering_creditCard_10 foreign key (credit_card_id) references creditCard (id) on delete restrict on update restrict;
create index ix_ordering_creditCard_10 on ordering (credit_card_id);
alter table ordering add constraint fk_ordering_customer_11 foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_ordering_customer_11 on ordering (customer_id);
alter table order_product add constraint fk_order_product_product_12 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_order_product_product_12 on order_product (product_id);
alter table order_product add constraint fk_order_product_order_13 foreign key (ordering_id) references ordering (id) on delete restrict on update restrict;
create index ix_order_product_order_13 on order_product (ordering_id);
alter table product add constraint fk_product_subCategory_14 foreign key (subCategory_id) references subCategory (id) on delete restrict on update restrict;
create index ix_product_subCategory_14 on product (subCategory_id);
alter table product add constraint fk_product_topCategory_15 foreign key (top_category_id) references category (id) on delete restrict on update restrict;
create index ix_product_topCategory_15 on product (top_category_id);
alter table subCategory add constraint fk_subCategory_topCategory_16 foreign key (topCategory_id) references category (id) on delete restrict on update restrict;
create index ix_subCategory_topCategory_16 on subCategory (topCategory_id);


