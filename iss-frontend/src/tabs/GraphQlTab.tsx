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

type GraphQlResponse = any;

const presets = {
  users: {
    title: "Query: users",
    query: `query {
  users {
    id
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
    id
    status
    created
    profile { firstName lastName login email mobilePhone }
    type { id }
  }
}`,
    variables: {
      input: {
        status: "ACTIVE",
        type: { id: "employee" },
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
      id: "00u...",
      input: {
        status: "SUSPENDED",
        profile: { mobilePhone: "+385911111111" },
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

  const [query, setQuery] = useState(preset.query);
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
