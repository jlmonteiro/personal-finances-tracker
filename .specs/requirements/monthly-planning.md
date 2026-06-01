# Requirements: Monthly Planning & Expenses

## 1. User Journeys

### UJ-2: Monthly Planning

1. User navigates to "Plan Month" and selects or creates a new financial month.
2. **The system SHALL** create the month and divide it into 4 quarters based on the configured start day.
3. User adds expected expenses with: payee, category (from payee's categories), title, description, expected value, and due date.
4. **The system SHALL** assign each expense to the appropriate quarter based on its due date.
5. **The system SHALL** calculate and display the budget for each quarter as the sum of expected expenses in that quarter.
6. User reviews the quarter budgets and total month budget vs income.

---

### UJ-3: Expense Tracking

1. User navigates to the current month view.
2. User selects a planned expense and records the actual payment.
3. **The system SHALL** allow the user to enter the actual value and payment date.
4. **The system SHALL** update the quarter and month summaries to reflect actual spending.
5. User can also add unplanned expenses directly (not previously in the plan).

---

## 2. Functional Requirements

### FR-8: Month Creation

**Acceptance Criteria:**

1. When the user creates a new month, the system shall calculate the month boundaries using the configured start day.
2. The system shall prevent creating duplicate months for the same period.
3. The system shall display the month with its start and end dates.

**Rationale:** So that I can plan my finances for a specific period, I want to create financial months.

**Priority:** Must

---

### FR-9: Quarter Division

**Acceptance Criteria:**

1. When a month is created, the system shall divide it into 4 roughly equal periods.
2. The system shall calculate quarter boundaries by dividing the total days in the month by 4.
3. The system shall display each quarter with its date range.

**Rationale:** So that I can plan and track spending in smaller chunks, I want my month split into 4 parts.

**Priority:** Must

---

### FR-10: Expense Planning

**Acceptance Criteria:**

1. The system shall allow the user to add a planned expense with: payee, category, title, description, expected value, and due date.
2. The system shall support two expense types: **single** (one-off) and **recurring** (repeats across months).
3. For recurring expenses, the system shall allow the user to define a frequency (monthly, weekly, fortnightly, four-weekly), a start date, and an optional end date.
4. The system shall only generate planned entries for recurring expenses within their active period (between start and end date).
5. If no end date is provided, the system shall continue generating entries indefinitely until the user deactivates or sets an end date.
6. When a due date is provided, the system shall assign the expense to the corresponding quarter.
5. The system shall validate that the due date falls within the month boundaries.
6. The system shall validate that the selected category belongs to the selected payee.

**Rationale:** So that I can anticipate my spending and avoid re-entering recurring bills each month, I want to plan expenses with optional recurrence.

**Priority:** Must

---

### FR-18: Expense Status

**Acceptance Criteria:**

1. The system shall derive the expense status automatically based on the following rules:
   - **Pending:** due date is in the future and no payment date is recorded.
   - **Paid:** a payment date and actual value are recorded.
   - **Overdue:** due date is in the past and no payment date is recorded.
2. The system shall display the status visually on each expense in the monthly view.
3. The system shall allow filtering expenses by status within a month or quarter.

**Rationale:** So that I can quickly see what's been paid and what needs attention, I want the system to derive status from dates and values.

**Priority:** Must

---

### FR-11: Budget Calculation

**Acceptance Criteria:**

1. The system shall calculate each quarter's budget as the sum of expected values of all planned expenses in that quarter.
2. When a planned expense is added, updated, or removed, the system shall recalculate the affected quarter's budget.
3. The system shall display the total month budget as the sum of all quarter budgets.

**Rationale:** So that I can see how much I expect to spend per quarter, I want the system to calculate budgets automatically.

**Priority:** Must

---

### FR-12: Expense Recording

**Acceptance Criteria:**

1. The system shall allow the user to record an actual payment for a planned expense by entering the actual value and payment date.
2. The system shall allow the user to add unplanned expenses with: payee, category, title, description, actual value, and payment date.
3. When an actual payment is recorded, the system shall update the quarter summary to reflect actual spending.

**Rationale:** So that I can track what I actually spend, I want to record payments as they happen.

**Priority:** Must

---

### FR-13: Expense Category Selection

**Acceptance Criteria:**

1. When creating or editing an expense, the system shall require exactly one category.
2. The system shall present only the categories associated with the selected payee.
3. If the payee has only one category, the system shall auto-select it.

**Rationale:** So that expenses are correctly categorized for reporting, I want to select the appropriate category per expense.

**Priority:** Must

---

### FR-22: Export Month to Markdown

**Acceptance Criteria:**

1. The system shall provide an export button in the monthly view (both calendar and table views).
2. When the user triggers the export, the system shall generate a Markdown document containing: month summary (income, budget, actual, balance), quarter breakdowns with all expenses (payee, category, title, expected value, actual value, due date, payment date, status), and category totals.
3. The system shall copy the generated Markdown to the clipboard.
4. The Markdown output shall be structured and detailed enough to be pasted into an LLM for financial analysis.

**Rationale:** So that I can use an LLM to analyse my monthly finances, I want to export the full month breakdown as Markdown.

**Priority:** Must
