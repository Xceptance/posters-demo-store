create table backofficeuser (
  id                            uuid not null,
  email                         varchar(255),
  password                      varchar(255),
  name                          varchar(255),
  first_name                    varchar(255),
  constraint pk_backofficeuser primary key (id)
);

create table billingaddress (
  id                            integer auto_increment not null,
  name                          varchar(255),
  first_name                    varchar(255),
  company                       varchar(255),
  address_line                  varchar(255),
  city                          varchar(255),
  state                         varchar(255),
  country                       varchar(255),
  zip                           varchar(255),
  customer_id                   uuid,
  constraint pk_billingaddress primary key (id)
);

create table cart (
  id                            uuid not null,
  customer_id                   uuid,
  shipping_address_id           integer,
  billing_address_id            integer,
  credit_card_id                integer,
  shipping_costs                double not null,
  sub_total_price               double not null,
  tax                           double not null,
  total_tax_price               double not null,
  total_price                   double not null,
  constraint uq_cart_customer_id unique (customer_id),
  constraint pk_cart primary key (id)
);

create table cartproduct (
  id                            integer auto_increment not null,
  product_id                    integer,
  cart_id                       uuid,
  product_count                 integer not null,
  finish                        varchar(255),
  postersize_id                 integer,
  price                         double not null,
  last_update                   timestamp not null,
  constraint pk_cartproduct primary key (id)
);

create table creditcard (
  id                            integer auto_increment not null,
  card_number                   varchar(255),
  name                          varchar(255),
  months                        integer not null,
  years                         integer not null,
  customer_id                   uuid,
  constraint pk_creditcard primary key (id)
);

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

create table ordering (
  id                            uuid not null,
  order_date                    varchar(255),
  shipping_address_id           integer,
  billing_address_id            integer,
  shipping_costs                double not null,
  sub_total_costs               double not null,
  total_tax_costs               double not null,
  tax                           double not null,
  total_costs                   double not null,
  credit_card_id                integer,
  customer_id                   uuid,
  order_status                  varchar(255),
  last_update                   timestamp not null,
  constraint pk_ordering primary key (id)
);

create table orderproduct (
  id                            integer auto_increment not null,
  product_id                    integer,
  ordering_id                   uuid,
  product_count                 integer not null,
  finish                        varchar(255),
  postersize_id                 integer,
  price                         double not null,
  constraint pk_orderproduct primary key (id)
);

create table postersize (
  id                            integer auto_increment not null,
  width                         integer not null,
  height                        integer not null,
  constraint pk_postersize primary key (id)
);

create table product (
  id                            integer auto_increment not null,
  name                          varchar(255),
  description_detail            varchar(4096),
  description_overview          varchar(1024),
  image_url                     varchar(255),
  small_image_url               varchar(255),
  medium_image_url              varchar(255),
  large_image_url               varchar(255),
  original_image_url            varchar(255),
  show_in_carousel              boolean default false not null,
  show_in_top_categorie         boolean default false not null,
  subcategory_id                integer,
  top_category_id               integer,
  minimum_price                 double not null,
  constraint pk_product primary key (id)
);

create table productpostersize (
  id                            integer auto_increment not null,
  product_id                    integer,
  postersize_id                 integer,
  price                         double not null,
  constraint pk_productpostersize primary key (id)
);

create table shippingaddress (
  id                            integer auto_increment not null,
  name                          varchar(255),
  first_name                    varchar(255),
  company                       varchar(255),
  address_line                  varchar(255),
  city                          varchar(255),
  state                         varchar(255),
  country                       varchar(255),
  zip                           varchar(255),
  customer_id                   uuid,
  constraint pk_shippingaddress primary key (id)
);

create table subcategory (
  id                            integer auto_increment not null,
  name                          varchar(255),
  topcategory_id                integer,
  constraint pk_subcategory primary key (id)
);

create table topcategory (
  id                            integer auto_increment not null,
  name                          varchar(255),
  constraint pk_topcategory primary key (id)
);

create index ix_billingaddress_customer_id on billingaddress (customer_id);
alter table billingaddress add constraint fk_billingaddress_customer_id foreign key (customer_id) references customer (id) on delete restrict on update restrict;

create index ix_cartproduct_product_id on cartproduct (product_id);
alter table cartproduct add constraint fk_cartproduct_product_id foreign key (product_id) references product (id) on delete restrict on update restrict;

create index ix_cartproduct_cart_id on cartproduct (cart_id);
alter table cartproduct add constraint fk_cartproduct_cart_id foreign key (cart_id) references cart (id) on delete restrict on update restrict;

create index ix_cartproduct_postersize_id on cartproduct (postersize_id);
alter table cartproduct add constraint fk_cartproduct_postersize_id foreign key (postersize_id) references postersize (id) on delete restrict on update restrict;

create index ix_creditcard_customer_id on creditcard (customer_id);
alter table creditcard add constraint fk_creditcard_customer_id foreign key (customer_id) references customer (id) on delete restrict on update restrict;

alter table customer add constraint fk_customer_cart_id foreign key (cart_id) references cart (id) on delete restrict on update restrict;

create index ix_ordering_shipping_address_id on ordering (shipping_address_id);
alter table ordering add constraint fk_ordering_shipping_address_id foreign key (shipping_address_id) references shippingaddress (id) on delete restrict on update restrict;

create index ix_ordering_billing_address_id on ordering (billing_address_id);
alter table ordering add constraint fk_ordering_billing_address_id foreign key (billing_address_id) references billingaddress (id) on delete restrict on update restrict;

create index ix_ordering_credit_card_id on ordering (credit_card_id);
alter table ordering add constraint fk_ordering_credit_card_id foreign key (credit_card_id) references creditcard (id) on delete restrict on update restrict;

create index ix_orderproduct_product_id on orderproduct (product_id);
alter table orderproduct add constraint fk_orderproduct_product_id foreign key (product_id) references product (id) on delete restrict on update restrict;

create index ix_orderproduct_ordering_id on orderproduct (ordering_id);
alter table orderproduct add constraint fk_orderproduct_ordering_id foreign key (ordering_id) references ordering (id) on delete restrict on update restrict;

create index ix_orderproduct_postersize_id on orderproduct (postersize_id);
alter table orderproduct add constraint fk_orderproduct_postersize_id foreign key (postersize_id) references postersize (id) on delete restrict on update restrict;

create index ix_product_subcategory_id on product (subcategory_id);
alter table product add constraint fk_product_subcategory_id foreign key (subcategory_id) references subcategory (id) on delete restrict on update restrict;

create index ix_product_top_category_id on product (top_category_id);
alter table product add constraint fk_product_top_category_id foreign key (top_category_id) references topcategory (id) on delete restrict on update restrict;

create index ix_productpostersize_product_id on productpostersize (product_id);
alter table productpostersize add constraint fk_productpostersize_product_id foreign key (product_id) references product (id) on delete restrict on update restrict;

create index ix_productpostersize_postersize_id on productpostersize (postersize_id);
alter table productpostersize add constraint fk_productpostersize_postersize_id foreign key (postersize_id) references postersize (id) on delete restrict on update restrict;

create index ix_shippingaddress_customer_id on shippingaddress (customer_id);
alter table shippingaddress add constraint fk_shippingaddress_customer_id foreign key (customer_id) references customer (id) on delete restrict on update restrict;

create index ix_subcategory_topcategory_id on subcategory (topcategory_id);
alter table subcategory add constraint fk_subcategory_topcategory_id foreign key (topcategory_id) references topcategory (id) on delete restrict on update restrict;

