---
framework: React + Mantine + TanStack Query
component: src/components/AddExpenseModal.tsx
---

# Add Expense

::: MODAL :::

### Add Expense

[v] Select Payee {dynamic: payees}
[v] Select Category {dynamic: payee.categories}

> hint: Category options filtered by selected payee

[ text: Title ]
[ text: Description (optional) ]
[ text: Expected value ]
[ text: Due date (YYYY-MM-DD) ]

***

=== ROW ===
> align: right
[ Cancel ](#cancel) [ Save ](#save)
--- END ---

--- END ---
