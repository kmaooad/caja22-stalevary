## Telegram bot (Stalevary)
### Business logic:
* Activities
    * Bulk import courses/course projects
        * Mass create/update courses
    * Manage extracurricular activities
        * Create/update/delete any extra activities (e.g. projects, hackathons, challenges etc.)
    * Manage courses
        * Manually create/update/delete courses
    * Assign competences to activities
        * Manually set/unset relation between activity and competences

### Develop stage
1. Install ngrok (MacOs)

```
brew install ngrok/ngrok/ngrok
```

2. Setup redirect link (default PORT=7071)

```
ngrok http http://localhost:{PORT}
```

3. Copy ngrok url

```
curl https://api.telegram.org/bot{BOT_TOKEN}/setWebhook?url={NGROK_URL}/api/TelegramWebhook
```

4. Check telegram webhook status

```
curl https://api.telegram.org/bot{BOT_TOKEN}/getWebhookInfo
```

5. Start azure cloud function locally

```
mvn clean package
```

```
mvn azure-functions:run -DenableDebug
```