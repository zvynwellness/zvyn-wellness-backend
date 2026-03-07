-- -----------------------------------------------------
-- Schema full-HoMYCare_DB
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `HoMYCare_DB`;

CREATE SCHEMA `HoMYCare_DB`;
USE `HoMYCare_DB` ;

-- -----------------------------------------------------
-- Table `HoMYCare_DB`.`hmyservice_category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `HoMYCare_DB`.`hmyservice_category` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `hmyservice_name` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE=InnoDB
AUTO_INCREMENT = 1;

-- -----------------------------------------------------
-- Table `HoMYCare_DB`.`hmyservice`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `HoMYCare_DB`.`hmyservice` (
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
  CONSTRAINT `fk_category` FOREIGN KEY (`category_id`) REFERENCES `hmyservice_category` (`id`)
) 
ENGINE=InnoDB
AUTO_INCREMENT = 1;


-- -----------------------------------------------------
-- Add sample data
-- -----------------------------------------------------

INSERT INTO hmyservice_category(hmyservice_name) VALUES ('HMYSERVICES');

INSERT INTO hmyservice (name, description, image_url, active, category_id, date_created)
VALUES ('House Cleaning', 'House cleaning including sweeping, vacuuuming and washing',
'assets/images/products/placeholder.png'
,1,1, NOW());

INSERT INTO hmyservice (name, description, image_url, active, category_id, date_created)
VALUES ('Garden Cleaning', 'Garden size up to 10m',
'assets/images/products/placeholder.png'
,1,1, NOW());


INSERT INTO hmyservice (name, description, image_url, active, category_id, date_created)
VALUES ('Babysitting', 'Cleaning, feeding ',
'assets/images/products/placeholder.png'
,1,1, NOW());

