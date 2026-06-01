# Test Scenarios

Test scenarios validating requirements. All tests follow BDD structure (Given/When/Then).

---

## 1. Setup

### TS-1: First-Time Setup Redirect

- **Given**: The application has no configuration stored.
- **When**: The user opens the application.
- **Then**: The system redirects to the setup wizard.

**Validates:** [FR-1: Setup Wizard](../requirements/setup.md#fr-1)

---

### TS-2: Complete Setup Wizard

- **Given**: The user is on the setup wizard.
- **When**: The user selects currency "EUR", month start day "1", and adds an income source (Salary, €3500, monthly, fixed day 25).
- **Then**: The system persists the configuration and redirects to the dashboard.

**Validates:** [FR-1](../requirements/setup.md#fr-1), [FR-2](../requirements/setup.md#fr-2), [FR-3](../requirements/setup.md#fr-3), [FR-4](../requirements/setup.md#fr-4)

---

### TS-3: Resume Incomplete Setup

- **Given**: The user completed step 1 (currency) but navigated away.
- **When**: The user returns to the application.
- **Then**: The system resumes the wizard at step 2.

**Validates:** [FR-1: Setup Wizard](../requirements/setup.md#fr-1)

---

### TS-4: Multiple Income Sources

- **Given**: The user is on step 3 of the setup wizard.
- **When**: The user adds two income sources: Salary (monthly, fixed 25th) and Freelance (monthly, relative last Friday).
- **Then**: The system stores both income sources with their respective frequencies and payment rules.

**Validates:** [FR-4: Income Configuration](../requirements/setup.md#fr-4)

---

## 2. Categories & Payees

### TS-5: Create Category

- **Given**: The user is on the categories page.
- **When**: The user creates a category with name "Groceries" and icon "shopping-cart".
- **Then**: The system stores the category and displays it in the list with the icon.

**Validates:** [FR-5: Category Management](../requirements/categories-payees.md#fr-5)

---

### TS-6: Delete Category With References

- **Given**: A category "Groceries" exists and is referenced by 3 expenses.
- **When**: The user attempts to delete the category.
- **Then**: The system displays an error: "Cannot delete category 'Groceries' because it is referenced by 3 expenses."

**Validates:** [FR-5: Category Management](../requirements/categories-payees.md#fr-5)

---

### TS-7: Create Payee With Categories

- **Given**: Categories "Groceries" and "Clothing" exist.
- **When**: The user creates a payee "Dunnes Stores" with both categories selected.
- **Then**: The system stores the payee with both category associations.

**Validates:** [FR-6](../requirements/categories-payees.md#fr-6), [FR-7](../requirements/categories-payees.md#fr-7)

---

### TS-8: Payee Requires At Least One Category

- **Given**: The user is creating a payee.
- **When**: The user submits without selecting any category.
- **Then**: The system displays a validation error: "At least one category is required."

**Validates:** [FR-7: Payee-Category Association](../requirements/categories-payees.md#fr-7)

---

## 3. Monthly Planning

### TS-9: Create Financial Month

- **Given**: Configuration exists with month start day = 1.
- **When**: The user creates a financial month for June 2026.
- **Then**: The system creates the month (Jun 1 - Jun 30) with 4 quarters and generates income entries from active income sources.

**Validates:** [FR-8](../requirements/monthly-planning.md#fr-8), [FR-9](../requirements/monthly-planning.md#fr-9)

---

### TS-10: Prevent Duplicate Month

- **Given**: A financial month for June 2026 already exists.
- **When**: The user attempts to create another month for June 2026.
- **Then**: The system returns a 409 conflict error.

**Validates:** [FR-8: Month Creation](../requirements/monthly-planning.md#fr-8)

---

### TS-11: Quarter Division

- **Given**: A financial month is created for June 2026 (30 days, start day 1).
- **When**: The system divides the month.
- **Then**: Quarters are: Q1 (Jun 1-7), Q2 (Jun 8-15), Q3 (Jun 16-23), Q4 (Jun 24-30).

**Validates:** [FR-9: Quarter Division](../requirements/monthly-planning.md#fr-9)

---

### TS-12: Add Planned Expense

- **Given**: A financial month for June 2026 exists with payee "Dunnes Stores" (categories: Groceries, Clothing).
- **When**: The user adds an expense: payee "Dunnes Stores", category "Groceries", title "Weekly shop", expected value €120, due date Jun 5.
- **Then**: The system assigns the expense to Q1 and recalculates Q1 budget.

**Validates:** [FR-10](../requirements/monthly-planning.md#fr-10), [FR-11](../requirements/monthly-planning.md#fr-11), [FR-13](../requirements/monthly-planning.md#fr-13)

---

### TS-13: Category Must Belong To Payee

- **Given**: Payee "Dunnes Stores" has categories "Groceries" and "Clothing". Category "Utilities" exists but is not associated.
- **When**: The user creates an expense for "Dunnes Stores" with category "Utilities".
- **Then**: The system rejects with a 422 error: "Category 'Utilities' is not associated with payee 'Dunnes Stores'."

**Validates:** [FR-13: Expense Category Selection](../requirements/monthly-planning.md#fr-13)

---

### TS-14: Budget Calculation

- **Given**: Q1 has two planned expenses: €120 (groceries) and €85 (electricity).
- **When**: The system calculates the Q1 budget.
- **Then**: Q1 budget = €205.

**Validates:** [FR-11: Budget Calculation](../requirements/monthly-planning.md#fr-11)

---

### TS-15: Record Payment

- **Given**: A planned expense exists (expected €120, due Jun 5, status Pending).
- **When**: The user records payment: actual value €115.50, payment date Jun 5.
- **Then**: The expense status becomes "Paid" and the quarter actual total is updated.

**Validates:** [FR-12: Expense Recording](../requirements/monthly-planning.md#fr-12)

---

### TS-16: Expense Status Derivation - Overdue

- **Given**: An expense has due date Jun 1 and no payment recorded.
- **When**: The current date is Jun 2.
- **Then**: The expense status is "Overdue".

**Validates:** [FR-18: Expense Status](../requirements/monthly-planning.md#fr-18)

---

### TS-17: Recurring Expense Generation

- **Given**: A recurring expense "Rent" exists (monthly, start Jan 2026, no end date, expected €1200).
- **When**: The user creates a financial month for June 2026.
- **Then**: The system generates a planned expense for "Rent" in June with expected value €1200.

**Validates:** [FR-10: Expense Planning](../requirements/monthly-planning.md#fr-10)

---

### TS-18: Recurring Expense With End Date

- **Given**: A recurring expense exists with start date Jan 2026 and end date May 2026.
- **When**: The user creates a financial month for June 2026.
- **Then**: The system does NOT generate an entry for this recurring expense.

**Validates:** [FR-10: Expense Planning](../requirements/monthly-planning.md#fr-10)

---

### TS-19: Override Recurring Expense

- **Given**: A recurring expense "Savings" exists (monthly, expected €400). A June 2026 month exists with the generated €400 entry.
- **When**: The user updates the June entry to expected value €500.
- **Then**: The expense is marked as `isOverride = true`, Q budget recalculates to reflect €500, and future months remain at €400.

**Validates:** [FR-21: Recurring Expense Monthly Override](../requirements/dashboard.md#fr-21)

---

## 4. Income

### TS-20: Income Entry Generation

- **Given**: An income source "Salary" exists (monthly, fixed day 25, €3500).
- **When**: The user creates a financial month for June 2026.
- **Then**: The system generates an income entry for Jun 25, assigned to Q4.

**Validates:** [FR-4](../requirements/setup.md#fr-4), [FR-17](../requirements/setup.md#fr-17)

---

### TS-21: Adjust Income Payment Date

- **Given**: An income entry exists for Jun 25 (Q4).
- **When**: The user adjusts the payment date to Jun 27 (bank holiday).
- **Then**: The entry is updated with `isDateAdjusted = true`, remains in Q4, and the recurring schedule is unchanged.

**Validates:** [FR-17: Income Payment Date Adjustment](../requirements/setup.md#fr-17)

---

## 5. Dashboard & Views

### TS-22: Dashboard Summary

- **Given**: June 2026 has total income €4200, total budget €3100, total actual €1850.
- **When**: The user opens the dashboard.
- **Then**: The system displays: income €4200, budget €3100, spent €1850, remaining €1250, net balance €2350.

**Validates:** [FR-14: Dashboard Home](../requirements/dashboard.md#fr-14)

---

### TS-23: Dashboard Empty State

- **Given**: No financial months exist.
- **When**: The user opens the dashboard.
- **Then**: The system displays an empty state with a prompt to plan the first month.

**Validates:** [FR-14: Dashboard Home](../requirements/dashboard.md#fr-14)

---

### TS-24: Calendar View Quarter Highlighting

- **Given**: June 2026 exists with 4 quarters.
- **When**: The user opens the calendar view.
- **Then**: Each quarter's date range is highlighted in a different colour tone.

**Validates:** [FR-19: Calendar View](../requirements/dashboard.md#fr-19)

---

### TS-25: Month Navigation

- **Given**: Financial months for May, June, and July 2026 exist. The user is viewing June.
- **When**: The user clicks the next arrow.
- **Then**: The view loads July 2026 data.

**Validates:** [FR-19](../requirements/dashboard.md#fr-19), [FR-20](../requirements/dashboard.md#fr-20)

---

### TS-26: Export Month to Markdown

- **Given**: June 2026 has income, planned expenses, and actual payments.
- **When**: The user clicks "Export Markdown".
- **Then**: The system generates a Markdown document with month summary, quarter breakdowns, all expenses, and category totals, and copies it to the clipboard.

**Validates:** [FR-22: Export Month to Markdown](../requirements/monthly-planning.md#fr-22)

---

## 6. Authentication

### TS-27: Dev Profile Bypasses Auth

- **Given**: The backend is running with `spring.profiles.active=dev`.
- **When**: A request is made without a Bearer token.
- **Then**: The request succeeds with a dev user identity injected.

**Validates:** [ADR-4: OAuth2 with Dev Fake Identity](adrs.md#adr-4)

---

### TS-28: Production Rejects Invalid Token

- **Given**: The backend is running in production mode.
- **When**: A request is made with an invalid or expired JWT.
- **Then**: The system returns 401 Unauthorized.

**Validates:** [ADR-4: OAuth2 with Dev Fake Identity](adrs.md#adr-4)

---

## Summary & Environment

- **Test Framework:** JUnit 5 + Cucumber (BDD, `.feature` files with Kotlin step definitions)
- **Frontend Tests:** Vitest + Testing Library
- **Database:** Testcontainers (PostgreSQL 17)
- **Mocks:** Only for external OAuth provider in integration tests
- **Reports:** Cucumber HTML/JSON reports with Gherkin steps
- **Verification:** All scenarios must pass; coverage target for service layer > 80%

### Cucumber Structure

```
backend/src/test/
├── resources/features/       # .feature files (Gherkin)
│   ├── setup.feature
│   ├── categories.feature
│   ├── payees.feature
│   ├── monthly-planning.feature
│   ├── income.feature
│   ├── dashboard.feature
│   └── auth.feature
└── kotlin/.../steps/         # Kotlin step definitions
    ├── SetupSteps.kt
    ├── CategorySteps.kt
    ├── PayeeSteps.kt
    ├── ExpenseSteps.kt
    ├── IncomeSteps.kt
    ├── DashboardSteps.kt
    └── AuthSteps.kt
```
