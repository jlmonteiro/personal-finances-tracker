# Requirements: Dashboard & Monthly Views

## 1. User Journeys

### UJ-4: Dashboard Review

1. User opens the application (after setup is complete).
2. **The system SHALL** display the dashboard as the home page.
3. User sees an overview of the current month: income, total budget, total spent, remaining, net balance.
4. User sees a breakdown per quarter: budget vs actual (bar chart).
5. User sees a category breakdown of expenses (pie/donut chart).
6. User sees budget usage progress bars per quarter.
7. User can switch to the calendar view or table view for detailed monthly exploration.
8. User can navigate to previous/next months using arrow buttons.

---

## 2. Functional Requirements

### FR-14: Dashboard Home

**Acceptance Criteria:**

1. When the user opens the application after setup, the system shall display the dashboard.
2. The system shall show the current month's summary: total income, total planned budget, total actual spending, remaining balance, and net balance (income minus actual spending).
3. The system shall display a bar chart showing planned budget vs actual spending per quarter.
4. The system shall display a pie/donut chart showing expenses grouped by category.
5. The system shall display progress bars showing budget usage per quarter (actual/planned as percentage).
6. If no month is planned yet, the system shall display an empty state with a prompt to plan the first month.

**Rationale:** So that I get an immediate visual overview of my finances, I want the dashboard as my landing page with charts.

**Priority:** Must

---

### FR-15: Budget vs Actual

**Acceptance Criteria:**

1. The system shall display each quarter's planned budget alongside actual spending in a bar chart.
2. The system shall visually indicate when actual spending exceeds the planned budget for a quarter.
3. The system shall show the variance (difference) between planned and actual for each quarter.
4. The system shall show income vs total expenses as a net balance indicator.

**Rationale:** So that I can see where I'm over or under budget, I want a quarter-by-quarter comparison.

**Priority:** Must

---

### FR-16: Category Breakdown

**Acceptance Criteria:**

1. The system shall display expenses grouped by category for the current month in a pie/donut chart.
2. The system shall show the total amount per category (both planned and actual).
3. The system shall display category icons (Tabler) alongside category names.
4. The system shall allow the user to identify which categories consume the most budget.

**Rationale:** So that I can understand my spending patterns, I want to see expenses by category.

**Priority:** Should

---

### FR-19: Calendar View

**Acceptance Criteria:**

1. The system shall display a calendar view of the current month.
2. The system shall highlight each quarter in a different colour tone for visual distinction.
3. The system shall display income entries on their payment dates.
4. The system shall display expenses on their due dates with a status icon: green for paid, red for overdue/pending.
5. The system shall display the value next to each expense entry.
6. The system shall provide arrow buttons to navigate to previous and next months.
7. When navigating months, the system shall load the selected month's data.

**Rationale:** So that I can see my financial month at a glance with timing context, I want a calendar layout.

**Priority:** Must

---

### FR-20: Table View

**Acceptance Criteria:**

1. The system shall display a table view grouped by quarter, showing full details of each expense: payee, category, title, description, expected value, actual value, due date, payment date, status.
2. The system shall visually separate each quarter section.
3. The system shall show quarter subtotals (planned and actual).
4. The system shall provide arrow buttons to navigate to previous and next months.
5. The system shall display income entries within the relevant quarter.

**Rationale:** So that I can see all expense details in a structured format, I want a table view per quarter.

**Priority:** Must

---

### FR-21: Recurring Expense Monthly Override

**Acceptance Criteria:**

1. The system shall allow the user to modify a recurring expense's values (expected value, due date, description) for a specific month without altering the recurring definition.
2. When a recurring expense is modified for a specific month, the system shall store the override for that month only.
3. The system shall visually indicate when a recurring expense has been overridden for the current month.
4. Future months shall continue to use the original recurring definition unless also overridden.

**Rationale:** So that I can adjust recurring expenses for exceptional months (e.g., saving more one month), I want per-month overrides.

**Priority:** Must
