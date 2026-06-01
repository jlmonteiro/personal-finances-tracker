# Requirements: Setup

## 1. User Journeys

### UJ-1: First-Time Setup

1. User launches the application for the first time.
2. **The system SHALL** detect that no configuration exists and redirect to the setup wizard.
3. User is presented with a step-by-step wizard.
4. User selects their preferred currency.
5. User configures the day of the month their financial month starts (default: 1).
6. User adds one or more income sources with: name, amount, frequency, and payment date (fixed or relative).
7. **The system SHALL** save the configuration and redirect to the home dashboard.
8. User sees the dashboard (empty state with guidance to plan their first month).

---

## 2. Functional Requirements

### FR-1: Setup Wizard

**Acceptance Criteria:**

1. When the application is launched and no configuration exists, the system shall display the setup wizard.
2. The system shall guide the user through configuration steps sequentially.
3. When the user completes all steps, the system shall persist the configuration and redirect to the dashboard.
4. If the user navigates away mid-wizard, then the system shall resume from the last incomplete step on next visit.

**Rationale:** So that the application is usable from the first launch, I want a guided setup experience.

**Priority:** Must

---

### FR-2: Currency Configuration

**Acceptance Criteria:**

1. The system shall present a list of common currencies (EUR, GBP, USD, etc.) for selection.
2. When the user selects a currency, the system shall store it as the application-wide currency.
3. The system shall display all monetary values using the selected currency symbol/format.

**Rationale:** So that monetary values are displayed correctly, I want to configure my currency once.

**Priority:** Must

---

### FR-3: Month Start Day

**Acceptance Criteria:**

1. The system shall allow the user to select a start day between 1 and 28.
2. The system shall default to day 1 if no selection is made.
3. When a start day is configured, the system shall use it to calculate month boundaries and quarter divisions.

**Rationale:** So that the financial month aligns with my pay cycle, I want to configure when my month starts.

**Priority:** Must

---

### FR-4: Income Configuration

**Acceptance Criteria:**

1. The system shall allow the user to add one or more income sources, each with: name, amount, payment frequency, and payment date.
2. The system shall support the following payment frequencies: monthly, weekly, fortnightly, four-weekly.
3. The system shall support the following payment date types:
   - **Fixed day:** a specific day of the month (e.g., 25th).
   - **Relative day:** a rule-based date (e.g., "every Monday", "last Thursday of the month", "first Friday of the month").
4. When income sources are configured, the system shall calculate total expected income per month and per quarter based on frequencies and payment dates.
5. The system shall allow adding, editing, and removing income sources from a settings page after initial setup.

**Rationale:** So that I can accurately see my total income against expenses, I want to configure all income sources with their schedules.

**Priority:** Must

---

### FR-17: Income Payment Date Adjustment

**Acceptance Criteria:**

1. In the monthly view, the system shall display the calculated payment dates for each income source.
2. The system shall allow the user to adjust a specific payment date for a given month (e.g., if it lands on a bank holiday).
3. When a payment date is adjusted, the system shall recalculate which quarter the income falls into.
4. Adjustments shall apply only to the specific month and not alter the recurring schedule.

**Rationale:** So that my monthly view reflects reality when payment dates shift due to bank holidays, I want to adjust individual payment dates.

**Priority:** Must
