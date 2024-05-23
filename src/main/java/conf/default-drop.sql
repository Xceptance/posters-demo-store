alter table billingaddress drop constraint if exists fk_billingaddress_customer_id;
drop index if exists ix_billingaddress_customer_id;

alter table cart drop constraint if exists fk_cart_customer_id;

alter table cart drop constraint if exists fk_cart_shipping_address_id;
drop index if exists ix_cart_shipping_address_id;

alter table cart drop constraint if exists fk_cart_billing_address_id;
drop index if exists ix_cart_billing_address_id;

alter table cart drop constraint if exists fk_cart_credit_card_id;
drop index if exists ix_cart_credit_card_id;

alter table cartproduct drop constraint if exists fk_cartproduct_product_id;
drop index if exists ix_cartproduct_product_id;

alter table cartproduct drop constraint if exists fk_cartproduct_cart_id;
drop index if exists ix_cartproduct_cart_id;

alter table cartproduct drop constraint if exists fk_cartproduct_postersize_id;
drop index if exists ix_cartproduct_postersize_id;

alter table creditcard drop constraint if exists fk_creditcard_customer_id;
drop index if exists ix_creditcard_customer_id;

alter table customer drop constraint if exists fk_customer_cart_id;

alter table ordering drop constraint if exists fk_ordering_shipping_address_id;
drop index if exists ix_ordering_shipping_address_id;

alter table ordering drop constraint if exists fk_ordering_billing_address_id;
drop index if exists ix_ordering_billing_address_id;

alter table ordering drop constraint if exists fk_ordering_credit_card_id;
drop index if exists ix_ordering_credit_card_id;

alter table orderproduct drop constraint if exists fk_orderproduct_product_id;
drop index if exists ix_orderproduct_product_id;

alter table orderproduct drop constraint if exists fk_orderproduct_ordering_id;
drop index if exists ix_orderproduct_ordering_id;

alter table orderproduct drop constraint if exists fk_orderproduct_postersize_id;
drop index if exists ix_orderproduct_postersize_id;

alter table product drop constraint if exists fk_product_subcategory_id;
drop index if exists ix_product_subcategory_id;

alter table product drop constraint if exists fk_product_top_category_id;
drop index if exists ix_product_top_category_id;

alter table productpostersize drop constraint if exists fk_productpostersize_product_id;
drop index if exists ix_productpostersize_product_id;

alter table productpostersize drop constraint if exists fk_productpostersize_postersize_id;
drop index if exists ix_productpostersize_postersize_id;

alter table shippingaddress drop constraint if exists fk_shippingaddress_customer_id;
drop index if exists ix_shippingaddress_customer_id;

alter table subcategory drop constraint if exists fk_subcategory_topcategory_id;
drop index if exists ix_subcategory_topcategory_id;

drop table if exists backofficeuser;

drop table if exists billingaddress;

drop table if exists cart;

drop table if exists cartproduct;

drop table if exists creditcard;

drop table if exists customer;

drop table if exists ordering;

drop table if exists orderproduct;

drop table if exists postersize;

drop table if exists product;

drop table if exists productpostersize;

drop table if exists shippingaddress;

drop table if exists subcategory;

drop table if exists topcategory;
