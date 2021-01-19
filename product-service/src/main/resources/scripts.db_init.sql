DROP DATABASE IF EXISTS productservice;
DROP USER IF EXISTS `product_service`@`%`;
CREATE DATABASE IF NOT EXISTS productservice CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS `product_service`@`%` IDENTIFIED WITH mysql_native_password BY 'password';
GRANT ALL PRIVILEGES ON `productservice`.* TO `product_service`@`%`;