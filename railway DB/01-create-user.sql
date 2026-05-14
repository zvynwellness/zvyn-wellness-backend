-- 1️⃣ Create the database
CREATE DATABASE IF NOT EXISTS HoMYCare_DB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- 2️⃣ Create the user (for MySQL 8+, uses secure default plugin)
CREATE USER IF NOT EXISTS 'homycareapp'@'localhost' IDENTIFIED BY 'homycareapp';
CREATE USER IF NOT EXISTS 'homycareapp'@'127.0.0.1' IDENTIFIED BY 'homycareapp';

-- 3️⃣ Grant privileges only on this database
GRANT ALL PRIVILEGES ON HoMYCare_DB.* TO 'homycareapp'@'localhost';
GRANT ALL PRIVILEGES ON HoMYCare_DB.* TO 'homycareapp'@'127.0.0.1';

-- 4️⃣ Apply changes
FLUSH PRIVILEGES;

-- 5️⃣ Verify everything
SHOW DATABASES;
SELECT user, host, plugin FROM mysql.user WHERE user='homycareapp';
SHOW GRANTS FOR 'homycareapp'@'localhost';
