# Docker + one-click deploy

## Локальный запуск через Docker Compose

Из корня проекта:

```bash
docker compose up --build
```

После запуска:

- Frontend: http://localhost:5173
- Backend API: http://localhost:8080/api/analyze

## One-click deploy на Render

1. Создай GitHub-репозиторий и загрузи туда проект.
2. В Render выбери **New → Blueprint**.
3. Укажи репозиторий.
4. Render прочитает `render.yaml` и создаст два сервиса:
   - `style-auditor-backend`
   - `style-auditor-frontend`

После первого деплоя:

1. Скопируй URL backend, например:
   `https://style-auditor-backend.onrender.com`
2. В настройках frontend-сервиса задай env:
   `VITE_API_URL=https://style-auditor-backend.onrender.com/api/analyze`
3. В настройках backend-сервиса задай env:
   `FRONTEND_ORIGIN=https://адрес-фронта.onrender.com`
4. Redeploy обоих сервисов.

## Ручной deploy backend на Render

Backend:
- Runtime: Docker
- Root Directory: `backend`
- Plan: Free

Env:
- `SERVER_PORT=8080`
- `FRONTEND_ORIGIN=https://адрес-фронта`

## Ручной deploy frontend на Vercel / Netlify

Root directory: `frontend`

Build command:

```bash
npm ci && npm run build
```

Publish directory:

```text
dist
```

Env:

```text
VITE_API_URL=https://адрес-backend/api/analyze
```

## Важно

Бесплатный backend на Render может засыпать. Первый запрос после простоя иногда идёт медленно.
