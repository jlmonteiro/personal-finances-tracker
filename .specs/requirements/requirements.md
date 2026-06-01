# Requirements Index

## Overview

Personal Finances Tracker — a local-first application to manage personal finances month by month. Each month is divided into 4 roughly equal periods (quarters) for budgeting and expense tracking.

**The North Star:** A simple, self-hosted tool that gives full visibility and control over monthly personal finances.

**Stakeholders:**
- **End Users:** Individual managing their personal finances locally
- **Developers:** Open-source contributors on GitHub

---

## Requirement Classifications

All specifications are categorized into three types:

- **User Journeys (UJ):** Narrative-driven scenarios describing an end-to-end goal from the user's perspective.
- **Functional Requirements (FR):** Specific features or behaviors the system MUST perform.
- **Non-Functional Requirements (NFR):** Quality attributes such as performance, security, and reliability.

---

## EARS Pattern

Requirements use the **EARS (Easy Approach to Requirements Syntax)** pattern:

- **Ubiquitous:** "The system shall..." (Always true)
- **Event-driven:** "When <trigger>, the system shall..."
- **Unwanted Behavior:** "If <condition>, then the system shall..."
- **State-driven:** "While <state>, the system shall..."
- **Optional:** "Where <feature exists>, the system shall..."
- **Complex:** Combinations of the above triggers.

[EARS Documentation](https://alistairmavin.com/ears/)

---

## 1. User Journeys

| ID | Journey | Description |
| :--- | :--- | :--- |
| [**UJ-1**](setup.md#uj-1) | First-Time Setup | User configures the application on first launch. |
| [**UJ-2**](monthly-planning.md#uj-2) | Monthly Planning | User plans a new month by entering expected expenses. |
| [**UJ-3**](monthly-planning.md#uj-3) | Expense Tracking | User records actual expenses as they occur. |
| [**UJ-4**](dashboard.md#uj-4) | Dashboard Review | User reviews financial insights on the home page. |

---

## 2. Setup

| ID | Requirement | Description |
| :--- | :--- | :--- |
| [**FR-1**](setup.md#fr-1) | Setup Wizard | First-run configuration wizard. |
| [**FR-2**](setup.md#fr-2) | Currency Configuration | User selects their currency. |
| [**FR-3**](setup.md#fr-3) | Month Start Day | User configures which day the financial month starts. |
| [**FR-4**](setup.md#fr-4) | Income Configuration | User configures income sources with amounts, frequencies, and payment dates. |
| [**FR-17**](setup.md#fr-17) | Income Payment Date Adjustment | Adjust income payment dates per month (e.g., bank holidays). |

---

## 3. Categories & Payees

| ID | Requirement | Description |
| :--- | :--- | :--- |
| [**FR-5**](categories-payees.md#fr-5) | Category Management | CRUD operations for expense categories with Tabler icon selection. |
| [**FR-6**](categories-payees.md#fr-6) | Payee Management | CRUD operations for payees. |
| [**FR-7**](categories-payees.md#fr-7) | Payee-Category Association | A payee can be linked to multiple categories. |

---

## 4. Monthly Planning & Expenses

| ID | Requirement | Description |
| :--- | :--- | :--- |
| [**FR-8**](monthly-planning.md#fr-8) | Month Creation | Create a new financial month for planning. |
| [**FR-9**](monthly-planning.md#fr-9) | Quarter Division | Each month is divided into 4 roughly equal periods. |
| [**FR-10**](monthly-planning.md#fr-10) | Expense Planning | Add expected expenses with due dates, supporting single and recurring entries. |
| [**FR-11**](monthly-planning.md#fr-11) | Budget Calculation | System calculates quarter budgets from planned expenses. |
| [**FR-12**](monthly-planning.md#fr-12) | Expense Recording | Record actual expense payments. |
| [**FR-13**](monthly-planning.md#fr-13) | Expense Category Selection | Each expense must have exactly one category selected from the payee's associated categories. |
| [**FR-18**](monthly-planning.md#fr-18) | Expense Status | Derived status (pending, paid, overdue) based on dates and values. |
| [**FR-22**](monthly-planning.md#fr-22) | Export Month to Markdown | Export full month breakdown as Markdown for LLM analysis. |

---

## 5. Dashboard & Monthly Views

| ID | Requirement | Description |
| :--- | :--- | :--- |
| [**FR-14**](dashboard.md#fr-14) | Dashboard Home | Home page with summary, charts, and progress bars. |
| [**FR-15**](dashboard.md#fr-15) | Budget vs Actual | Bar chart of planned vs actual spending per quarter. |
| [**FR-16**](dashboard.md#fr-16) | Category Breakdown | Pie/donut chart of expenses by category. |
| [**FR-19**](dashboard.md#fr-19) | Calendar View | Month calendar with quarter highlighting, income/expenses, and status icons. |
| [**FR-20**](dashboard.md#fr-20) | Table View | Per-quarter table with full expense details. |
| [**FR-21**](dashboard.md#fr-21) | Recurring Expense Monthly Override | Modify recurring expense values for a specific month only. |

---

## 6. Constraints & Scope

### Assumptions

| # | Assumption | Detail |
|---|-----------|--------|
| [**AS-1**](technical.md#as-1) | Single user | Only one user uses the application at a time. |
| [**AS-2**](technical.md#as-2) | Docker available | The host machine has Docker installed for the database. |
| [**AS-3**](technical.md#as-3) | Local or launcher access | Accessed locally in dev or via micro-lc launcher in production. |
| [**AS-4**](technical.md#as-4) | Modern browser | User accesses via a modern browser (latest 2 versions). |

### Out of Scope

| # | Item | Detail |
|---|------|--------|
| [**OOS-1**](technical.md#oos-1) | Multi-user support | Single authenticated user; no multi-tenancy. |
| [**OOS-2**](technical.md#oos-2) | Cloud deployment | Application is designed for local use only. |
| [**OOS-3**](technical.md#oos-3) | Bank integration | No automatic import from bank accounts. |
| [**OOS-4**](technical.md#oos-4) | Mobile app | No native mobile application. |
| [**OOS-5**](technical.md#oos-5) | Multi-currency | Single currency only; no conversion support. |

---

## 7. Glossary

| Term | Definition |
| :--- | :--- |
| **Quarter** | One of 4 roughly equal periods within a financial month. |
| **Payee** | An entity (store, service, person) to whom expenses are paid. |
| **Category** | A classification for expenses (e.g., Groceries, Utilities, Clothing). |
| **Budget** | The sum of expected expenses for a given quarter, calculated by the system. |
| **Planned Expense** | An expense entered during monthly planning with an expected value and due date. |
| **Actual Expense** | A recorded payment with the real amount and payment date. |
| **Income Source** | A recurring source of income (e.g., salary, freelance, rental) with a defined amount, frequency, and payment schedule. |
| **Fixed Date** | A specific day of the month for a payment (e.g., the 25th). |
| **Relative Date** | A rule-based payment date (e.g., "last Thursday of the month", "every Monday"). |
