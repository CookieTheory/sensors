# System Sensor Dashboard

A desktop web application that monitors and reports real-time computer sensor data:

- CPU Temperature
- CPU Power Usage
- Network Usage

Built with:
- 🧠 Backend: Java (Spring Boot)
- ⚡ Frontend: Vite + React

---

## 📁 Project Structure

git root/  
├── src/main/ # Java Spring Boot backend (sensor data API)  
└── src/main/webapp/ # Vite + React frontend (dashboard UI)

## 🚀 Getting Started

### 🧰 Prerequisites

- Java 21+
- Maven

Run maven build (mvn clean install) to install and create .jar file.
You can dockerize or just run app on host machine you want to monitor.
Create external appplication.properties file where .jar is.
Template for application.properties can be found under src/main/resources/.

The backend will start at http://localhost:{provided port in app.properties}

API endpoints:  
├── /api/temps  
├── /api/tempsjson  
├── /api/watts  
├── /api/wattsjson  
├── /api/network  
└── /api/networkjson  

Frontend will start at:
http://localhost:{provided port in app.properties}/{provided root in app.properties}

It shows all sensor data in small dashboards, green for temps below 50°, orange for temps from 50° to 70° and red for temps above 70°.

## 📊 Features

- Live CPU temperature and power readings
- Real-time network bandwidth usage
- Auto-refreshing dashboard UI
- Responsive and lightweight design

## 🛠 Technologies used

- Java 21 + Spring Boot
- Vite + React + Axios
- Linux lm_sensors library
- REST API

## 📃 License

MIT License

## 🙋‍♂️ Author

CookieTheory - @CookieTheory
