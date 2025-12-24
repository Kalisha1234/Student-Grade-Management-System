# Git Workflow

## Branch Structure

- **main** - Production-ready code
- **develop** - Integration branch for features
- **feature/** - Feature branches

## Workflow

1. **Create Feature Branch**
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/feature-name
   ```

2. **Work on Feature**
   ```bash
   git add .
   git commit -m "feat: description"
   git push origin feature/feature-name
   ```

3. **Create Pull Request**
   - Open PR from `feature/feature-name` → `develop`
   - Review and merge

4. **Merge to Main**
   ```bash
   git checkout main
   git pull origin main
   git merge develop
   git push origin main
   ```

5. **Delete Feature Branch**
   ```bash
   git branch -d feature/feature-name
   git push origin --delete feature/feature-name
   ```

## Commit Convention

- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation
- `test:` - Tests
- `refactor:` - Code refactoring

## Feature Branches

- feature/advanced-collections
- feature/audit-trail
- feature/caching-system
- feature/concurrent-reports
- feature/nio2-file-operations
- feature/pattern-search
- feature/realtime-dashboard
- feature/regex-validation
- feature/scheduled-tasks
- feature/stream-processing
