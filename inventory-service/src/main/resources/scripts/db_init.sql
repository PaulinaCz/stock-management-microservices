DROP DATABASE IF EXISTS inventoryservice;
DROP USER IF EXISTS `warehouse_user`@`%`;
CREATE DATABASE IF NOT EXISTS inventoryservice CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS `warehouse_user`@`%` IDENTIFIED WITH mysql_native_password BY 'password';
GRANT ALL PRIVILEGES ON `inventoryservice`.* TO `warehouse_user`@`%`;