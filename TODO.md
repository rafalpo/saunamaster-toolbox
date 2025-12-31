# 📋 Project Roadmap: Functional Slices

## Milestone 1: The Living Skeleton (Infra & Communication)
- **Goal**: A hollow but functional system deployed on the cluster with working connectivity.
- **Tasks**:
  - [ ] Initialize Server (Spring Boot) with `/health` actuator endpoint.
  - [ ] Initialize Client (Next.js) displaying "Hello World" from API.
  - [ ] Create Dockerfile for both applications.
  - [ ] Configure environment variables for database connectivity.
- **Definition of Done (DoD)**:
  - [ ] Application is successfully deployed on the K8s cluster.
  - [ ] Frontend fetches and displays data from the backend.
  - [ ] **Deployment**: Verified on a running cluster.

---

## Milestone 2: Inventory Foundation (The Shelf)
- **Goal**: Manage individual items on "The Shelf".
- **Tasks**:
  - [ ] Implement data model for Resources (CRUD).
  - [ ] Create UI for browsing and adding items.
  - [ ] Setup database migrations.
- **Definition of Done (DoD)**:
  - [ ] New items can be added via UI and are persisted in the database.
  - [ ] Repository layer integration tests are passing.
  - [ ] Data validation prevents empty or corrupt entries.
  - [ ] **Deployment**: Updated and tested on the cluster.

---

## Milestone 3: Event Logging (The Usage Fact)
- **Goal**: Record the "fact of usage" without complex stock management.
- **Tasks**:
  - [ ] Implement mechanism to log "Item X used" with a timestamp.
  - [ ] UI: Add "Log Use" button to resource list.
  - [ ] Build a simple history view for each item.
- **Definition of Done (DoD)**:
  - [ ] Clicking "Log Use" creates a record in the fact table.
  - [ ] Historical data is displayed chronologically.
  - [ ] API End-to-End tests verify log persistence.
  - [ ] **Deployment**: Updated and tested on the cluster.

---

## Milestone 4: Composition Logic (Recipes/Blends)
- **Goal**: Create blends from existing items on the shelf.
- **Tasks**:
  - [ ] Implement data model for Blends (Many-to-Many with ratios).
  - [ ] Create "Blend Creator" UI (selecting ingredients from The Shelf).
- **Definition of Done (DoD)**:
  - [ ] Blends correctly reference physical items in the database.
  - [ ] UI allows setting ratios for each ingredient in a blend.
  - [ ] Data integrity: Deleting a resource handles its references in blends.
  - [ ] **Deployment**: Updated and tested on the cluster.

---

## Milestone 5: Scenario Orchestration (The Session)
- **Goal**: A full sauna ritual plan integrating stories, music, and blends.
- **Tasks**:
  - [ ] Implement Session Plan model (description, music URLs, assigned blends).
  - [ ] Build the "Master View" for the Sauna Master to follow during the session.
- **Definition of Done (DoD)**:
  - [ ] All system components (Resources, Blends, Sessions) are interconnected.
  - [ ] Session view displays all assigned resources and story details.
  - [ ] Automated E2E tests cover the main user flow.
  - [ ] **Deployment**: Updated and tested on the cluster.

---

## Milestone 6: Final Polish & Localization
- **Goal**: Multi-language support and mobile optimization.
- **Tasks**:
  - [ ] Implement i18n on Frontend and Backend error messages.
  - [ ] UI/UX optimization for mobile browsers.
- **Definition of Done (DoD)**:
  - [ ] Application is available in at least two languages (EN/PL).
  - [ ] No console errors during language switching.
  - [ ] Final deployment confirms stability across all features.
  - [ ] **Deployment**: Production-ready state on the cluster.
  