# Frontend Architecture

## Overview

React SPA built with Vite, Mantine UI, and TypeScript. Designed as a micro-frontend composable by micro-lc, with no top-level chrome.

---

## Project Structure

```
frontend/
├── public/
├── src/
│   ├── api/                  # API client (fetch wrapper, types)
│   │   ├── client.ts         # Base HTTP client with auth header
│   │   ├── configuration.ts
│   │   ├── income-sources.ts
│   │   ├── categories.ts
│   │   ├── payees.ts
│   │   ├── financial-months.ts
│   │   ├── expenses.ts
│   │   └── recurring-expenses.ts
│   ├── components/           # Shared/reusable components
│   │   ├── IconPicker.tsx    # Tabler icon searchable picker
│   │   ├── StatusBadge.tsx   # Expense status indicator
│   │   ├── MonthNavigator.tsx # Arrow buttons for prev/next month
│   │   └── PageLayout.tsx    # Internal page wrapper (no chrome)
│   ├── pages/                # Route-level components
│   │   ├── Dashboard.tsx
│   │   ├── Setup.tsx
│   │   ├── MonthCalendar.tsx
│   │   ├── MonthTable.tsx
│   │   ├── Categories.tsx
│   │   ├── Payees.tsx
│   │   ├── IncomeSources.tsx
│   │   ├── RecurringExpenses.tsx
│   │   └── Settings.tsx
│   ├── hooks/                # Custom React hooks
│   │   ├── useApi.ts         # Data fetching with loading/error states
│   │   └── useCurrentMonth.ts
│   ├── types/                # TypeScript interfaces (mirrors API DTOs)
│   ├── utils/                # Helpers (date formatting, currency, etc.)
│   ├── App.tsx               # Router setup
│   └── main.tsx              # Entry point
├── package.json
├── vite.config.ts
└── tsconfig.json
```

---

## Routing

Using React Router v6. All routes are internal — no top-level layout chrome.

| Path | Page | Description | Traces |
|------|------|-------------|--------|
| `/` | Dashboard | Home with charts and summary | FR-14, FR-15, FR-16 |
| `/setup` | Setup | Wizard (redirected here if no config) | FR-1, FR-2, FR-3, FR-4 |
| `/month/:id/calendar` | MonthCalendar | Calendar view with quarter highlighting | FR-19 |
| `/month/:id/table` | MonthTable | Table view per quarter | FR-20 |
| `/categories` | Categories | CRUD for categories | FR-5 |
| `/payees` | Payees | CRUD for payees | FR-6, FR-7 |
| `/income-sources` | IncomeSources | Manage income sources | FR-4 |
| `/recurring-expenses` | RecurringExpenses | Manage recurring templates | FR-10 |
| `/settings` | Settings | Update configuration | FR-2, FR-3, FR-4 |

**Navigation guard:** On app load, check `GET /api/v1/configuration`. If 404, redirect to `/setup`.

---

## State Management

Lightweight approach — no global store (Redux/Zustand). Use:

- **React Query (TanStack Query)** for server state (API data, caching, refetching)
- **React context** only for app-wide config (currency, month start day)
- **Local state** for form inputs and UI toggles

**Rationale:** The app is mostly CRUD with server-derived state. React Query handles caching, optimistic updates, and background refetching without a separate store.

---

## API Client

A thin fetch wrapper that:
1. Prepends base URL (`/api/v1`)
2. Attaches Bearer token from micro-lc (if available) or skips in dev
3. Parses JSON responses
4. Throws typed errors (RFC 7807 Problem Details)

