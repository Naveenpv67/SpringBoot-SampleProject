curl --request GET \
  --url 'https://YOUR_DOMAIN.atlassian.net/rest/api/3/issue/YOUR_ISSUE_KEY?fields=description' \
  --header 'Authorization: Basic YOUR_BASE64_ENCODED_CREDENTIALS' \
  --header 'Accept: application/json'
