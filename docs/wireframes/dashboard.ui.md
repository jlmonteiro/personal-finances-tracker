---
framework: React + Mantine + TanStack Query
theme: ./design-system.md
component: src/pages/Dashboard.tsx
---

# Dashboard

=== ROW ===

||| COLUMN |||
> width: 220px

## Navigation

- **📊 Dashboard**
- 📅 Calendar
- 📋 Table

***

- 🏷️ Categories
- 🏪 Payees
- 💰 Income
- 🔄 Recurring

***

- ⚙️ Settings

--- END ---

||| COLUMN |||
> flex: 1

## June 2026 - Overview

=== ROW ===

::: CARD :::
**Total Income**
## €4,200.00
--- END ---

::: CARD :::
**Total Budget**
## €3,100.00
--- END ---

::: CARD :::
**Total Spent**
## €1,850.00
--- END ---

::: CARD :::
**Net Balance**
## €2,350.00
--- END ---

--- END ---

***

### Budget vs Actual

| Quarter | Budget | Actual | Variance | Usage |
| ------- | ------ | ------ | -------- | ----- |
| Q1 (Jun 1-7) | €800.00 | €750.00 | +€50.00 | (( 94% )) |
| Q2 (Jun 8-15) | €900.00 | €650.00 | +€250.00 | (( 72% )) |
| Q3 (Jun 16-23) | €750.00 | €450.00 | +€300.00 | (( 60% )) |
| Q4 (Jun 24-30) | €650.00 | €0.00 | +€650.00 | (( 0% )) |

***

### Expenses by Category

| Icon | Category | Planned | Actual |
| ---- | -------- | ------- | ------ |
| 🛒 | Groceries | €480.00 | €465.00 |
| ⚡ | Utilities | €320.00 | €310.00 |
| 🚗 | Transport | €200.00 | €185.00 |
| 🐷 | Savings | €400.00 | €400.00 |

***

=== ROW ===
[ View Calendar ](#month-calendar) [ View Table ](#month-table)
--- END ---

--- END ---

--- END ---