```typescript
// api/client.ts
const baseUrl = '/api/v1';

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const token = getAuthToken(); // from micro-lc context or null in dev
  const res = await fetch(`${baseUrl}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token && { Authorization: `Bearer ${token}` }),
      ...options?.headers,
    },
  });
  if (!res.ok) throw await res.json(); // RFC 7807 error
  return res.json();
}
```

## Navigation

Internal sidebar using Mantine `NavLink`. Visible on all pages except the Setup wizard.

```
┌──────────────────────────────────────────┐
│ Sidebar        │  Page Content            │
│                │                          │
│ 📊 Dashboard   │                          │
│ 📅 Calendar    │                          │
│ 📋 Table       │                          │
│ ─────────────  │                          │
│ 🏷️ Categories  │                          │
│ 🏪 Payees      │                          │
│ 💰 Income      │                          │
│ 🔄 Recurring   │                          │
│ ─────────────  │                          │
│ ⚙️ Settings    │                          │
└──────────────────────────────────────────┘
```

- Collapsible on smaller screens (Mantine `AppShell` with responsive navbar)
- Active item highlighted based on current route
- Calendar and Table views share a "Month" section with the MonthNavigator

---

## Forms

Using **React Hook Form** with Mantine integration (`@mantine/form` or `react-hook-form` + `@hookform/resolvers`).

- Zod for schema validation (type-safe, composable)
- Form state managed by React Hook Form (not component state)
- Validation runs on blur and submit

---

### MonthNavigator

Shared between Calendar and Table views. Arrow buttons + month/year display.

```
  ← June 2026 →
```

Navigates by loading the previous/next financial month from the API.

### IconPicker

Modal with a searchable grid of Tabler icons. User types to filter, clicks to select. Includes a helper link to https://tabler.io/icons.

### StatusBadge

Renders expense status with colour:
- 🟢 **Paid** — green
- 🔴 **Overdue** — red
- 🟡 **Pending** — yellow/amber

---

## Charts (Mantine Charts / Recharts)

| Chart | Page | Library | Traces |
|-------|------|---------|--------|
| Bar chart (budget vs actual per quarter) | Dashboard | Mantine BarChart | FR-15 |
| Pie/donut (category breakdown) | Dashboard | Mantine PieChart | FR-16 |
| Progress bars (budget usage per quarter) | Dashboard | Mantine Progress | FR-14 |

---

## Micro-Frontend Integration

### Standalone (dev)

- Vite dev server on `http://localhost:5173`
- Proxy: `/api` → `http://localhost:8080`
- No auth token needed (backend dev profile)
- Full-page rendering (no launcher)

### Composed (production)

- Built as static assets, served by nginx or loaded by micro-lc
- micro-lc injects auth token via shared context or event bus
- Renders inside launcher's content area
- No top-level chrome rendered

### Vite Config

```typescript
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080'
    }
  },
  build: {
    outDir: 'dist',
  }
})
```

---

## Theming

- Use Mantine's theme provider
- Define a base theme (colours, fonts, spacing)
- Support CSS variable injection from micro-lc launcher for consistent theming across apps
- Dark mode support via Mantine's `ColorSchemeProvider`

---

## UI Wireframes (Markdown-UI DSL)

Low-fidelity wireframes in `docs/wireframes/` using [Markdown-UI DSL](https://github.com/MegaByteMark/markdown-ui-dsl). Each `.ui.md` file includes a `component:` frontmatter key linking to the target React component.

| Wireframe | File | Traces |
|-----------|------|--------|
| Setup (Currency) | [setup-step1.ui.md](../../docs/wireframes/setup-step1.ui.md) | FR-1, FR-2 |
| Setup (Month Start) | [setup-step2.ui.md](../../docs/wireframes/setup-step2.ui.md) | FR-3 |
| Setup (Income) | [setup-step3.ui.md](../../docs/wireframes/setup-step3.ui.md) | FR-4 |
| Dashboard | [dashboard.ui.md](../../docs/wireframes/dashboard.ui.md) | FR-14, FR-15, FR-16 |
| Calendar View | [month-calendar.ui.md](../../docs/wireframes/month-calendar.ui.md) | FR-19 |
| Table View | [month-table.ui.md](../../docs/wireframes/month-table.ui.md) | FR-20 |
| Categories | [categories.ui.md](../../docs/wireframes/categories.ui.md) | FR-5 |
| Payees | [payees.ui.md](../../docs/wireframes/payees.ui.md) | FR-6, FR-7 |
| Add Expense | [add-expense.ui.md](../../docs/wireframes/add-expense.ui.md) | FR-10, FR-13 |
| Record Payment | [record-payment.ui.md](../../docs/wireframes/record-payment.ui.md) | FR-12 |
