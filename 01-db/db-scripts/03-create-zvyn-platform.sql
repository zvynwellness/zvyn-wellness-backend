USE `HoMYCare_DB`;

CREATE TABLE IF NOT EXISTS `app_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `full_name` VARCHAR(120) NOT NULL,
  `email` VARCHAR(190) NOT NULL,
  `password_hash` VARCHAR(100) NOT NULL,
  `role` ENUM('ADMIN', 'EMPLOYEE', 'CUSTOMER') NOT NULL,
  `active` BIT NOT NULL DEFAULT 1,
  `date_created` DATETIME(6) DEFAULT NULL,
  `last_updated` DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_user_email` (`email`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `employee_profile` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `job_title` VARCHAR(120) DEFAULT NULL,
  `specialty` VARCHAR(120) DEFAULT NULL,
  `biography` VARCHAR(1000) DEFAULT NULL,
  `accepting_clients` BIT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_employee_profile_user` (`user_id`),
  CONSTRAINT `fk_employee_profile_user` FOREIGN KEY (`user_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `wellness_service` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(120) NOT NULL,
  `slug` VARCHAR(140) NOT NULL,
  `description` VARCHAR(1600) NOT NULL,
  `category` VARCHAR(30) NOT NULL DEFAULT 'MOVE',
  `duration_minutes` INT NOT NULL,
  `image_url` VARCHAR(255) DEFAULT NULL,
  `display_order` INT NOT NULL DEFAULT 0,
  `active` BIT NOT NULL DEFAULT 1,
  `customer_capacity` INT NOT NULL DEFAULT 10,
  `employee_capacity` INT NOT NULL DEFAULT 5,
  `location` VARCHAR(255) NOT NULL DEFAULT 'To be confirmed',
  `google_maps_url` VARCHAR(1000) DEFAULT NULL,
  `price_amount` DECIMAL(10,2) DEFAULT NULL,
  `currency` CHAR(3) NOT NULL DEFAULT 'JPY',
  `scheduled_date_time` DATETIME DEFAULT NULL,
  `instructor_employee_id` BIGINT DEFAULT NULL,
  `date_created` DATETIME(6) DEFAULT NULL,
  `last_updated` DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_wellness_service_slug` (`slug`),
  KEY `idx_wellness_service_instructor` (`instructor_employee_id`),
  CONSTRAINT `fk_wellness_service_instructor` FOREIGN KEY (`instructor_employee_id`) REFERENCES `employee_profile` (`id`)
) ENGINE=InnoDB;

ALTER TABLE `wellness_service` ADD COLUMN IF NOT EXISTS `category` VARCHAR(30) NOT NULL DEFAULT 'MOVE';
UPDATE `wellness_service` SET `category` = 'MOVE' WHERE `category` IS NULL OR TRIM(`category`) = '';
UPDATE `wellness_service` SET `category` = 'REFLECT' WHERE `slug` = 'yoga-x-journaling';

UPDATE `wellness_service` SET `customer_capacity` = 10 WHERE `customer_capacity` < 0;
UPDATE `wellness_service` SET `employee_capacity` = 5 WHERE `employee_capacity` < 0;
UPDATE `wellness_service` SET `location` = 'To be confirmed' WHERE TRIM(`location`) = '';
UPDATE `wellness_service` SET `currency` = 'JPY' WHERE TRIM(`currency`) = '';

CREATE TABLE IF NOT EXISTS `pricing_plan` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(120) NOT NULL,
  `description` VARCHAR(500) NOT NULL,
  `session_count` INT NOT NULL,
  `price_amount` DECIMAL(10,2) DEFAULT NULL,
  `currency` CHAR(3) NOT NULL DEFAULT 'JPY',
  `display_price` VARCHAR(80) NOT NULL,
  `offline_payment_notice` VARCHAR(500) NOT NULL,
  `display_order` INT NOT NULL DEFAULT 0,
  `active` BIT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `purpose_content` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `brand_philosophy` VARCHAR(1200) NOT NULL,
  `personal_approach` VARCHAR(1200) NOT NULL,
  `core_values` VARCHAR(1200) NOT NULL,
  `active` BIT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `reservation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `customer_id` BIGINT NOT NULL,
  `service_id` BIGINT NOT NULL,
  `employee_id` BIGINT DEFAULT NULL,
  `preferred_date_time` DATETIME(6) NOT NULL,
  `notes` VARCHAR(1000) DEFAULT NULL,
  `status` ENUM('PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
  `date_created` DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_reservation_customer` (`customer_id`),
  KEY `idx_reservation_service` (`service_id`),
  KEY `idx_reservation_employee` (`employee_id`),
  CONSTRAINT `fk_reservation_customer` FOREIGN KEY (`customer_id`) REFERENCES `app_user` (`id`),
  CONSTRAINT `fk_reservation_service` FOREIGN KEY (`service_id`) REFERENCES `wellness_service` (`id`),
  CONSTRAINT `fk_reservation_employee` FOREIGN KEY (`employee_id`) REFERENCES `employee_profile` (`id`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `monthly_service` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `service_id` BIGINT NOT NULL,
  `employee_id` BIGINT DEFAULT NULL,
  `service_month` DATE NOT NULL,
  `active` BIT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_monthly_service_service` (`service_id`),
  CONSTRAINT `fk_monthly_service_service` FOREIGN KEY (`service_id`) REFERENCES `wellness_service` (`id`),
  CONSTRAINT `fk_monthly_service_employee` FOREIGN KEY (`employee_id`) REFERENCES `employee_profile` (`id`)
) ENGINE=InnoDB;

INSERT INTO `purpose_content` (`brand_philosophy`, `personal_approach`, `core_values`, `active`)
SELECT
  'Wellness is not a destination. It is your rhythm—personal, evolving, and uniquely yours.',
  'Listening to how your mind, body, energy, and everyday life work together helps you build a rhythm that supports greater balance, clarity, and effectiveness.',
  'Awareness, consistency, and balance—guided by a rhythm centered around you',
  1
WHERE NOT EXISTS (SELECT 1 FROM `purpose_content`);

UPDATE `purpose_content` SET
  `brand_philosophy` = 'Wellness is not a destination. It is your rhythm—personal, evolving, and uniquely yours.',
  `personal_approach` = 'Listening to how your mind, body, energy, and everyday life work together helps you build a rhythm that supports greater balance, clarity, and effectiveness.',
  `core_values` = 'Awareness, consistency, and balance—guided by a rhythm centered around you'
WHERE `active` = 1;

INSERT INTO `wellness_service`
  (`name`, `slug`, `description`, `category`, `duration_minutes`, `image_url`, `display_order`, `active`, `date_created`)
VALUES
  ('Private Movement', 'private-movement', 'One-to-one movement guidance adapted to your body, ability, goals, and pace.', 'MOVE', 60, 'assets/images/products/wellness/journey.svg', 1, 1, NOW()),
  ('Restorative Wellness', 'restorative-wellness', 'A gentle restorative session centered on rest, recovery, nourishment, and sustainable daily rhythms.', 'RESTORE_EXPLORE', 60, 'assets/images/products/wellness/restore.svg', 2, 1, NOW()),
  ('Guided Mindfulness', 'guided-mindfulness', 'Personal mindfulness practice that supports calm attention, emotional steadiness, and greater clarity.', 'REFLECT', 45, 'assets/images/products/wellness/mindful.svg', 3, 1, NOW())
ON DUPLICATE KEY UPDATE
  `name` = VALUES(`name`),
  `description` = VALUES(`description`),
  `category` = VALUES(`category`),
  `duration_minutes` = VALUES(`duration_minutes`),
  `image_url` = VALUES(`image_url`),
  `display_order` = VALUES(`display_order`),
  `active` = VALUES(`active`);

INSERT INTO `pricing_plan`
  (`name`, `description`, `session_count`, `price_amount`, `currency`, `display_price`, `offline_payment_notice`, `display_order`, `active`)
SELECT 'Individual Session', 'A single private wellness session.', 1, NULL, 'JPY', 'Contact us', 'Payment is arranged offline after your reservation is confirmed.', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM `pricing_plan` WHERE `name` = 'Individual Session');

