# Calorie calculator Bot

Multi-module Spring Boot project that explores a calorie-calculation using photo provided in Telegram bot and a ChatGPT integration.

## Overview
Spring Boot non-web application that implements a Telegram bot for food recognition and calorie calculation using OpenAI/ChatGPT. The project uses **Spring Modulith** for modular architecture with event-driven inter-module communication.

### Module Structure
- **tgbot**: Telegram bot message handling, commands, user interaction
- **foodrecognition**: AI-based food recognition and nutritional analysis
- **infrastructure**: Shared configuration (ChatClient bean, prompts)

## Technologies Stack
- Java: 25
- Spring Boot: 3.5.9
- Spring Modulith: 1.4.6
- Spring AI: 1.1.2 (OpenAI)
- Telegram Bots API: 9.1.0
- Lombok

## Prerequisites
- JDK 25 installed and on PATH (JAVA_HOME pointing to JDK 25)
- Maven 3.9+ installed and on PATH

## Running with Docker Compose
This project provides a docker-compose.yml to run the services together with a Postgres database.

1. Build the project jars:
   - On Windows PowerShell: `mvn -DskipTests package`
2. Copy and edit environment variables:
   - Duplicate `.env.example` to `.env` and provide real values for BOT_NAME and BOT_TOKEN, override ports/DB creds.
3. Start the stack:
   - `docker compose up -d --build`