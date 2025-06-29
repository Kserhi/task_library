# Library Management API

## Огляд

Цей проект реалізує REST API для управління бібліотекою з використанням Java Spring Boot, JPA (Hibernate), Bean Validation, PostgreSQL та Swagger (OpenAPI). Забезпечує CRUD-операції для книг та учасників, бізнес-логіку позичання/повернення книг, а також додаткові ендпоїнти для статистики.

## Технології

* Java 17
* Spring Boot 3.5.3
* Spring Data JPA (Hibernate)
* Bean Validation (Hibernate Validator)
* PostgreSQL 14 (Docker)
* springdoc-openapi-starter-webmvc-ui
* Lombok
* JUnit 5, Mockito
* Maven

## Структура проекту

```
src/
├── main/
│   ├── java/com/example/library
│   │   ├── config/      # Конфігурація (Swagger)
│   │   ├── controller/  # REST-контролери
│   │   ├── dto/         # DTO-класи
│   │   ├── model/       # JPA-сутності
│   │   ├── repository/  # Репозиторії Spring Data
│   │   ├── service/     # Сервісний шар
│   │   └── LibraryApplication.java
│   └── resources/
│       ├── application.properties
│       └── db/
└── test/
    └── java/com/example/library/service  # Unit-тести
```

## Налаштування Docker (PostgreSQL)

У корені проекту створіть файл `docker-compose.yml`:

```yaml
services:
  database:
    container_name: crm-db
    image: postgres:14
    environment:
      POSTGRES_DB: "library"
      POSTGRES_USER: "root"
      POSTGRES_PASSWORD: "root"
    ports:
      - "5432:5432"
```

Запустіть контейнер:

```bash
docker-compose up -d
```

## Налаштування підключення до БД

Встановіть у файлі `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/library
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
library.borrow.limit=10
```

## Збірка та запуск

1. Переконайтеся, що запущено Docker-контейнер з PostgreSQL.
2. Виконайте збірку Maven:

   ```bash
   mvn clean install
   ```
3. Запустіть додаток:

   ```bash
   mvn spring-boot:run
   ```

   або

   ```bash
   java -jar target/library-0.0.1-SNAPSHOT.jar
   ```
4. Перейдіть у браузері на Swagger UI:

   ```
   http://localhost:8080/swagger-ui.html
   ```

## Ендпоїнти API

Повний опис доступний у Swagger UI. Основні:

### Книги (`/api/books`)

* `POST /api/books` — додати книгу
* `GET /api/books` — отримати всі книги
* `GET /api/books/{id}` — отримати книгу по ID
* `PUT /api/books/{id}` — оновити книгу
* `DELETE /api/books/{id}` — видалити книгу
* `GET /api/books/borrowed/by-member/{name}` — книги, позичені учасником
* `GET /api/books/borrowed/distinct-titles` — унікальні назви позичених книг
* `GET /api/books/borrowed/statistics` — статистика позичень

### Учасники (`/api/members`)

* `POST /api/members` — створити учасника
* `GET /api/members` — отримати всіх учасників
* `GET /api/members/{id}` — отримати учасника по ID
* `PUT /api/members/{id}` — оновити учасника
* `DELETE /api/members/{id}` — видалити
* `POST /api/members/{memberId}/borrow/{bookId}` — позичити книгу
* `POST /api/members/{memberId}/return/{bookId}` — повернути книгу

## Тести

Юніт-тести для сервісів знаходяться в:

```
src/test/java/com/example/library/service
```

Запуск тестів:

```bash
mvn test
```

## Ліцензія

MIT
