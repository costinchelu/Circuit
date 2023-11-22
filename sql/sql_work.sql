-- -----------------------------------------------------------------------------
-- Hibernate creates tables using property "spring.jpa.hibernate.ddl-auto=update"

-- In order to test the app on local environment, create a MySQL schema:
-- CREATE SCHEMA
CREATE SCHEMA ecomdb;

-- CREATE USER
CREATE USER ecomuser IDENTIFIED BY '12345';

-- GRANT
GRANT ALL PRIVILEGES ON ecomdb.* TO 'ecomuser'@'%';
FLUSH PRIVILEGES;

-- USERS info
SELECT CURRENT_USER();
SHOW GRANTS FOR 'ecomuser'@'%';

-- use a MySQL Dump file to create the sample DB


-- update the order dates so that it can be more recent (for reporting):

-- update orders for sales reports (change date)
UPDATE orders SET order_time = ADDDATE(order_time, 18) WHERE id > 0;
UPDATE orders SET deliver_date = DATE_FORMAT(ADDDATE(order_time, deliver_days + 1), '%Y-%m-%d') WHERE id > 0;


-- ------------------------------------------------------------------------------------------------------
-- this may not be needed:
-- update orders for incorrect order entries
/* UPDATE order_details o SET o.product_cost = (SELECT p.cost FROM products p WHERE p.id = o.product_id) WHERE o.id > 0;
UPDATE order_details o SET o.unit_price = (SELECT p.price FROM products p WHERE p.id = o.product_id) WHERE o.id > 0;
UPDATE order_details SET subtotal = unit_price * quantity WHERE id > 0;
UPDATE order_details o SET o.subtotal = o.unit_price * o.quantity WHERE o.id > 0;

UPDATE orders o SET o.product_cost = (SELECT SUM(d.product_cost) FROM order_details d WHERE d.order_id = o.id) WHERE o.id > 0;
UPDATE orders o SET o.subtotal = (SELECT SUM(d.subtotal) FROM order_details d WHERE d.order_id = o.id) WHERE o.id > 0;
UPDATE orders o SET o.shipping_cost = (SELECT SUM(d.shipping_cost) FROM order_details d WHERE d.order_id = o.id) WHERE o.id > 0;
UPDATE orders o SET o.total = o.subtotal + o.tax + o.shipping_cost WHERE o.id > 0; */

-- create an index for searching function
-- ALTER TABLE products ADD FULLTEXT INDEX `products_FTS` (`name`, `short_description`, `full_description`);