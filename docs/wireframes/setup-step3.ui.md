---
framework: React + Mantine + TanStack Query
component: src/pages/Setup.tsx
---

# Setup Wizard - Income Sources

::: CARD :::
> align: center, max-width: 700px

## Personal Finances Tracker

| Step 1: Currency | Step 2: Month Start |[ Step 3: Income ]|

***

### Add your income sources

| Name | Amount | Frequency | Payment Date | Actions |
| ---- | ------ | --------- | ------------ | ------- |
| Salary | €3,500.00 | Monthly | Fixed: 25th | [ Edit ](#edit) [ Delete ](#delete) |

[ + Add Income Source ](#add-income)

***

### Add Income Source

[ text: Name ]
[ text: Amount ]
[v] Monthly {Monthly, Weekly, Fortnightly, Four-Weekly}
[v] Fixed {Fixed, Relative}
[ text: Date rule (e.g. 25, LAST_THURSDAY) ]
[ text: Start date (YYYY-MM-DD) ]
[ text: End date (optional) ]

> hint: Leave end date empty for indefinite

***

=== ROW ===
> space: between
[ Back ](#back) [ Finish Setup ](#finish)
--- END ---

--- END ---
