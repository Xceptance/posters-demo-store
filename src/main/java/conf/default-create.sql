create table user (
  id                        varchar(40) not null,
  email                     varchar(255),
  password                  varchar(255),
  name                      varchar(255),
  first_name                varchar(255),
  constraint pk_user primary key (id))
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
  customer_id               varchar(40),
  constraint pk_billingAddress primary key (id))
;

create table cart (
  id                        varchar(40) not null,
  customer_id               varchar(40),
  sub_Total_Price	    	double,
  tax                       double,
  total_Tax_Price	    	double,
  shipping_costs            double,
  total_Price               double,
  constraint pk_cart primary key (id))
;

create table cartProduct (
  id                        integer not null,
  product_id                integer,
  cart_id                   varchar(40),
  product_count             integer,
  finish                    varchar(255),
  posterSize_id             integer,
  price                     double,
  last_update               timestamp not null,
  constraint pk_cartProduct primary key (id))
;

create table creditCard (
  id                        integer not null,
  card_number               varchar(255),
  name                      varchar(255),
  month                     integer,
  year                      integer,
  customer_id               varchar(40),
  constraint pk_creditCard primary key (id))
;

create table customer (
  id                        varchar(40) not null,
  email                     varchar(255),
  password                  varchar(255),
  name                      varchar(255),
  first_name                varchar(255),
  cart_id                   varchar(40),
  constraint pk_customer primary key (id))
;

create table ordering (
  id                        varchar(40) not null,
  order_date                varchar(255),
  shipping_address_id       integer,
  billing_address_id        integer,
  sub_Total_Costs		    double,
  total_Tax_Costs		    double,
  tax                       double,
  shipping_costs            double,
  total_costs               double,
  credit_card_id            integer,
  customer_id               varchar(40),
  last_update               timestamp not null,
  constraint pk_ordering primary key (id))
;

create table orderProduct (
  id                        integer not null,
  product_id                integer,
  ordering_id               varchar(40),
  product_count             integer,
  finish                    varchar(255),
  postersize_id             integer,
  price                     double,
  constraint pk_orderProduct primary key (id))
;

create table posterSize (
  id                        integer not null,
  width                     integer,
  height                    integer,
  constraint pk_posterSize primary key (id))
;

create table product (
  id                        integer not null,
  name                      varchar(255),
  description_detail        varchar(4096),
  description_overview      varchar(1024),
  image_url                 varchar(255),
  show_in_carousel          boolean,
  show_in_top_categorie     boolean,
  subCategory_id            integer,
  top_category_id           integer,
  minimum_price             double,
  constraint pk_product primary key (id))
;

create table productPosterSize (
  id                        integer not null,
  product_id                integer,
  posterSize_id             integer,
  price                     double,
  constraint pk_productPosterSize primary key (id))
;

create table shippingAddress (
  id                        integer not null,
  name                      varchar(255),
  company                   varchar(255),
  address_line              varchar(255),
  city                      varchar(255),
  state                     varchar(255),
  country                   varchar(255),
  zip                       varchar(255),
  customer_id               varchar(40),
  constraint pk_shippingAddress primary key (id))
;

create table subCategory (
  id                        integer not null,
  name                      varchar(255),
  topCategory_id            integer,
  constraint pk_subCategory primary key (id))
;

create table topCategory (
  id                        integer not null,
  name                      varchar(255),
  constraint pk_topCategory primary key (id))
;

create sequence billingAddress_seq;

create sequence cartProduct_seq;

create sequence creditCard_seq;

create sequence orderProduct_seq;

create sequence posterSize_seq;

create sequence product_seq;

create sequence productPosterSize_seq;

create sequence shippingAddress_seq;

create sequence subCategory_seq;

create sequence topCategory_seq;

alter table billingAddress add constraint fk_billingAddress_customer_1 foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_billingAddress_customer_1 on billingAddress (customer_id);
alter table cart add constraint fk_cart_customer_2 foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_cart_customer_2 on cart (customer_id);
alter table cartProduct add constraint fk_cartProduct_product_3 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_cartProduct_product_3 on cartProduct (product_id);
alter table cartProduct add constraint fk_cartProduct_cart_4 foreign key (cart_id) references cart (id) on delete restrict on update restrict;
create index ix_cartProduct_cart_4 on cartProduct (cart_id);
alter table cartProduct add constraint fk_cartProduct_size_5 foreign key (posterSize_id) references posterSize (id) on delete restrict on update restrict;
create index ix_cartProduct_size_5 on cartProduct (posterSize_id);
alter table creditCard add constraint fk_creditCard_customer_6 foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_creditCard_customer_6 on creditCard (customer_id);
alter table customer add constraint fk_customer_cart_7 foreign key (cart_id) references cart (id) on delete restrict on update restrict;
create index ix_customer_cart_7 on customer (cart_id);
alter table ordering add constraint fk_ordering_shippingAddress_8 foreign key (shipping_address_id) references shippingAddress (id) on delete restrict on update restrict;
create index ix_ordering_shippingAddress_8 on ordering (shipping_address_id);
alter table ordering add constraint fk_ordering_billingAddress_9 foreign key (billing_address_id) references billingAddress (id) on delete restrict on update restrict;
create index ix_ordering_billingAddress_9 on ordering (billing_address_id);
alter table ordering add constraint fk_ordering_creditCard_10 foreign key (credit_card_id) references creditCard (id) on delete restrict on update restrict;
create index ix_ordering_creditCard_10 on ordering (credit_card_id);
alter table ordering add constraint fk_ordering_customer_11 foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_ordering_customer_11 on ordering (customer_id);
alter table orderProduct add constraint fk_orderProduct_product_12 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_orderProduct_product_12 on orderProduct (product_id);
alter table orderProduct add constraint fk_orderProduct_order_13 foreign key (ordering_id) references ordering (id) on delete restrict on update restrict;
create index ix_orderProduct_order_13 on orderProduct (ordering_id);
alter table orderProduct add constraint fk_orderProduct_size_14 foreign key (postersize_id) references posterSize (id) on delete restrict on update restrict;
create index ix_orderProduct_size_14 on orderProduct (postersize_id);
alter table product add constraint fk_product_subCategory_15 foreign key (subCategory_id) references subCategory (id) on delete restrict on update restrict;
create index ix_product_subCategory_15 on product (subCategory_id);
alter table product add constraint fk_product_topCategory_16 foreign key (top_category_id) references topCategory (id) on delete restrict on update restrict;
create index ix_product_topCategory_16 on product (top_category_id);
alter table productPosterSize add constraint fk_productPosterSize_product_17 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_productPosterSize_product_17 on productPosterSize (product_id);
alter table productPosterSize add constraint fk_productPosterSize_size_18 foreign key (posterSize_id) references posterSize (id) on delete restrict on update restrict;
create index ix_productPosterSize_size_18 on productPosterSize (posterSize_id);
alter table shippingAddress add constraint fk_shippingAddress_customer_19 foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_shippingAddress_customer_19 on shippingAddress (customer_id);
alter table subCategory add constraint fk_subCategory_topCategory_20 foreign key (topCategory_id) references topCategory (id) on delete restrict on update restrict;
create index ix_subCategory_topCategory_20 on subCategory (topCategory_id);


