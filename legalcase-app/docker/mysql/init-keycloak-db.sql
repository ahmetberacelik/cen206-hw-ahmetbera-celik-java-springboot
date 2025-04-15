-- Veritabanlarını oluştur
CREATE DATABASE IF NOT EXISTS `legalcasedb`;
CREATE DATABASE IF NOT EXISTS `legalcase_users`;
CREATE DATABASE IF NOT EXISTS `legalcase_cases`;
CREATE DATABASE IF NOT EXISTS `keycloak`;

-- Create database for hearings service
CREATE DATABASE IF NOT EXISTS legalcase_hearings CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Kullanıcıyı oluştur (yeniden oluşturma ihtiyacı durumunda)
CREATE USER IF NOT EXISTS 'legalcaseuser'@'%' IDENTIFIED BY 'legalcasepass';

-- Kullanıcıya tüm veritabanlarına erişim izni ver
GRANT ALL PRIVILEGES ON `legalcasedb`.* TO 'legalcaseuser'@'%';
GRANT ALL PRIVILEGES ON `legalcase_users`.* TO 'legalcaseuser'@'%';
GRANT ALL PRIVILEGES ON `legalcase_cases`.* TO 'legalcaseuser'@'%';
GRANT ALL PRIVILEGES ON `keycloak`.* TO 'legalcaseuser'@'%';

-- Grant privileges for hearings database
GRANT ALL PRIVILEGES ON legalcase_hearings.* TO 'legalcaseuser'@'%';

-- İzinleri hemen uygula
FLUSH PRIVILEGES; 