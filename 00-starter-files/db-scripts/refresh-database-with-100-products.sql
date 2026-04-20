-- -----------------------------------------------------
-- Schema full-HoMYCare_DB
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `HoMYCare_DB`;

CREATE SCHEMA `HoMYCare_DB`;
USE `HoMYCare_DB` ;

-- -----------------------------------------------------
-- Table `HoMYCare_DB`.`product_category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `HoMYCare_DB`.`product_category` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `category_name` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE=InnoDB
AUTO_INCREMENT = 1;

-- -----------------------------------------------------
-- Table `HoMYCare_DB`.`product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `HoMYCare_DB`.`product` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) DEFAULT NULL,
  `description` VARCHAR(255) DEFAULT NULL,
  `image_url` VARCHAR(255) DEFAULT NULL,
  `active` BIT DEFAULT 1,
   `date_created` DATETIME(6) DEFAULT NULL,
  `last_updated` DATETIME(6) DEFAULT NULL,
  `category_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_category` (`category_id`),
  CONSTRAINT `fk_category` FOREIGN KEY (`category_id`) REFERENCES `product_category` (`id`)
)
ENGINE=InnoDB
AUTO_INCREMENT = 1;

-- -----------------------------------------------------
-- product_category
-- -----------------------------------------------------
INSERT INTO product_category(category_name) VALUES ('House Cleaning');
INSERT INTO product_category(category_name) VALUES ('Gardening');
INSERT INTO product_category(category_name) VALUES ('Babysitting');

-- -----------------------------------------------------
-- House Cleaning
-- -----------------------------------------------------
INSERT INTO product (name, description, image_url, active, category_id, date_created) VALUES ('House Cleaning AZ', 'Sweeping, vacuuming and washing','assets/images/products/housecleaning/housecleaning-homycare-1000.png',1,1, NOW());
INSERT INTO product (name, description, image_url, active, category_id, date_created) VALUES ('House Cleaning NY', 'Toilet cleaning','assets/images/products/housecleaning/housecleaning-homycare-1001.png',1,1, NOW());

-- -----------------------------------------------------
-- Gardening
-- -----------------------------------------------------
INSERT INTO product (name, description, image_url, active, category_id, date_created) VALUES ('Garden Cleaning AZ', 'Garden size up to 10m', 'assets/images/products/gardenning/gardening-homycare-1000.png',1,2, NOW());
INSERT INTO product (name, description, image_url, active, category_id, date_created) VALUES ('Garden Cleaning NY', 'Garden size up to 10m', 'assets/images/products/gardenning/gardening-homycare-1001.png',1,2, NOW());

-- -----------------------------------------------------
-- Babysitting
-- -----------------------------------------------------
INSERT INTO product (name, description, image_url, active, category_id, date_created) VALUES ('Babysitting Indo', 'Cleaning, feeding ','assets/images/products/babysitting/babysitting-homycare-1000.png',1,3, NOW());