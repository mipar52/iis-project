# iis-project

Maven:

```bash
# clean
./mvnw -q clean package -DskipTests
```

```bash
# generate - xml, grcp

```

Quick test endpoints:
OKTA:
```bash
curl -i \
  -H "Accept: application/json" \
  -H "Authorization: SSWS OKTA_API_TOKEN" \
  "https://dev-6x28kdc6wjb5lq66.okta.com/api/v1/users?limit=10"
```

IMPORT XML & JSON:

```bash
curl -i -X POST \
  -F "xmlFile=@okta-user.xml;type=application/xml" \
  -F "jsonFile=@okta-user.json;type=application/json" \
  "http://localhost:8080/api/import/okta-user"
```

REST testing:

GET:
```bash
curl -i http://localhost:8081/api/v1/users
```

Create:
```bash
 curl -i -X POST http://localhost:8081/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "profile": {
      "firstName": "Branko",
      "lastName": "Kockica",
      "login": "bkockica@algebra.hr",
      "email": "bkockica@algebra.hr"
    },
    "type": { "id": "oty1192io1nxYU7Lq698" }
  }'

```


