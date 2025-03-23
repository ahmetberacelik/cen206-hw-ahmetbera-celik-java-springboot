-- Create Keycloak database if it doesn't exist
CREATE DATABASE IF NOT EXISTS keycloak;

-- Grant all privileges on keycloak database to legalcaseuser
GRANT ALL PRIVILEGES ON keycloak.* TO 'legalcaseuser'@'%';

-- Flush privileges to ensure changes take effect
FLUSH PRIVILEGES; 