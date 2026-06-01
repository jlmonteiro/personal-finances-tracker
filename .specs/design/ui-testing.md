# UI Testing Guidelines

## Principle: Test Behavior, Not Implementation

Tests should verify what the user sees and does — never how the UI is built internally. This prevents brittle tests that break on every refactor.

```typescript
// ❌ Brittle — breaks when you change component structure
expect(wrapper.find('.btn-primary').at(2).text()).toBe('Save')

// ✅ Stable — tests user-visible behavior
expect(screen.getByRole('button', { name: 'Save' })).toBeInTheDocument()
```

---

## Testing Trophy

```
        ╭───────╮
        │  E2E  │        Few — critical user journeys only
        ╰───┬───╯
      ╭─────┴─────╮
      │Integration │      Most effort here
      ╰─────┬─────╯
    ╭───────┴───────╮
    │  Unit (logic) │     Pure functions, utils, hooks
    ╰───────┬───────╯
  ╭─────────┴─────────╮
  │   Static (types)   │  TypeScript catches this
  ╰────────────────────╯
```

---

## Test Layers

| Layer | What to test | Tool | Brittleness |
|-------|-------------|------|-------------|
| **Static** | Type errors, lint | TypeScript + ESLint | Zero |
| **Unit** | Pure logic (utils, calculations, custom hooks) | Vitest | Low |
| **Integration** | Full page/component renders, user interactions, API calls | Vitest + Testing Library + MSW | Low |
| **E2E** | Critical user journeys (setup wizard, plan month, record payment) | Playwright | Medium |

---

## Tools

| Tool | Purpose |
|------|---------|
| **Vitest** | Test runner (fast, Vite-native) |
| **@testing-library/react** | DOM queries by accessibility roles/labels |
| **@testing-library/user-event** | Simulate real user interactions |
| **MSW (Mock Service Worker)** | Mock API responses at network level |
| **Playwright + Cucumber** | E2E with Gherkin `.feature` files (unified BDD language with backend) |

---

## Rules

### Do

- Query by **role**, **label**, **text**, or **placeholder** — what the user sees
- Mock the **network** (MSW), not components
- Test **outcomes** (toast appeared, list updated, navigation happened)
- Test **error states** (API failure, validation errors)
- Use `findBy` (async) for elements that appear after API calls
- Write integration tests that render full pages with real child components
- Keep E2E tests to 3-5 critical user journeys

### Don't

- Don't query by CSS class, id, or `data-testid` (unless no accessible alternative exists)
- Don't test Mantine internals (that a Button renders, that a Modal animates)
- Don't use snapshot tests — they break on every change and nobody reviews diffs
- Don't shallow render — it tests implementation, not behavior
- Don't mock child components — render the real tree
- Don't test styling or layout (that's visual regression territory, not unit tests)

---

## Query Priority (Testing Library)

Use queries in this order of preference:

1. `getByRole` — buttons, links, headings, textboxes (best: accessible to everyone)
2. `getByLabelText` — form inputs (best for forms)
3. `getByPlaceholderText` — when no label exists
4. `getByText` — non-interactive elements
5. `getByDisplayValue` — filled form inputs
6. `getByTestId` — **last resort** only when no semantic query works

---

## MSW (Mock Service Worker)

Mock at the network level so components use real fetch/axios code:

```typescript
import { http, HttpResponse } from 'msw'
import { setupServer } from 'msw/node'

const server = setupServer(
  http.get('/api/v1/categories', () => {
    return HttpResponse.json({
      data: [
        { id: '1', name: 'Groceries', icon: 'shopping-cart' },
        { id: '2', name: 'Utilities', icon: 'bolt' },
      ],
      pagination: { page: 1, size: 20, totalElements: 2, totalPages: 1 }
    })
  })
)

beforeAll(() => server.listen())
afterEach(() => server.resetHandlers())
afterAll(() => server.close())
```

---

## Integration Test Example

```typescript
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { AddExpenseModal } from './AddExpenseModal'

test('creates expense when form is submitted', async () => {
  server.use(
    http.post('/api/v1/financial-months/:id/expenses', () => {
      return HttpResponse.json({ id: '...', title: 'Weekly shop', status: 'PENDING' })
    })
  )

  render(<AddExpenseModal monthId="123" />)

  await userEvent.selectOptions(screen.getByLabelText('Payee'), 'Dunnes Stores')
  await userEvent.selectOptions(screen.getByLabelText('Category'), 'Groceries')
  await userEvent.type(screen.getByLabelText('Title'), 'Weekly shop')
  await userEvent.type(screen.getByLabelText('Expected value'), '120.00')
  await userEvent.type(screen.getByLabelText('Due date'), '2026-06-07')
  await userEvent.click(screen.getByRole('button', { name: 'Save' }))

  expect(await screen.findByText('Expense created')).toBeInTheDocument()
})
```

---

## E2E Tests (Playwright + Cucumber)

Gherkin `.feature` files with Playwright step definitions — same BDD language as backend Cucumber tests.

### Feature File

```gherkin
# e2e/features/setup.feature
Feature: Setup Wizard

  Scenario: Complete first-time setup
    Given the application has no configuration
    When I open the application
    Then I should be redirected to the setup wizard

    When I select currency "EUR"
    And I click "Next"
    And I select month start day "1"
    And I click "Next"
    And I fill in income source "Salary" with amount "3500"
    And I click "Finish Setup"
    Then I should see the dashboard
```

### Step Definitions

```typescript
// e2e/steps/setup.steps.ts
import { Given, When, Then } from '@cucumber/cucumber'
import { expect } from '@playwright/test'

Given('the application has no configuration', async function () {
  // API or DB setup to ensure clean state
})

When('I open the application', async function () {
  await this.page.goto('/')
})

Then('I should be redirected to the setup wizard', async function () {
  await expect(this.page).toHaveURL('/setup')
})

When('I select currency {string}', async function (currency: string) {
  await this.page.getByLabel('Currency').selectOption(currency)
})

When('I click {string}', async function (buttonName: string) {
  await this.page.getByRole('button', { name: buttonName }).click()
})

Then('I should see the dashboard', async function () {
  await expect(this.page).toHaveURL('/')
  await expect(this.page.getByText('Overview')).toBeVisible()
})
```

---

## File Structure

```
frontend/src/
├── __tests__/              # Integration tests (per page)
│   ├── Dashboard.test.tsx
│   ├── Categories.test.tsx
│   ├── Payees.test.tsx
│   └── MonthTable.test.tsx
├── utils/__tests__/        # Unit tests (pure logic)
│   ├── currency.test.ts
│   └── dateUtils.test.ts
└── mocks/
    ├── handlers.ts         # MSW default handlers
    └── server.ts           # MSW server setup

e2e/                        # Playwright + Cucumber E2E tests
├── features/               # Gherkin .feature files
│   ├── setup.feature
│   ├── monthly-planning.feature
│   └── expense-tracking.feature
├── steps/                  # Kotlin-style step definitions (TypeScript)
│   ├── setup.steps.ts
│   ├── planning.steps.ts
│   └── common.steps.ts
├── support/
│   └── world.ts            # Playwright page context for Cucumber
└── cucumber.js             # Cucumber config
```
