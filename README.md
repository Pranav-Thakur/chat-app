# ChatApp 📱💬

A secure, real-time 1-on-1 chat application built with **Spring Boot**, **WebSockets**, **JWT authentication**, and **MongoDB**. Inspired by WhatsApp's clean flow and production-readiness, this app supports OTP-based login, active chat tracking, and scalable WebSocket communication.

---

## 🚀 Features

- 📲 **OTP-based login** using phone number using https://console.twilio.com/
- 🔐 **JWT-based authentication** stored securely in HttpOnly cookies
- 💬 **1-on-1 chat** with real-time message delivery using **STOMP over WebSockets**
- ✅ **Message read receipts**
- 💾 **Chat persistence** in MongoDB
- 📂 Lightweight front-end using **HTML, JS, CSS**
- 🌐 Production-ready structure with support for **session tracking**, **device IDs**, and **multi-node JWT validation**

---

## 🛠️ Tech Stack

| Layer            | Tech Used                                                       |
|------------------|-----------------------------------------------------------------|
| Backend          | Spring Boot 2.7+, WebSocket, JWT, MongoDB                       |
| Frontend         | Vanilla JS, HTML5, CSS3                                         |
| CI/CD            | GitHub Actions                                                  |
| Auth             | JWT tokens, Device ID                                   |
| Storage          | MongoDB (PersonalChats, ChatMessages), MySQL (Users, Login Sessions) |
| Dev Tools        | Postman, TablePlus, IntelliJ, Chrome Dev Tools                  |
| Deployment Ready | Docker / Render.com                                             |

---

## 🔑 Architecture Overview

- Client authenticates via OTP → JWT is set as HttpOnly cookie
- User connects via WebSocket with STOMP client
- Messages are pushed via `/app/chat.send`, routed to `/topic/messages` if recipient is online
- Chats are stored in MongoDB under sender-receiver `PersonalChats`
- Each session/device tracked using a `deviceId` and `ip`

---

## 🚦 Run Locally

### ✅ Prerequisites
- Java 11
- MongoDB Community (v6+)
- MySQL (optional, for hybrid storage)
- Maven
- Node.js (only for UI testing, optional)

### ⚙️ Steps

1. **Clone repo**
   ```bash
   git clone https://github.com/Pranav-Thakur/chatapp.git
   cd chat-app
   run mysql and mongodb instance locally and put that in secret.properties
   mvn clean install -DskipTest
   ./mvnw spring-boot:run
   Visit: http://localhost:8080/login.html
   ```

### 📡 Deployment Notes
- Ensure HTTPS (JWT in cookies won’t work over http)
- Use SameSite=Strict and Secure=true for cookies
- JWT validation is stateless — works fine in multi-instance deployments
- Render/Heroku/Nginx reverse proxy should support WebSocket upgrade


### 🎯 Roadmap (Suggestions)
- ✅ Real-time chat
- ⏳ Group chats
- 📷 Image/file support
- 🧠 Typing indicators
- 🔔 Push notifications


---

## 🧪 Testing with browser
- Local: Open Browser, https://localhost:8080/login.html
- Prod : Open Browser, https://chat-app.onrender.com/login.html

---

## 🛠 Configuration Notes
- For H2 in-memory: Set in application.yml
- For MySQL: Update application.yml with DB credentials
- Use column type BINARY(16) for UUIDs
- Ensure mysql-connector-java is added in pom.xml

---

## 🙏 Acknowledgements
- Inspired by HIT: The Third Case and Kafka's event-driven architecture.
- Built for learning and exploring full-stack system design with Spring Boot.

## 📌 Author
- Pranav Thakur | Software Engineer | Systems Thinker
---

Let me know if you'd like to [add CI/CD instructions or Dockerization setup](dr) — would be the next logical prod step.

---