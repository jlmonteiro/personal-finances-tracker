# Personal Finances Tracker

[![Backend CI](https://github.com/jlmonteiro/personal-finances-tracker/actions/workflows/backend.yml/badge.svg)](https://github.com/jlmonteiro/personal-finances-tracker/actions/workflows/backend.yml)
[![Frontend CI](https://github.com/jlmonteiro/personal-finances-tracker/actions/workflows/frontend.yml/badge.svg)](https://github.com/jlmonteiro/personal-finances-tracker/actions/workflows/frontend.yml)
![Version](https://img.shields.io/badge/version-0.0.1-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-2.1-purple?logo=kotlin)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-green?logo=springboot)
![React](https://img.shields.io/badge/React-19-blue?logo=react)
![License](https://img.shields.io/badge/license-Apache%202.0-green)

A personal finances tracker — month-by-month budgeting with quarter breakdowns.

## Prerequisites

- JDK 21+
- Docker & Docker Compose
- Node.js 20+ (or let Gradle download it)

## Quick Start

### Backend

```bash
./gradlew :backend:bootRun
```

Spring Docker Compose plugin will auto-start PostgreSQL.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Vite dev server runs on http://localhost:5173 with API proxy to http://localhost:8080.

## Build

```bash
./gradlew build
```

## Project Structure

```
├── backend/          # Spring Boot 4.x + Kotlin
├── frontend/         # React + Mantine + Vite
├── docker-compose.yml
├── .specs/           # Requirements, design, tasks (SDD)
└── docs/wireframes/  # UI wireframes (Markdown-UI DSL)
```

## License

Apache 2.0
