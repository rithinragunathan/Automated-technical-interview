# Automated Technical Mock Interview Sandbox

An automated system to evaluate candidate code submissions in a sandboxed environment, featuring built-in mock interviews.

## Architecture

```text
                  +-----------------+
                  |     Browser     |
                  +--------+--------+
                           |
                           v
                  +-----------------+
                  |   Nginx Proxy   |
                  +--------+--------+
                           |
            +--------------+---------------+
            |                              |
            v                              v
    +----------------+             +---------------+
    |  Frontend SPA  |             |  Backend API  |
    +----------------+             +-------+-------+
                                           |
                         +-----------------+-----------------+
                         |                 |                 |
                         v                 v                 v
                 +--------------+  +---------------+ +---------------+
                 |  PostgreSQL  |  |     Redis     | | Sandbox Worker|
                 +--------------+  +---------------+ +-------+-------+
                                                             |
                                                             v
                                                     +---------------+
                                                     | Docker Socket |
                                                     +---------------+
```

## Prerequisites
- Docker & Docker Compose
- Java 21 (for local development)
- Node.js 20+ (for local development)

## Quick Start

1. `cp .env.example .env`
2. Configure `.env` if necessary.
3. `docker-compose up --build`

Access the application at http://localhost.

## Environment Setup Instructions
- Copy `.env.example` to `.env`, set `GEMINI_API_KEY`, and configure the local database and runtime settings.
- For local dev out of Docker, you can start MySQL and Redis using `docker compose up mysql redis -d`.

## Module Descriptions
- **frontend**: React SPA.
- **backend**: Spring Boot REST API for managing users, questions, and evaluating responses.
- **sandbox-worker**: Isolated Spring Boot service for running untrusted code securely using Docker Engine API.
- **nginx**: Reverse proxy mapping `/api` to the backend and the rest to the frontend.

## API Overview
- `POST /api/auth/*`: Authentication.
- `GET /api/questions`: Get questions.
- `POST /sandbox/execute`: Execute code (Internal).

## Development Workflow
Use `SPRING_PROFILE=dev` for hot-reloading and development specific configs.
Frontend dev server runs on `localhost:5173`.

## Security Notes
- Sandbox worker uses `no-new-privileges` and limits CPU/Memory to isolate code execution.
- Networks are restricted so the sandbox doesn't access internal DBs.

## Troubleshooting
- **Docker permission denied**: Make sure the docker socket is mounted correctly and the sandbox-worker can access it.
- **Code timeouts**: Adjust `SANDBOX_TIMEOUT_SECONDS` in `.env`.
- **Gemini configuration**: Set `GEMINI_API_KEY` in `.env`. The key is read only from the environment and is never stored in source code.
- **Database access denied during local backend startup**: `.env` is loaded by Docker Compose, but not by Spring Boot when started directly from an IDE or Maven. Set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` in the run configuration, or start the backend with `docker compose up --build`. If the MySQL volume was initialized with different credentials, update the variables to match that database or recreate the volume when its data can be discarded.

## License
MIT
