DROP DATABASE IF EXISTS productservice;
DROP USER IF EXISTS `warehouse_user`@`%`;
CREATE DATABASE IF NOT EXISTS productservice CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS `warehouse_user`@`%` IDENTIFIED WITH mysql_native_password BY 'password';
GRANT ALL PRIVILEGES ON `productservice`.* TO `warehouse_user`@`%`;