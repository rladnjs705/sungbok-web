# Git Commit & Push Skill

> Automated Git commit and push workflow with conventional commits format

## Description

Simplifies the Git workflow by automating status check, staging, committing, and pushing changes.
Follows conventional commits format and automatically adds Co-Authored-By for Claude.

## Triggers

- `/commit [message]` - Commit with optional message
- `/push` - Commit and push in one command
- `/save [message]` - Same as commit (alias)

Korean triggers:
- `/커밋 [메시지]`
- `/푸시`
- `/저장 [메시지]`

## Arguments

| Argument | Required | Description | Example |
|----------|----------|-------------|---------|
| `message` | No | Commit message (auto-generated if omitted) | `"fix: 로고 클릭 버그 수정"` |
| `--no-push` | No | Commit only, skip push | `/commit "feat: 새 기능" --no-push` |
| `--amend` | No | Amend previous commit | `/commit --amend` |
| `--files` | No | Commit specific files only | `/commit --files src/app/*.tsx` |

## Workflow

### Step 1: Check Git Status

```bash
git status --short
```

If no changes, notify user and exit.

### Step 2: Show Changes

Display changed files to user:
- Modified files (M)
- New files (A)
- Deleted files (D)
- Untracked files (??)

### Step 3: Confirm with User

Use `AskUserQuestion` to confirm:
- "Commit these changes?"
- Options:
  - "Yes, commit all" (default)
  - "Select specific files"
  - "Cancel"

### Step 4: Generate or Use Commit Message

**If message provided:**
- Use as-is

**If message NOT provided:**
- Analyze changes using Git diff
- Generate conventional commit message:
  - `feat:` - New feature
  - `fix:` - Bug fix
  - `docs:` - Documentation
  - `style:` - Formatting, UI/UX
  - `refactor:` - Code refactoring
  - `test:` - Tests
  - `chore:` - Build, dependencies

**Format:**
```
<type>: <short description>

<optional detailed description>

Co-Authored-By: Claude Sonnet 4.5 <noreply@anthropic.com>
```

### Step 5: Stage Files

```bash
# All files (default)
git add .

# Specific files
git add <files>
```

### Step 6: Commit

```bash
git commit -m "$(cat <<'EOF'
<commit message>
EOF
)"
```

### Step 7: Push (unless --no-push)

```bash
git push
```

If first push on new branch:
```bash
git push -u origin <branch-name>
```

### Step 8: Success Report

```
✅ Commit: <hash>
✅ Files: <count>
✅ Pushed to: origin/<branch>

GitHub: https://github.com/<user>/<repo>/commit/<hash>
```

## Examples

### Example 1: Auto-generated Message

```bash
/commit
```

**Workflow:**
1. Detects changes in `frontend/src/app/not-found.tsx`
2. Generates: `"feat: 커스텀 404 에러 페이지 추가"`
3. Commits and pushes

### Example 2: Custom Message

```bash
/commit "fix: 로고 클릭 시 스크롤 버그 수정"
```

### Example 3: Commit Only (No Push)

```bash
/commit "docs: README 업데이트" --no-push
```

### Example 4: Amend Previous Commit

```bash
/commit --amend
```

### Example 5: Specific Files

```bash
/commit --files frontend/src/components/*.tsx
```

## Conventional Commits Types

| Type | Description | Example |
|------|-------------|---------|
| `feat` | New feature | `feat: 사용자 로그인 기능 추가` |
| `fix` | Bug fix | `fix: 헤더 다크모드 색상 수정` |
| `docs` | Documentation | `docs: API 문서 업데이트` |
| `style` | Formatting, UI | `style: 버튼 스타일 개선` |
| `refactor` | Code refactoring | `refactor: useAuth 훅 리팩토링` |
| `perf` | Performance | `perf: 이미지 로딩 최적화` |
| `test` | Tests | `test: 로그인 테스트 추가` |
| `build` | Build system | `build: webpack 설정 변경` |
| `ci` | CI/CD | `ci: Jenkins 파이프라인 추가` |
| `chore` | Dependencies, etc | `chore: 의존성 업데이트` |

## Scope Examples (Optional)

```
feat(auth): 로그인 기능 추가
fix(ui): 버튼 정렬 수정
docs(api): REST API 문서 작성
```

## Error Handling

### No Git Repository
```
❌ Error: Not a git repository
💡 Run: git init
```

### No Changes
```
ℹ️ No changes to commit
Working tree clean
```

### Merge Conflict
```
❌ Error: Merge conflict detected
💡 Resolve conflicts first, then commit
```

### Push Failed
```
❌ Error: Push failed (rejected)
💡 Run: git pull --rebase
```

## Safety Rules

- ⚠️ Never use `--force` without explicit user confirmation
- ⚠️ Never commit `.env` files (already in .gitignore)
- ⚠️ Never amend pushed commits without warning
- ⚠️ Always show changes before committing

## Integration with PDCA

After successful commit, suggest:
```
💡 Next steps:
- /pdca status - Check PDCA progress
- /pdca next - Continue to next phase
- /pdca analyze <feature> - Run gap analysis
```

## Related Skills

- `/pdca` - PDCA cycle management
- `/code-review` - Review code before commit
- `/zero-script-qa` - Test before commit

---

## Implementation Notes

**Tools to use:**
- `Bash` - Git commands
- `AskUserQuestion` - User confirmation
- `Read` - Analyze changed files
- `Grep` - Search for patterns in diff

**Do NOT use:**
- `Write` or `Edit` for commit (use Bash only)

**Git Safety Protocol:**
- Check branch before push
- Warn if pushing to main/master
- Never skip hooks (--no-verify)
- Never force push without confirmation

---

Generated with bkit v1.5.0 - Custom Skill
