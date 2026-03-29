# iis-project

Frontend client:
```bash
cd iss-fronend
npm i
npm run dev
```

Maven:

```bash
# clean
./mvnw -q clean package -DskipTests
```

```bash
# generate - xml, grcp, graphql
./mvnw -q clean generate-sources
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

LOGIN:
```bash
curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test"}'
```

REGISTER:
```bash
curl -s -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test"}'
```

CREATE USER:
```bash
curl -i -X POST http://localhost:8081/api/v1/users \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "profile": {
      "firstName":"Milica",
      "lastName":"Krmptoci",
      "login":"mili@algebra.hr",
      "email":"mili@algebra.hr"
    },
    "type": { "id":"oty1192io1nxYU7Lq6767" }
  }'
```

GraphQL:

- Get user:
```bash
curl -i -X POST http://localhost:8081/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -d '{"query":"query { user(id: \"00uXXXXXXXXXXXXXXX\") { id status profile { firstName lastName login email } } }"}'
```

- create user:
```bash
curl -i -X POST http://localhost:8081/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -d '{"query":"mutation { createUser(input: { status: \"ACTIVE\", type: { id: \"employee\" }, profile: { firstName: \"Branko\", lastName: \"Kockica\", login: \"bkocka@algebra.hr\", email: \"bkocka@algebra.hr\", mobilePhone: \"+385911234567\" } }) { id status created profile { firstName lastName login email mobilePhone } type { id } } }"}'
```

- update user:
```bash
curl -i -X POST http://localhost:8081/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -d '{"query":"mutation { updateUser(id: \"00uXXXXXXXXXXXXXXX\", input: { status: \"SUSPENDED\", profile: { mobilePhone: \"+385911111111\" } }) { id status lastUpdated profile { firstName lastName login email mobilePhone } } }"}'
```
- delete user:
```bash
curl -i -X POST http://localhost:8081/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -d '{"query":"mutation { deleteUser(id: \"00uXXXXXXXXXXXXXXX\") }"}'
```

TODO:
- Better error handling
- React client!
