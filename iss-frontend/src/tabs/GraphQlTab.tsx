import React, { useMemo, useState } from "react";
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Divider,
  Grid,
  MenuItem,
  TextField,
  Typography,
} from "@mui/material";
import { httpJson } from "../api/http";
import InfoButton from "../components/InfoButton";

type GraphQlResponse = any;

const presets = {
  users: {
    title: "Query: users",
    query: `query {
  users {
    status
    created
    lastUpdated
    profile { firstName lastName login email mobilePhone secondEmail }
    type { id }
  }
}`,
    variables: {},
  },
  userById: {
    title: "Query: user(id)",
    query: `query($id: ID!) {
  user(id: $id) {
    id
    status
    created
    profile { firstName lastName login email }
  }
}`,
    variables: { id: "00u..." },
  },
  createUser: {
    title: "Mutation: createUser",
    query: `mutation($input: CreateUserInput!) {
  createUser(input: $input) {
    profile { firstName lastName login email mobilePhone }
  }
}`,
    variables: {
      input: {
        profile: {
          firstName: "Branko",
          lastName: "Kockica",
          login: "bkocka@algebra.hr",
          email: "bkocka@algebra.hr",
          mobilePhone: "+385911234567",
        },
      },
    },
  },
  updateUser: {
    title: "Mutation: updateUser",
    query: `mutation($id: ID!, $input: UpdateUserInput!) {
  updateUser(id: $id, input: $input) {
    id
    status
    lastUpdated
    profile { firstName lastName login email mobilePhone }
    type { id }
  }
}`,
    variables: {
      id: "00u11ga7ck8ZgfWuu698",
      input: {
        profile: {
          firstName: "Brankyy",
          lastName: "Kockii",
          mobilePhone: "+385911111111",
          login: "bkockica@gmail.com",
          email: "bkockica@gmail.com",
        },
      },
    },
  },
  deleteUser: {
    title: "Mutation: deleteUser",
    query: `mutation($id: ID!) {
  deleteUser(id: $id)
}`,
    variables: { id: "00u..." },
  },
} as const;

type PresetKey = keyof typeof presets;

export default function GraphqlTab() {
  const [presetKey, setPresetKey] = useState<PresetKey>("users");
  const preset = useMemo(() => presets[presetKey], [presetKey]);

  const [query, setQuery] = useState(() => preset.query as string);
  const [variablesText, setVariablesText] = useState(
    JSON.stringify(preset.variables, null, 2),
  );

  const [out, setOut] = useState<GraphQlResponse | null>(null);
  const [err, setErr] = useState<string | null>(null);

  function loadPreset(key: PresetKey) {
    setPresetKey(key);
    setQuery(presets[key].query);
    setVariablesText(JSON.stringify(presets[key].variables, null, 2));
    setOut(null);
    setErr(null);
  }

  async function execute() {
    setErr(null);
    setOut(null);

    let variables: any = {};
    if (variablesText.trim()) {
      variables = JSON.parse(variablesText);
    }

    const res = await httpJson<GraphQlResponse>("/graphql", {
      method: "POST",
      body: JSON.stringify({ query, variables }),
    });

    setOut(res);
  }

  return (
    <Box>
      <Typography variant="h5" gutterBottom>
        Tab 5 — GraphQL
      </Typography>

      <InfoButton
        text="Koristite REST API i integrirajte svoju aplikaciju s njim. Implementirajte
prilagođenu verziju ovog REST API sučelja koja se povezuje s bazom
podataka aplikacije i pruža sve četiri krajnje točke (GET, POST, PUT i
DELETE) s JWT tokenima (access i refresh) te koristi GraphQL. Dodajte
prekidač u konfiguraciju aplikacije koji će omogućiti promjenu s javnog na
prilagođeno REST API sučelje (LO3 – 8 bodova, LO4 – 12 bodova, LO5 – 12
bodova, LO6 – 2 boda, LO7 – 8 bodova)."
        title="Tab 5 — GraphQL"
      />

      <Card sx={{ mb: 2 }}>
        <CardContent>
          <Typography variant="subtitle2" sx={{ mb: 1 }}>
            CRUD buttons
          </Typography>

          <Box sx={{ display: "flex", gap: 1, flexWrap: "wrap" }}>
            <Button variant="outlined" onClick={() => loadPreset("users")}>
              GET (users)
            </Button>

            <Button variant="outlined" onClick={() => loadPreset("createUser")}>
              POST (createUser)
            </Button>

            <Button variant="outlined" onClick={() => loadPreset("updateUser")}>
              PUT (updateUser)
            </Button>

            <Button
              variant="outlined"
              color="error"
              onClick={() => loadPreset("deleteUser")}
            >
              DELETE (deleteUser)
            </Button>

            <Button variant="text" onClick={() => loadPreset("userById")}>
              GET by id (user)
            </Button>
          </Box>
        </CardContent>
      </Card>

      {err && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {err}
        </Alert>
      )}

      <Card sx={{ mb: 2 }}>
        <CardContent>
          <Typography variant="subtitle2" sx={{ mb: 1 }}>
            Presets
          </Typography>

          <TextField
            select
            fullWidth
            label="Preset"
            value={presetKey}
            onChange={(e) => loadPreset(e.target.value as PresetKey)}
          >
            {Object.entries(presets).map(([k, v]) => (
              <MenuItem key={k} value={k}>
                {v.title}
              </MenuItem>
            ))}
          </TextField>
        </CardContent>
      </Card>

      <Grid container spacing={2}>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="subtitle2">Query</Typography>
              <TextField
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                fullWidth
                multiline
                minRows={12}
                margin="normal"
              />
              <Divider sx={{ my: 1 }} />
              <Typography variant="subtitle2">Variables (JSON)</Typography>
              <TextField
                value={variablesText}
                onChange={(e) => setVariablesText(e.target.value)}
                fullWidth
                multiline
                minRows={8}
                margin="normal"
              />

              <Button
                variant="contained"
                onClick={() => execute().catch((e) => setErr(e.message))}
              >
                Execute
              </Button>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="subtitle2">Response</Typography>
              {out ? (
                <pre style={{ overflowX: "auto" }}>
                  {JSON.stringify(out, null, 2)}
                </pre>
              ) : (
                <Typography variant="body2" sx={{ color: "text.secondary" }}>
                  (nema response još)
                </Typography>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