INSERT INTO `pricing_plan`
  (`name`, `description`, `session_count`, `price_amount`, `currency`, `display_price`, `offline_payment_notice`, `display_order`, `active`)
SELECT 'Group - Session', 'A shared wellness session designed for connection and consistent support.', 1, NULL, 'JPY', 'Contact us', 'Payment is arranged offline after your reservation is confirmed.', 2, 1
WHERE NOT EXISTS (SELECT 1 FROM `pricing_plan` WHERE `name` = 'Group - Session');

INSERT INTO `pricing_plan`
  (`name`, `description`, `session_count`, `price_amount`, `currency`, `display_price`, `offline_payment_notice`, `display_order`, `active`)
SELECT 'Private Consultation', 'A private introductory conversation to understand your needs and recommend a suitable path.', 1, NULL, 'JPY', 'Contact us', 'No online payment is collected. Any applicable fee is confirmed directly with you.', 3, 1
WHERE NOT EXISTS (SELECT 1 FROM `pricing_plan` WHERE `name` = 'Private Consultation');

UPDATE `pricing_plan`
SET `name` = 'Group - Session',
    `description` = 'A shared wellness session designed for connection and consistent support.',
    `session_count` = 1
WHERE `name` = 'Four-Session Collection';

-- Development-only accounts. All use ChangeMe123! and must be changed before deployment.
INSERT INTO `app_user` (`full_name`, `email`, `password_hash`, `role`, `active`, `date_created`)
VALUES
  ('ZVyn Administrator', 'admin@zvynwellness.local', '$2y$10$freH6yDUCHAGOvhjGWIFyeAbEvWUXBipsfYsZb5giK5Si6oth9nkm', 'ADMIN', 1, NOW()),
  ('ZVyn Practitioner', 'employee@zvynwellness.local', '$2y$10$freH6yDUCHAGOvhjGWIFyeAbEvWUXBipsfYsZb5giK5Si6oth9nkm', 'EMPLOYEE', 1, NOW()),
  ('Sample Customer', 'customer@zvynwellness.local', '$2y$10$freH6yDUCHAGOvhjGWIFyeAbEvWUXBipsfYsZb5giK5Si6oth9nkm', 'CUSTOMER', 1, NOW())
ON DUPLICATE KEY UPDATE `full_name` = VALUES(`full_name`), `role` = VALUES(`role`), `active` = VALUES(`active`);

INSERT INTO `employee_profile` (`user_id`, `job_title`, `specialty`, `biography`, `accepting_clients`)
SELECT `id`, 'Wellness Practitioner', 'Movement, restoration, and mindfulness',
       'Provides quiet, individualized support designed around each client.', 1
FROM `app_user`
WHERE `email` = 'employee@zvynwellness.local'
  AND NOT EXISTS (SELECT 1 FROM `employee_profile` WHERE `user_id` = `app_user`.`id`);
