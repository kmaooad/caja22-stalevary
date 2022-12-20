export BOT_TOKEN="5620337367:AAFqW_u_yKWlTRsh3-N_bxyTTHUiDSDCpAg"
export NGROK_URL="https://aeab-37-73-80-97.ngrok.io"
curl https://api.telegram.org/bot${BOT_TOKEN}/setWebhook?url=${NGROK_URL}/api/TelegramWebhook
curl https://api.telegram.org/bot${BOT_TOKEN}/getWebhookInfo