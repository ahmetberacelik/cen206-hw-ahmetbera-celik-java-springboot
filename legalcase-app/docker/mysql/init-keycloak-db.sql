-- Veritabanlarını oluştur
CREATE DATABASE IF NOT EXISTS `legalcasedb`;
CREATE DATABASE IF NOT EXISTS `legalcase_users`;
CREATE DATABASE IF NOT EXISTS `keycloak`;

-- Kullanıcıyı oluştur (yeniden oluşturma ihtiyacı durumunda)
CREATE USER IF NOT EXISTS 'legalcaseuser'@'%' IDENTIFIED BY 'legalcasepass';

-- Kullanıcıya tüm veritabanlarına erişim izni ver
GRANT ALL PRIVILEGES ON `legalcasedb`.* TO 'legalcaseuser'@'%';
GRANT ALL PRIVILEGES ON `legalcase_users`.* TO 'legalcaseuser'@'%';
GRANT ALL PRIVILEGES ON `keycloak`.* TO 'legalcaseuser'@'%';

-- İzinleri hemen uygula
FLUSH PRIVILEGES; 