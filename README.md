# Every Soft Ping Bot

[![CI](https://github.com/oURMIo/every-soft-ping-bot/actions/workflows/ci.yml/badge.svg)](https://github.com/oURMIo/every-soft-ping-bot/actions/workflows/ci.yml)

A Telegram bot that lets users quickly notify all chat participants with a single command. It automatically tracks members who write messages and provides a ping command to mention everyone at once.

## Features

- **Auto-tracking** — automatically registers chat members as they send messages
- **Ping command** — mentions all tracked members in a single message
- **Rate limiting** — 30-second cooldown per chat to prevent spam
- **HTML mentions** — uses Telegram's HTML parsing for proper user mentions

## Tech Stack

- Java 25, Quarkus 3.32
- PostgreSQL 16
- [java-telegram-bot-api](https://github.com/pengrad/java-telegram-bot-api)
- Hibernate ORM Panache
- Docker, GitHub Actions CI

## Bot Commands

| Command | Description |
|---------|-------------|
| `@botname ping` | Mention all tracked chat members |
| `@botname help` | Show usage instructions |
| `@botname about` | Show bot info |

## Getting Started

### Prerequisites

- JDK 25+
- PostgreSQL 16 (or Docker)

### Configuration

Copy `.env.example` and fill in the values:

| Variable | Description |
|----------|-------------|
| `BOT_TOKEN` | Telegram bot token from [@BotFather](https://t.me/BotFather) |
| `DB_HOST` | PostgreSQL host |
| `DB_PORT` | PostgreSQL port |
| `DB_NAME` | Database name |
| `DB_USER` | Database user |
| `DB_PASSWORD` | Database password |

### Run with Docker Compose

```bash
docker-compose up
```

### Run Locally

```bash
./gradlew build
java -jar build/quarkus-app/quarkus-run.jar
```

### Build Docker Image

```bash
docker build -f src/main/docker/Dockerfile.jvm -t esoft-ping-bot:latest .
```

### Run Tests

```bash
./gradlew test
```

Tests use an in-memory H2 database, no external dependencies required.

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.