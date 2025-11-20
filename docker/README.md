# Docker Setup for Internship Placement System CLI

This folder contains everything needed to run the Java CLI application in Docker.

## How to Build and Run

1. **Build the Docker image:**

   ```sh
   docker build -t internship-system .
   ```

2. **Run the CLI app interactively:**

   ```sh
   docker run -it internship-system
   ```

3. **Persist data changes (mount volume):**
   ```sh
   docker run -it -v $(pwd)/../data:/app/data internship-system
   ```
   _(On Windows, use `%cd%` instead of `$(pwd)`)_

## Dockerfile Overview

- Uses `openjdk:17-slim` base image
- Copies all Java source files and data
- Compiles with `javac *.java`
- Runs the CLI app with `java InternshipPlacementSystem`

## Advanced: Docker Compose

Create a `docker-compose.yml` like this:

```yaml
version: "3.8"
services:
  internship-system:
    build: .
    stdin_open: true
    tty: true
    volumes:
      - ../data:/app/data
```

Run with:

```sh
docker-compose run internship-system
```
