# 🚀 URL Shortener API

A production-ready REST API that shortens long URLs, tracks analytics, and uses caching for high performance.  
Built with **Spring Boot**, **Redis**, and **Docker**, documented with **Swagger UI**.

🔗 Turn `https://very-long-domain.com/some/path?query=123` into `http://localhost:8081/xyz123`

---

## Table of Contents:
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
- [Example Usage](#-example-usage)

---

## 🌟 Features
- ✅ Shorten any valid URL
- 📊 Analytics on shortened links (usage count, timestamps)
- ⚡ In-memory caching with Redis for fast lookups
- 🐳 Fully Dockerized setup (DB + app)
- 📖 Swagger UI for interactive API exploration

---

## 🛠️ Tech Stack
- **Backend:** Java 17, Spring Boot
- **Database:** PostgreSQL/MySQL (Dockerized)
- **Cache:** Redis
- **Documentation:** Swagger UI
- **Containerization:** Docker & Docker Compose

---

## 🚀 Getting Started

### 1️⃣ Clone repository
```bash

git clone https://github.com/user/url-shortener-service
cd url-shortener-service

```

### 2️⃣ Build Docker image with Jib
```bash
mvn compile jib:dockerBuild
```

### 3️⃣ Start services
```bash
docker-compose up -d
```

### 4️⃣ Verify Services
* Swagger UI: http://localhost:8081/swagger-ui.html

## 🔗 Example Usage
```
POST /shorten
Content-Type: application/json

{
  "url": "https://very-long-domain.com/some/path?query=123"
}

Response:
{
  "shortUrl": "http://localhost:8080/xyz123"
}
```

