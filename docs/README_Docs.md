# Документация

# 💳 Bank Cards Management System

RESTful API для управления банковскими картами. Реализовано в рамках демонстрации навыков Middle Java Developer.

## 🛠 Технологический стек
- **Java 21** / Spring Boot 3.2.4
- **PostgreSQL** (порт 5435)
- **Liquibase** (управление миграциями БД)
- **Spring Security + JWT** (авторизация по ролям: ADMIN, USER)
- **MapStruct** (безопасное маскирование номеров карт)
- **Lombok** (сокращение шаблонного кода)
- **SpringDoc OpenAPI (Swagger)** (интерактивная документация)

## 🚀 Быстрый запуск

1. **Инфраструктура (Docker)**:
   Запустите контейнер с базой данных:
   ```bash
   docker-compose up -d
