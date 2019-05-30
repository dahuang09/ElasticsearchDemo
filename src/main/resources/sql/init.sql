--shell: mysql -u root -p
CREATE DATABASE IF NOT EXISTS my_db default charset utf8 COLLATE utf8_general_ci;
--use my_db
CREATE TABLE IF NOT EXISTS `USER`(
   `id` VARCHAR(100),
   `name` VARCHAR(100) NOT NULL,
   `updated_date` DATE,
   PRIMARY KEY ( `id` )
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `J_ORDER`(
   `id` VARCHAR(100),
   `order_num` VARCHAR(100) NOT NULL,
   `amt` DECIMAL(5,5),
   `description` VARCHAR(1000) NOT NULL,
   `updated_date` DATE,
   PRIMARY KEY ( `id` )
)ENGINE=InnoDB DEFAULT CHARSET=utf8;