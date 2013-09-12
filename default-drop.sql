SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists basket;

drop table if exists basket_product;

drop table if exists billingAddress;

drop table if exists creditCard;

drop table if exists customer;

drop table if exists deliveryAddress;

drop table if exists ordering;

drop table if exists order_product;

drop table if exists product;

drop table if exists subCategory;

drop table if exists category;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists basket_seq;

drop sequence if exists basket_product_seq;

drop sequence if exists billingAddress_seq;

drop sequence if exists creditCard_seq;

drop sequence if exists customer_seq;

drop sequence if exists deliveryAddress_seq;

drop sequence if exists ordering_seq;

drop sequence if exists order_product_seq;

drop sequence if exists product_seq;

drop sequence if exists subCategory_seq;

drop sequence if exists category_seq;

