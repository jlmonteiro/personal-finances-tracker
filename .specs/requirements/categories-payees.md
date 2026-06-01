# Requirements: Categories & Payees

## 1. Functional Requirements

### FR-5: Category Management

**Acceptance Criteria:**

1. The system shall allow the user to create a category with a name and an icon.
2. The system shall provide a searchable icon picker using the Tabler Icons library for icon selection.
3. The system shall display a link to the Tabler Icons page (https://tabler.io/icons) below the icon input field so the user can browse available icons.
4. The system shall store the icon as a string identifier (e.g., `"shopping-cart"`, `"home"`).
4. The system shall allow the user to list all categories with their icons.
5. The system shall allow the user to update a category name and icon.
6. The system shall allow the user to delete a category only if no expenses reference it.
7. If the user attempts to delete a category with associated expenses, then the system shall display an error message.

**Rationale:** So that I can classify my expenses with visual distinction for reporting and dashboards, I want to manage expense categories with icons.

**Priority:** Must

---

### FR-6: Payee Management

**Acceptance Criteria:**

1. The system shall allow the user to create a payee with a name.
2. The system shall allow the user to list all payees.
3. The system shall allow the user to update a payee name.
4. The system shall allow the user to delete a payee only if no expenses reference it.
5. If the user attempts to delete a payee with associated expenses, then the system shall display an error message.

**Rationale:** So that I can track who I pay, I want to manage payees.

**Priority:** Must

---

### FR-7: Payee-Category Association

**Acceptance Criteria:**

1. The system shall allow a payee to be associated with one or more categories.
2. When creating or editing a payee, the system shall present available categories for selection.
3. The system shall require at least one category per payee.
4. When creating an expense for a payee, the system shall present only the categories associated with that payee for selection.

**Rationale:** So that I can quickly categorize expenses based on the payee, I want payees linked to their relevant categories (e.g., Dunnes Stores → Groceries, Clothing).

**Priority:** Must
