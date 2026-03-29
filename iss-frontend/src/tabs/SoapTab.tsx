import React, { useState } from "react";
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from "@mui/material";
import { httpText } from "../api/http";

type SoapUser = {
  id?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  login?: string;
  mobilePhone?: string;
};

function buildSoapEnvelope(term: string) {
  // NAMESPACE_URI = http://milan.com/iis/oktauser/soap
  // localPart = searchOktaUsersRequest
  return `<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:ok="http://milan.com/iis/oktauser/soap">
  <soapenv:Header/>
  <soapenv:Body>
    <ok:searchOktaUsersRequest>
      <ok:term>${escapeXml(term)}</ok:term>
    </ok:searchOktaUsersRequest>
  </soapenv:Body>
</soapenv:Envelope>`;
}

function escapeXml(s: string) {
  return s
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&apos;");
}

function parseSoapUsers(xmlText: string): SoapUser[] {
  const doc = new DOMParser().parseFromString(xmlText, "text/xml");

  // tražimo sve <user> elemente bez obzira na namespace
  const users = Array.from(doc.getElementsByTagName("user"));
  return users.map((u) => {
    const get = (tag: string) => {
      const el = Array.from(u.getElementsByTagName(tag))[0];
      return el?.textContent ?? undefined;
    };
    return {
      id: get("id"),
      firstName: get("firstName"),
      lastName: get("lastName"),
      email: get("email"),
      login: get("login"),
      mobilePhone: get("mobilePhone"),
    };
  });
}

export default function SoapTab() {
  const [term, setTerm] = useState("");
  const [raw, setRaw] = useState<string | null>(null);
  const [users, setUsers] = useState<SoapUser[]>([]);
  const [err, setErr] = useState<string | null>(null);

  async function search() {
    setErr(null);
    setRaw(null);
    setUsers([]);

    const body = buildSoapEnvelope(term);
    const res = await httpText("/ws", {
      method: "POST",
      headers: {
        "Content-Type": "text/xml; charset=utf-8",
        SOAPAction: "",
      },
      body,
    });

    setRaw(res.text);

    if (res.text.includes("Fault")) {
      throw new Error("SOAP Fault detected. Pogledaj RAW response.");
    }

    setUsers(parseSoapUsers(res.text));
  }

  return (
    <Box>
      <Typography variant="h5" gutterBottom>
        Tab 3 — SOAP: search (generiranje XML + XPath + validacija)
      </Typography>

      {err && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {err}
        </Alert>
      )}

      <Card sx={{ mb: 2 }}>
        <CardContent>
          <Typography variant="body2" sx={{ color: "text.secondary", mb: 1 }}>
            Endpoint: POST /ws (SOAP Envelope)
          </Typography>
          <TextField
            label="Term"
            fullWidth
            value={term}
            onChange={(e) => setTerm(e.target.value)}
          />
          <Button
            sx={{ mt: 2 }}
            variant="contained"
            onClick={() => search().catch((e) => setErr(e.message))}
          >
            Search SOAP
          </Button>
        </CardContent>
      </Card>

      {users.length > 0 && (
        <Card>
          <CardContent>
            <Typography variant="h6">Matches ({users.length})</Typography>
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>First</TableCell>
                  <TableCell>Last</TableCell>
                  <TableCell>Email</TableCell>
                  <TableCell>Login</TableCell>
                  <TableCell>Mobile</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {users.map((u, i) => (
                  <TableRow key={i}>
                    <TableCell>{u.id}</TableCell>
                    <TableCell>{u.firstName}</TableCell>
                    <TableCell>{u.lastName}</TableCell>
                    <TableCell>{u.email}</TableCell>
                    <TableCell>{u.login}</TableCell>
                    <TableCell>{u.mobilePhone}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      )}

      {raw && (
        <Card sx={{ mt: 2 }}>
          <CardContent>
            <Typography variant="subtitle2">RAW SOAP response</Typography>
            <pre style={{ overflowX: "auto" }}>{raw}</pre>
          </CardContent>
        </Card>
      )}
    </Box>
  );
}
