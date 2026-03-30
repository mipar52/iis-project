import React, { useMemo, useState } from "react";
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Grid,
  Stack,
  Typography,
} from "@mui/material";
import { getAccessToken } from "../api/http";
import InfoButton from "../components/InfoButton";

type ApiResult = { kind: "import"; data: any } | { kind: "users"; data: any };

async function fetchJson(url: string, init: RequestInit = {}) {
  const res = await fetch(url, init);
  const text = await res.text();

  let data: any = null;
  try {
    data = text ? JSON.parse(text) : null;
  } catch {
    data = text;
  }

  if (!res.ok) {
    const msg =
      typeof data === "string"
        ? data
        : data
          ? JSON.stringify(data)
          : `HTTP ${res.status}`;
    throw new Error(msg);
  }

  return data;
}

export default function ImportTab() {
  const [xmlFile, setXmlFile] = useState<File | null>(null);
  const [jsonFile, setJsonFile] = useState<File | null>(null);

  const [result, setResult] = useState<ApiResult | null>(null);
  const [err, setErr] = useState<string | null>(null);
  const [loading, setLoading] = useState<"import" | "users" | null>(null);

  const headers = useMemo(() => {
    const h: Record<string, string> = {};
    const token = getAccessToken();
    if (token) h["Authorization"] = `Bearer ${token}`;
    return h;
  }, []);

  const canImport = Boolean(xmlFile || jsonFile);

  async function submitImport() {
    setErr(null);
    setResult(null);
    setLoading("import");

    try {
      const fd = new FormData();
      if (xmlFile) fd.append("xmlFile", xmlFile);
      if (jsonFile) fd.append("jsonFile", jsonFile);

      const data = await fetchJson("/api/import/okta-user", {
        method: "POST",
        body: fd,
        headers,
      });

      setResult({ kind: "import", data });
    } finally {
      setLoading(null);
    }
  }

  async function loadUsers() {
    setErr(null);
    setResult(null);
    setLoading("users");

    try {
      const data = await fetchJson("/api/import/users", {
        method: "GET",
        headers,
      });

      setResult({ kind: "users", data });
    } finally {
      setLoading(null);
    }
  }

  return (
    <Box>
      <Typography variant="h5" gutterBottom>
        Import (XML/JSON) + pregled korisnika
      </Typography>
      <InfoButton
        title="Tab 2 — REST XML/JSON import (Opis zadatka)"
        text={`REST API sučelje koje uključuje servis (endpoint) koja će biti pozvana POST
metodom i poslati XML i JSON datoteku. XML i JSON datoteke moraju
sadržavati proizvoljne podatke za entitet koji je vezan za domenu zadanog
REST API sučelja. Zadani entitet prvo se mora validirati, provjeriti jesu li svi
zadani podaci ispravni pomoću XSD i JSON validacije datoteke sheme, a tek
zatim ga spremiti u bazu podataka sustava. U slučaju pogrešaka, potrebno je
korisniku prikazati pogreške validacije. (LO2 – 2 boda, LO3 – 2 boda, LO5 – 2
boda)`}
      />

      <Stack spacing={2}>
        {err && <Alert severity="error">{err}</Alert>}

        {result?.kind === "import" && (
          <Alert severity="success">Import OK</Alert>
        )}
        {result?.kind === "users" && (
          <Alert severity="success">Učitani korisnici</Alert>
        )}

        <Card>
          <CardContent>
            <Typography variant="subtitle1" gutterBottom>
              Import
            </Typography>
            <Typography variant="body2" sx={{ color: "text.secondary", mb: 2 }}>
              POST /api/import/okta-user (multipart: xmlFile, jsonFile)
            </Typography>

            <Grid container spacing={2}>
              <Grid item xs={12} md={6}>
                <Stack spacing={1}>
                  <input
                    type="file"
                    accept=".xml,application/xml,text/xml"
                    onChange={(e) => setXmlFile(e.target.files?.[0] ?? null)}
                  />
                  <Typography variant="body2">
                    XML: {xmlFile ? xmlFile.name : "(nije odabran)"}
                  </Typography>
                  {xmlFile && (
                    <Button
                      size="small"
                      variant="outlined"
                      onClick={() => setXmlFile(null)}
                    >
                      Clear XML
                    </Button>
                  )}
                </Stack>
              </Grid>

              <Grid item xs={12} md={6}>
                <Stack spacing={1}>
                  <input
                    type="file"
                    accept=".json,application/json"
                    onChange={(e) => setJsonFile(e.target.files?.[0] ?? null)}
                  />
                  <Typography variant="body2">
                    JSON: {jsonFile ? jsonFile.name : "(nije odabran)"}
                  </Typography>
                  {jsonFile && (
                    <Button
                      size="small"
                      variant="outlined"
                      onClick={() => setJsonFile(null)}
                    >
                      Clear JSON
                    </Button>
                  )}
                </Stack>
              </Grid>
            </Grid>

            <Stack direction="row" spacing={2} sx={{ mt: 2 }}>
              <Button
                variant="contained"
                disabled={!canImport || loading !== null}
                onClick={() => submitImport().catch((e) => setErr(e.message))}
              >
                {loading === "import" ? "Importing..." : "Submit import"}
              </Button>

              <Button
                variant="outlined"
                disabled={loading !== null}
                onClick={() => loadUsers().catch((e) => setErr(e.message))}
              >
                {loading === "users" ? "Loading..." : "Get all users"}
              </Button>
            </Stack>
          </CardContent>
        </Card>

        {result && (
          <Card>
            <CardContent>
              <Typography variant="subtitle2" gutterBottom>
                Response
              </Typography>
              <pre style={{ overflowX: "auto", margin: 0 }}>
                {JSON.stringify(result.data, null, 2)}
              </pre>
            </CardContent>
          </Card>
        )}
      </Stack>
    </Box>
  );
}
