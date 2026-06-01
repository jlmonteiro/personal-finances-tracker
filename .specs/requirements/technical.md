# Requirements: Constraints & Scope

## 1. Assumptions

### AS-1: Single User

**Statement:** Only one user uses the application at a time.

**Impact if Wrong:** Would need multi-tenancy, data isolation, and user management.

---

### AS-2: Docker Available

**Statement:** The host machine has Docker and Docker Compose installed.

**Impact if Wrong:** The database would need to be provisioned manually or an alternative embedded database used.

---

### AS-3: Local or Launcher Access

**Statement:** The application is accessed either locally during development or via a micro-lc launcher in production.

**Impact if Wrong:** Would need additional deployment targets (public internet, CDN, etc.).

---

### AS-4: Modern Browser

**Statement:** The user accesses the application via a modern browser (Chrome, Firefox, Safari, Edge — latest 2 versions).

**Impact if Wrong:** Would need polyfills, fallback UI, and broader compatibility testing.

---

## 2. Out of Scope

### OOS-1: Multi-User Support

**Description:** No support for multiple user accounts or data isolation between users.

**Rationale:** Application is authenticated (single user via Google OAuth) but designed for personal use only. Multi-tenancy adds complexity; can be added later.

---

### OOS-2: Cloud Deployment

**Description:** No cloud hosting, managed databases, or remote access beyond local network.

**Rationale:** Application runs locally or on a personal server behind the launcher.

---

### OOS-3: Bank Integration

**Description:** No automatic import of transactions from bank accounts.

**Rationale:** Adds significant complexity; can be added later as an enhancement.

---

### OOS-4: Mobile App

**Description:** No native mobile application.

**Rationale:** The responsive web UI accessed via browser is sufficient.

---

### OOS-5: Multi-Currency Support

**Description:** No support for multiple currencies or currency conversion.

**Rationale:** Application uses a single currency configured during setup. Multi-currency adds exchange rate complexity; can be added later.
