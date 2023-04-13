-- file: 10-create-user-and-db.sql
CREATE DATABASE homework3;
CREATE ROLE program WITH PASSWORD 'test';
GRANT ALL PRIVILEGES ON DATABASE homework3 TO program;
ALTER ROLE program WITH LOGIN;