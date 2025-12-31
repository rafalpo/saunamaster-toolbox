# SaunaMaster Toolbox 🧖‍♂️🌿

A specialized digital assistant designed for Sauna Masters to manage their professional resources, create unique aromatic blends, and plan immersive sauna sessions.

## 🌟 Features

- **The Shelf (Inventory):** Track your essential oils, towels, and other equipment. Simple "usage logging" to keep track of what was used and when.
- **Blends Gallery:** Create and store your secret recipes. Define ratios of different essential oils (e.g., Anise + Bergamot Mint).
- **Session Planner:** Draft your sauna rituals by combining stories, music playlists, and required resources/blends in one place.
- **Usage History:** Automated logging of which oils or blends were used during specific sessions.

## 🛠 Tech Stack

- **Backend:** Java 21 with Spring Boot 3.x
- **Frontend:** React.js / Next.js (Tailwind CSS for styling)
- **Database:** PostgreSQL (External)
- **Orchestration:** Kubernetes (K8s)

## 🏗 Deployment & Infrastructure (Kubernetes)

The application is designed to be cloud-native and deployed as a single-tenant instance for a Sauna Master or a specific Spa Center.

### Deployment components:
- **Deployment:** Separate pods for Backend and Frontend.
- **Services:** Internal communication via ClusterIP.
- **Ingress:** Configured to expose the application to the internet.
- **Configuration:**
  - `ConfigMap`: For non-sensitive data (e.g., language settings, UI theme).
  - `Secrets`: For sensitive credentials (DB_URL, DB_USERNAME, DB_PASSWORD).

### Environment Variables required:
To run the container, the following variables must be provided via K8s Secrets or Docker Environment variables:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

## 🌍 Roadmap

- [ ] Core Resource Management (The Shelf)
- [ ] Blend Recipe Creator
- [ ] Session Notes & History
- [ ] Multi-language support (English, Polish, German)

## 📄 License

This project is licensed under the **MIT License** - feel free to use, modify, and distribute it in your sauna community!

---
*Created as part of the "12 Apps in 12 Months" challenge.*