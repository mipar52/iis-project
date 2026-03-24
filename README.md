# iis-project

Quick test endpoints:
```bash
curl -i \
  -H "Accept: application/json" \
  -H "Authorization: SSWS OKTA_API_TOKEN" \
  "https://dev-6x28kdc6wjb5lq66.okta.com/api/v1/users?limit=10"
```

```bash
curl -i -X POST \
  -F "xmlFile=@okta-user.xml;type=application/xml" \
  -F "jsonFile=@okta-user.json;type=application/json" \
  "http://localhost:8080/api/import/okta-user"
```


