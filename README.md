# Style Auditor Pattern — readable

## Что внутри

- одна кнопка анализа;
- один endpoint: `POST /api/analyze`;
- каждый чек вынесен в отдельный Java-класс;
- общий `AnalysisService`, без дублирования анализаторов;
- подсветка для локальных проблем;
- тире в диалогах не подсвечиваются: `PatternDashOutsideDialogueCheck` игнорирует тире, если оно первое значимое на строке.

## Запуск backend

```bash
cd backend
mvn spring-boot:run
```

## Запуск frontend

```bash
cd frontend
npm install
npm run dev
```

## Где добавлять проверки

Создай новый класс в:

```text
backend/src/main/java/com/styleauditor/checks
```

Реализуй интерфейс:

```java
public class MyCheck implements TextCheck {
    @Override
    public CheckResult check(ChunkContext context) {
        return new CheckResult();
    }
}
```

Потом добавь класс в список `checks` внутри `AnalysisService`.



## Легенда

Во фронтенде добавлен спойлер-легенда: расшифровка цветов, пунктов 1–20 и общих метрик S1–S3.
