# Contributing to Shopee FMS

Thank you for considering a contribution! Please follow these guidelines.

## Branch naming

| Type     | Pattern                  | Example                       |
|----------|--------------------------|-------------------------------|
| Feature  | `feat/<short-desc>`      | `feat/export-sales-pdf`       |
| Bug fix  | `fix/<short-desc>`       | `fix/order-total-rounding`    |
| Chore    | `chore/<short-desc>`     | `chore/upgrade-spring-boot`   |
| Docs     | `docs/<short-desc>`      | `docs/api-endpoints`          |

## Commit messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(optional scope): <short summary>
```

Types: `feat`, `fix`, `docs`, `chore`, `refactor`, `test`, `perf`, `style`.

## Pull requests

1. Fork the repo and create a branch from `main`.
2. Make your changes with clear, focused commits.
3. Ensure the backend builds (`./mvnw verify`) and the frontend lints (`npm run lint`).
4. Open a PR against `main` with a description of what you changed and why.

## Code style

- **Backend** — follow standard Java conventions; use constructor injection over field injection.
- **Frontend** — ESLint is configured; run `npm run lint` before committing.

## Reporting bugs

Open a GitHub Issue with steps to reproduce, expected behavior, and actual behavior.
