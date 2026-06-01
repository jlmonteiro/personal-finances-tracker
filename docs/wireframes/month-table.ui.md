---
framework: React + Mantine + TanStack Query
component: src/pages/MonthTable.tsx
---

# Month - Table View

=== ROW ===

||| COLUMN |||
> width: 220px

## Navigation

- 📊 Dashboard
- 📅 Calendar
- **📋 Table**

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

=== ROW ===
> align: center
[ ← ](#prev-month) **June 2026** [ → ](#next-month)
--- END ---

***

### Q1: Jun 1 - Jun 7
> hint: Quarter background tone 1

| Status | Payee | Category | Title | Expected | Actual | Due | Paid |
| ------ | ----- | -------- | ----- | -------- | ------ | --- | ---- |
| (( 🟢 Paid )) | Dunnes | Groceries | Weekly shop | €120.00 | €115.50 | Jun 5 | Jun 5 |
| (( 🔴 Overdue )) | Landlord | Housing | Rent | €1,200.00 | - | Jun 1 | - |
| (( 🟢 Paid )) | ESB | Utilities | Electricity | €85.00 | €82.30 | Jun 3 | Jun 3 |

> Q1 Subtotal: Budget €1,405.00 | Actual €197.80

***

### Q2: Jun 8 - Jun 15
> hint: Quarter background tone 2

| Status | Payee | Category | Title | Expected | Actual | Due | Paid |
| ------ | ----- | -------- | ----- | -------- | ------ | --- | ---- |
| (( 🟡 Pending )) | Dunnes | Groceries | Weekly shop | €120.00 | - | Jun 12 | - |
| (( 🟡 Pending )) | Vodafone | Utilities | Mobile | €45.00 | - | Jun 10 | - |

> Q2 Subtotal: Budget €165.00 | Actual €0.00

***

### Income this month

| Source | Amount | Date | Quarter | Adjusted |
| ------ | ------ | ---- | ------- | -------- |
| Salary | €3,500.00 | Jun 25 | Q4 | No |
| Freelance | €700.00 | Jun 15 | Q2 | No |

***

=== ROW ===
[ + Add Expense ](#add-expense) [ Export Markdown ](#export) [ View Calendar ](#calendar-view)
--- END ---

--- END ---

--- END ---
