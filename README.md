# REST-CRUD Person-Department

## Описание
Spring Boot приложение по работе с сущностями Person (люди) и Department (отдел). Реализованы базовые CRUD операции над ними.
 
**Person**:
* `GET /` – получение подробной информации о всех людях и о том, какому они принадлежат отделу.
* `GET /{id}` – получение подробной информации о человеке. Если информация по id не найдена, то возвращать 404 ошибку.
* `POST /` – создание новой записи о человеке.
* `PATCH /{id}` – обновление информации о человеке. Если информация по id не найдена, то возвращать 404 ошибку.
* `DELETE /{id}` – удаление информации о человеке и удаление его из отдела. Если запись с таким id не найдена, ничего не делать.

**Department**:
* `GET /` – получение краткой информации о всех департаментах.
* `GET /{id}` – получение подробной информации о департаменте и краткой информации о людях в нем. Если информация по id не найдена, то возвращать 404 ошибку.
* `POST /` – создание нового департамента.
* `PATCH /{id}` – обновление данных о департаменте. Если информация по id не найдена, то возвращать 404 ошибку.
* `DELETE /{id}` – удаление всех людей из департамента и удаление самого департамента. Если запись с таким id не найдена, ничего не делать.
* `POST /{departmentId}/{personId}` – добавление нового человека в департамент. Если не найден человек или департамент, отдавать 404 ошибку.
Если департамент закрыт, то отдавать 409 ошибку.
* `DELETE /{departmentId}/{personId}` – удаление человека из департамента. Если департамент не найден, отдавать 404 ошибку, если не найден человек в департаменте, то ничего не делать.
* `POST /{id}/close` – удаление всех людей из департамента и установка отметки на департаменте, что он закрыт для добавления новых людей. Если информация по id не найдена, то возвращать 404 ошибку.

Сущность _Person_ имеет поля:
```postgresql
CREATE TABLE person
(
    id          INTEGER NOT NULL CONSTRAINT person_pkey PRIMARY KEY,
    first_name  VARCHAR(80) NOT NULL,
    last_name   VARCHAR(80) NOT NULL,
    middle_name VARCHAR(80),
    age         INTEGER
);
```

Сущность _Department_ имеет поля:
```postgresql
CREATE TABLE department
(
    id     INTEGER NOT NULL CONSTRAINT department_pkey PRIMARY KEY,
    name   VARCHAR(80) NOT NULL CONSTRAINT idx_department_name UNIQUE,
    closed BOOLEAN DEFAULT FALSE NOT NULL
);
```
1. Создание схемы базы данных реализовано через скрипты миграции flyway.
1. Для работы с базой данных использован Hibernate (т.е. EntityManager).
1. По описанию методов генерируется OpenAPI спецификация, описание в json доступно по адресу http://localhost:8080/swagger-ui.html.
1. Используя Jacoco (code coverage), генерируется отчет о покрытии и деплоится его на [https://codecov.io/](https://codecov.io/).
1. В GitHub Actions происходит сборка и прогон интеграционных тестов через newman. 

## Сборка приложения 
```shell script
# запустить PostgreSQL в docker-контейнере
docker compose up -d postgres

# загружает gradle wrapper 6.8
./gradlew wrapper

# сборка проекта, прогон unit-тестов, локальный запуск приложения (по-умолчанию профиль local)
./gradlew clean build bootRun 
```
