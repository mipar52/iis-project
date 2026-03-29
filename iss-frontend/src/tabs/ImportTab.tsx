import React, { useState } from "react";
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Grid,
  Typography,
} from "@mui/material";
import { getAccessToken } from "../api/http";

export default function ImportTab() {
  const [xmlFile, setXmlFile] = useState<File | null>(null);
  const [jsonFile, setJsonFile] = useState<File | null>(null);

  const [out, setOut] = useState<any>(null);
  const [err, setErr] = useState<string | null>(null);

  async function submit() {
    setErr(null);
    setOut(null);

    const fd = new FormData();
    if (xmlFile) fd.append("xmlFile", xmlFile);
    if (jsonFile) fd.append("jsonFile", jsonFile);

    const headers: Record<string, string> = {};
    const token = getAccessToken();
    if (token) headers["Authorization"] = `Bearer ${token}`;

    const res = await fetch("/api/import/okta-user", {
      method: "POST",
      body: fd,
      headers,
    });

    const text = await res.text();
    let data: any = null;
    try {
      data = JSON.parse(text);
    } catch {
      data = text;
    }

    if (!res.ok)
      throw new Error(typeof data === "string" ? data : JSON.stringify(data));
    setOut(data);
  }

  return (
    <Box>
      <Typography variant="h5" gutterBottom>
        Tab 2 — REST: slanje XML + JSON (multipart) + validacija
      </Typography>

      {err && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {err}
        </Alert>
      )}
      {out && (
        <Alert severity="success" sx={{ mb: 2 }}>
          Import OK
        </Alert>
      )}

      <Card>
        <CardContent>
          <Typography variant="body2" sx={{ color: "text.secondary", mb: 2 }}>
            Endpoint: POST /api/import/okta-user (parts: xmlFile, jsonFile)
          </Typography>

          <Grid container spacing={2}>
            <Grid item xs={12} md={6}>
              <input
                type="file"
                accept=".xml,application/xml,text/xml"
                onChange={(e) => setXmlFile(e.target.files?.[0] ?? null)}
              />
              <Typography variant="body2">
                XML: {xmlFile ? xmlFile.name : "(nije odabran)"}
              </Typography>
            </Grid>

            <Grid item xs={12} md={6}>
              <input
                type="file"
                accept=".json,application/json"
                onChange={(e) => setJsonFile(e.target.files?.[0] ?? null)}
              />
              <Typography variant="body2">
                JSON: {jsonFile ? jsonFile.name : "(nije odabran)"}
              </Typography>
            </Grid>
          </Grid>

          <Button
            sx={{ mt: 2 }}
            variant="contained"
            onClick={() => submit().catch((e) => setErr(e.message))}
          >
            Submit import
          </Button>

          {out && (
            <Box sx={{ mt: 2 }}>
              <Typography variant="subtitle2">Response</Typography>
              <pre style={{ overflowX: "auto" }}>
                {JSON.stringify(out, null, 2)}
              </pre>
            </Box>
          )}
        </CardContent>
      </Card>
    </Box>
  );
}
