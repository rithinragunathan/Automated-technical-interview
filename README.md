# Automated Technical Mock Interview Sandbox

An automated system to evaluate candidate code submissions in a sandboxed environment, featuring AI-based mock interviews.

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
- Copy `.env.example` to `.env` and configure your API keys (e.g., OpenAI API Key).
- For local dev out of Docker, you can start Postgres and Redis using `docker-compose up postgres redis -d`.

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

## License
MIT
