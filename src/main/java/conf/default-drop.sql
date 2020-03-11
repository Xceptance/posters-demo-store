SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists billingAddress;

drop table if exists cart;

drop table if exists cartProduct;

drop table if exists creditCard;

drop table if exists customer;

drop table if exists ordering;

drop table if exists orderProduct;

drop table if exists posterSize;

drop table if exists product;

drop table if exists productPosterSize;

drop table if exists shippingAddress;

drop table if exists subCategory;

drop table if exists topCategory;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists billingAddress_seq;

drop sequence if exists cartProduct_seq;

drop sequence if exists creditCard_seq;

drop sequence if exists orderProduct_seq;

drop sequence if exists posterSize_seq;

drop sequence if exists product_seq;

drop sequence if exists productPosterSize_seq;

drop sequence if exists shippingAddress_seq;

drop sequence if exists subCategory_seq;

drop sequence if exists topCategory_seq;

