# Requirements: Budgets & Bank Accounts

## 1. Functional Requirements

### FR-23: Category Budgets

**Acceptance Criteria:**

1. The system shall allow the user to set a budget amount per category per quarter.
2. The system shall display the budget as informational (Planned, Spent, Remaining) without enforcing limits.
3. When an expense is added to a category that has no budget for that quarter, the system shall auto-create a budget with value €0 for that category/quarter.
4. The system shall allow the user to modify any category budget at any time.
5. When a budget is modified, the system shall affect only that specific quarter (independent of other quarters).
6. For each category in the expense view, the system shall display:
   - **Planned:** The budget value set for that category/quarter.
   - **Spent:** The sum of `actualValue` from paid expenses in that category/quarter.
   - **Remaining:** Planned minus Spent (negative if overspent, displayed in red).

**Rationale:** So that I can track where I overspent, I want to set target budgets per category and compare against actual spending.

**Priority:** Must

---

### FR-24: Bank Account Management

**Acceptance Criteria:**

1. The system shall allow the user to create a bank account with a name, description, and logo.
2. The system shall store the logo as a binary image in the database (bytea, max 500KB).
3. The system shall allow the user to list all bank accounts with their logos.
4. The system shall allow the user to update a bank account (name, description, logo).
5. The system shall allow the user to delete a bank account only if no expenses reference it.
6. If the user attempts to delete a bank account with associated expenses, then the system shall display an error message.

**Rationale:** So that I can assign expenses to funding sources, I want to manage my bank accounts.

**Priority:** Must

---

### FR-25: Expense Fund Source

**Acceptance Criteria:**

1. When creating an expense, the system shall require the user to select a bank account as the funding source.
2. The system shall display a simple dropdown of bank accounts (1–5 expected).
3. The system shall display a "By Account" summary showing the total of unpaid expenses per bank account.
4. The summary shall calculate: for each bank account, sum of `expectedValue` of all expenses with status PENDING or OVERDUE assigned to that account.
5. The system shall display this summary in the month sidebar panel.

**Rationale:** So that I can track how much money I need in each account for my planned expenses, I want to assign a bank account to each expense.

**Priority:** Must
