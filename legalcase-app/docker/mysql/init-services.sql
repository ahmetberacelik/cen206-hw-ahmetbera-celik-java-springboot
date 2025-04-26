-- Ana veritabanı zaten 'legalcasedb' olarak oluşturuldu

-- User service veritabanı
CREATE DATABASE IF NOT EXISTS legalcase_users;
GRANT ALL PRIVILEGES ON legalcase_users.* TO 'legalcaseuser'@'%';

-- Case service veritabanı
CREATE DATABASE IF NOT EXISTS legalcase_cases;
GRANT ALL PRIVILEGES ON legalcase_cases.* TO 'legalcaseuser'@'%';

-- Keycloak veritabanı
CREATE DATABASE IF NOT EXISTS keycloak;
GRANT ALL PRIVILEGES ON keycloak.* TO 'legalcaseuser'@'%';

-- Client service veritabanı
CREATE DATABASE IF NOT EXISTS legalcase_clients;
GRANT ALL PRIVILEGES ON legalcase_clients.* TO 'legalcaseuser'@'%';

-- Hearing service veritabanı
CREATE DATABASE IF NOT EXISTS legalcase_hearings CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON legalcase_hearings.* TO 'legalcaseuser'@'%';

-- İzinleri hemen uygula
FLUSH PRIVILEGES;