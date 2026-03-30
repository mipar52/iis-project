# iis-project

## App overview
This project is a multi-interface backend + client “dashboard” application built to demonstrate several integration styles and validation techniques in one system.

At a high level, the application:

### Manages two different user concepts

App Users: users of this application (stored in the app_users table). They authenticate via username/password, receive JWT access + refresh tokens, and have a role (USER or ADMIN) that controls what they are allowed to do.

Okta Users (domain entity): a separate entity used for demonstrating XML/JSON import, SOAP filtering, and GraphQL operations. These are not the same as App Users.

Exposes multiple APIs for different assignment points

### REST Import (XML + JSON)
A REST endpoint accepts XML and JSON files, validates:

### XML against an XSD, and JSON against a JSON Schema

If valid, the entity is stored in the database; if not, the API returns detailed validation errors.

### SOAP Search with XPath + XML Validation

The SOAP service generates an XML export file from backend data, validates that generated XML using Jakarta XML validation, then applies XPath filtering based on the search term and returns the filtered results in the SOAP response.

### gRPC Weather Service (DHMZ integration)

A gRPC server integrates with DHMZ weather data (from vrijeme.hr) and returns the current temperature for a given city name or substring. If more than one city matches, it returns all matches.

### GraphQL API

GraphQL is provided to query and manage the domain entity. Queries are available for “read” operations, while mutations are typically restricted to ADMIN users.

### Client

Includes a React client dashboard A simple tabbed UI provides a single place to test all parts:

Login/registration
XML/JSON import endpoints
SOAP calls
DHMZ weather search
GraphQL queries/mutations
Role-based access control (RBAC)

USER role: read-only access (e.g., GET and GraphQL queries)
ADMIN role: full CRUD access (POST/PUT/DELETE and GraphQL mutations)


## Okta

Okta is a cloud-based Identity and Access Management (IAM) platform. It is commonly used by organizations to centralize and secure authentication and authorization across applications.

In practice, Okta provides:

**User directory and lifecycle management**
- Store users, groups, and profile attributes; manage provisioning, deactivation, and user status.

**Authentication services**

- Single Sign-On (SSO)
- Multi-Factor Authentication (MFA)
- Password policies and account recovery
- Authorization and access control

**Group-based access**
- Role and policy management
- Integration with standards such as OAuth 2.0, OpenID Connect, and SAML
- Integration ecosystem Okta integrates with many SaaS and enterprise apps, allowing one identity to be used across multiple systems.


## Okta documentation
- Okta Users API documentation: https://developer.okta.com/docs/api/openapi/asa/asa/users

### Okta domain
- Main URL: https://<domin>.okta.com/admin/users
- Users: https://<domain>.okta.com/admin/users

## Running the projects

Frontend client:
```bash
cd iss-fronend
npm i
npm run dev
```

## Quick commands

**Maven:**

```bash
# clean
./mvnw -q clean package -DskipTests
```

```bash
# generate - xml, grcp, graphql
./mvnw -q clean generate-sources
```

**Quick test endpoints:**
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
