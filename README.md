# 🕳️ The Void SSG

A static site generator steeped in cosmic horror. Sites are grimoires, content corrupts over time, and Lovecraftian entities lurk in your markdown.

Built with **Java 21 + Spring Boot 3.2** on the backend, **React + TypeScript + Vite** on the frontend, and a **Spring Shell CLI** for terminal-based rituals.

![Java](https://img.shields.io/badge/Java-21-purple)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)
![React](https://img.shields.io/badge/React-19-blue)
![Three.js](https://img.shields.io/badge/Three.js-WebGL-black)
![MySQL](https://img.shields.io/badge/MySQL-8-orange)

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [CLI Commands](#cli-commands)
- [REST API](#rest-api)
- [Frontend](#frontend)
- [Configuration](#configuration)
- [Data Model](#data-model)

---

## Overview

The Void SSG manages **sites** (grimoires) containing **entries** (markdown content). Each site has an **entropy mode** that determines how content corrupts over time. The system scans content for references to Lovecraftian entities (Cthulhu, Nyarlathotep, Hastur, etc.) and applies eldritch side effects — symbol injection, whisper replacement, and content obfuscation.

Builds generate ANSI-formatted narratives describing the corruption process, and a navigation system can hide or rename links based on a viewer's identity and the current time.

---

## Features

- **Entropy-driven corruption** — Content degrades according to configurable entropy modes (None, Daily, User-Based, Cryptographic)
- **Entity detection** — Regex-based scanning for 7 Lovecraftian entities with themed warnings and corruption boosts
- **Narrative builds** — Async build process produces ANSI-colored stories of what happened to your content
- **Viewer-aware navigation** — Links are deterministically shown, hidden, or obfuscated per viewer
- **Interactive CLI** — Full Spring Shell interface with formatted, color-coded terminal output
- **Fluid WebGL background** — Three.js Navier-Stokes fluid simulation on the frontend
- **Java 21 virtual threads** — Async builds and Tomcat requests run on virtual threads

---

## Architecture

```
┌─────────────────────┐     ┌─────────────────────┐
│   React Frontend    │────▶│   Spring Boot API   │
│  (Vite + Three.js)  │     │   (port 8080)       │
└─────────────────────┘     └────────┬────────────┘
                                     │
                            ┌────────▼────────────┐
                            │   Spring Shell CLI   │
                            └────────┬────────────┘
                                     │
                            ┌────────▼────────────┐
                            │   MySQL (the_void)   │
                            └─────────────────────┘
```

| Layer | Tech |
|---|---|
| Backend | Java 21, Spring Boot 3.2, Spring Data JPA, Spring Shell 3.2 |
| Frontend | React 19, TypeScript, Vite, Three.js, Axios, React Router |
| Database | MySQL 8, Flyway (migrations available, Hibernate DDL active) |
| Build | Maven, npm |

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- Node.js 18+
- MySQL 8+

### Database Setup

Create the database:

```sql
CREATE DATABASE the_void;
```

The app uses Hibernate `ddl-auto: update` by default, so tables are created automatically on startup. A Flyway migration is also available at `src/main/resources/db/migration/V1_init_schema.sql` if you prefer managed migrations.

### Backend

```bash
# Build and run
mvn clean package
java -jar target/the-void-ssg-1.0.0.jar

# Or run with Maven
mvn spring-boot:run
```

The server starts on port **8080** with the Spring Shell interactive prompt.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The Vite dev server proxies `/api` requests to the backend on port 8080.

---

## CLI Commands

The Spring Shell CLI provides a full interface for managing sites and entries.

### Site Management

| Command | Description |
|---|---|
| `void init <name> [--mode DAILY] [--sanity 50]` | Create a new grimoire |
| `void list` | List all sites |
| `void info <siteId>` | Show site details |
| `void delete <siteId> --confirm true` | Delete a site |
| `void entropy <siteId> <mode>` | Change entropy mode |
| `void intensity <siteId> <intensity>` | Set corruption intensity (0–100) |
| `void build <siteId>` | Build the site |

### Entry Management

| Command | Description |
|---|---|
| `void entry list <siteId>` | List all entries |
| `void entry show <siteId> <slug>` | Show entry details |
| `void entry add <siteId> --title "..." --slug "..." --content "..."` | Create an entry |
| `void entry update <siteId> <slug> --content "..."` | Update an entry |
| `void entry delete <siteId> <slug> --confirm true` | Delete an entry |
| `void entry corrupt <siteId> <slug> [--viewerHash anonymous]` | Preview corruption |

---

## REST API

### Sites — `/api/sites`

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/sites` | List all sites |
| `GET` | `/api/sites/{id}` | Get a site |
| `POST` | `/api/sites?name=...&entropyMode=...&sanityThreshold=...` | Create a site |
| `PATCH` | `/api/sites/{id}/entropy?mode=...` | Update entropy mode |
| `PATCH` | `/api/sites/{id}/intensity?intensity=...` | Update corruption intensity |
| `DELETE` | `/api/sites/{id}` | Delete a site |

### Entries — `/api/sites/{siteId}/entries`

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/sites/{siteId}/entries` | List entries |
| `GET` | `/api/sites/{siteId}/entries/{slug}` | Get entry (header: `X-Viewer-Hash`) |
| `POST` | `/api/sites/{siteId}/entries?title=...&slug=...&content=...` | Create entry |
| `PUT` | `/api/sites/{siteId}/entries/{slug}?content=...` | Update entry |
| `DELETE` | `/api/sites/{siteId}/entries/{slug}` | Delete entry |
| `GET` | `/api/sites/{siteId}/entries/{slug}/corrupt` | Preview corruption (header: `X-Viewer-Hash`) |

### Builds — `/api/sites/{siteId}/builds`

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/sites/{siteId}/builds` | Trigger async build |
| `GET` | `/api/sites/{siteId}/builds` | Get build history |

---

## Frontend

The React frontend provides a visual interface for managing grimoires.

| Route | Page | Description |
|---|---|---|
| `/` | Site List | Card grid of all grimoires with create form |
| `/site/:siteId` | Site Detail | Tabbed view — Entries, Build History, Settings |
| `/site/:siteId/entries/new` | Entry Create | Form with auto-generated slug |
| `/site/:siteId/entries/:slug` | Entry Detail | Full content view with inline editing and corruption preview |

### Visual Components

- **LiquidEther** — Full-page Three.js fluid simulation (Navier-Stokes) in cosmic purple/green. Animates autonomously and responds to mouse interaction.
- **GooeyNav** — Animated tab/action bar with particle transition effects, used for navigation and action buttons throughout the app.

---

## Configuration

Application settings in `src/main/resources/application.yml`:

| Property | Default | Description |
|---|---|---|
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/the_void` | Database connection |
| `spring.jpa.hibernate.ddl-auto` | `update` | Schema management |
| `spring.flyway.enabled` | `false` | Flyway migrations |

Custom properties under the `void.*` prefix:

| Property | Default | Description |
|---|---|---|
| `void.output.basePath` | `./void-sites` | Output directory for built sites |
| `void.entropy.maxIntensity` | `100` | Maximum corruption intensity |
| `void.entropy.symbols` | `⛧ ☠ ☥ ⛥ ⛤ ⚸` | Symbols injected during corruption |
| `void.entities.enabled` | `true` | Enable entity detection |
| `void.entities.warnOnDetection` | `true` | Warn when entities are found |

### Entropy Modes

| Mode | Behavior |
|---|---|
| `NONE` | No corruption — the void sleeps |
| `DAILY` | Corruption changes with the cosmic calendar |
| `USER_BASED` | Each viewer sees their own corruption |
| `CRYPTOGRAPHIC` | Corruption bound to the site's hash |

---

## Data Model

Four tables in the `the_void` database:

- **`sites`** — Grimoires with entropy settings, build hash, and entity ward
- **`entries`** — Markdown content with corruption level, entity influence, and optimistic locking
- **`build_logs`** — Build narratives with detection/corruption stats
- **`visitors`** — Viewer tracking per site via hashed identity

### Entity Detection

Content is scanned for references to 7 Lovecraftian entities:

| Entity | Triggers |
|---|---|
| Cthulhu | Cthulhu, R'lyeh, ph'nglui |
| Nyarlathotep | Nyarlathotep, Crawling Chaos, Black Pharaoh |
| Yog-Sothoth | Yog-Sothoth, Key and Gate |
| Azathoth | Azathoth, Nuclear Chaos, Demon Sultan |
| Dagon | Dagon, Deep Ones |
| Shub-Niggurath | Shub-Niggurath, Black Goat, Thousand Young |
| Hastur | Hastur, King in Yellow, Carcosa |

Detected entities boost corruption by +15 and generate themed warnings during builds.
