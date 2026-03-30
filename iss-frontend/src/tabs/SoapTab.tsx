import React, { useState } from "react";
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  FormControlLabel,
  Switch,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from "@mui/material";
import { httpText } from "../api/http";
import InfoButton from "../components/InfoButton";

type SoapUser = {
  id?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  login?: string;
  mobilePhone?: string;
};

function buildSoapEnvelope(term: string, exact: boolean) {
  // NAMESPACE_URI = http://milan.com/iis/oktauser/soap
  // localPart = searchOktaUsersRequest
  return `<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:ok="http://milan.com/iis/oktauser/soap">
  <soapenv:Header/>
  <soapenv:Body>
    <ok:searchOktaUsersRequest>
      <ok:term>${escapeXml(term)}</ok:term>
      <ok:exact>${exact ? "true" : "false"}</ok:exact>
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

function formatXml(xmlText: string) {
  const doc = new DOMParser().parseFromString(xmlText, "application/xml");

  // ako parser napravi <parsererror>, vrati original da vidiš problem
  if (doc.getElementsByTagName("parsererror").length) return xmlText;

  const serializer = new XMLSerializer();
  const raw = serializer.serializeToString(doc);

  // XSLT indent (radi u browserima koji podržavaju XSLTProcessor)
  try {
    const xsltDoc = new DOMParser().parseFromString(
      `<?xml version="1.0" encoding="UTF-8"?>
       <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
         <xsl:output method="xml" indent="yes"/>
         <xsl:strip-space elements="*"/>
         <xsl:template match="@*|node()">
           <xsl:copy>
             <xsl:apply-templates select="@*|node()"/>
           </xsl:copy>
         </xsl:template>
       </xsl:stylesheet>`,
      "application/xml",
    );

    const proc = new XSLTProcessor();
    proc.importStylesheet(xsltDoc);
    const outDoc = proc.transformToDocument(doc);
    return new XMLSerializer().serializeToString(outDoc);
  } catch {
    // fallback: bar ubaci nove linije između tagova
    return raw.replace(/></g, ">\n<");
  }
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
  const [exact, setExact] = useState(false);
  const [raw, setRaw] = useState<string | null>(null);
  const [users, setUsers] = useState<SoapUser[]>([]);
  const [err, setErr] = useState<string | null>(null);

  async function search() {
    setErr(null);
    setRaw(null);
    setUsers([]);

    const body = buildSoapEnvelope(term, exact);

    const res = await httpText("/ws", {
      method: "POST",
      headers: {
        "Content-Type": "text/xml; charset=utf-8",
        // SOAPAction: ""  // makni
      },
      body,
    });

    setRaw(formatXml(res.text));

    const doc = new DOMParser().parseFromString(res.text, "text/xml");
    const fault = doc.getElementsByTagName("Fault")[0];
    if (fault) {
      const faultString =
        fault.getElementsByTagName("faultstring")[0]?.textContent ??
        "SOAP Fault";
      throw new Error(faultString);
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
          <InfoButton
            text="SOAP sučelje koje uključuje uslugu koja prima pojam prema kojem se traži
entitet. Prije toga, na backendu se mora generirati XML datoteka koja sadrži
podatke dohvaćene iz jedne od REST API metoda prema zadanoj temi.
Uneseni pojam, koji je ulazni podatak SOAP metode, mora se koristiti za
filtriranje samo onih zapisa koji odgovaraju zadanom pojmu uz pomoć XPath-a
i pripremljene XML datoteke, te ih vratiti kao rezultat poziva SOAP metode.
(LO2 – 4 boda, LO3 – 2 boda, LO5 – 4 boda)


Koristeći Jakarta XML, provjerite pripremljenu datoteku iz prethodnog koraka
kako biste vidjeli je li u skladu s postavljenim pravilima validacije i vratite
poruke o validaciji ako podaci u XML datoteci nisu valjani. (LO2 – 4 boda, LO5
– 2 boda; LO7 – 2 boda)"
            title="Tab 3 — SOAP: search (generiranje XML + XPath + validacija) (Opis zadatka)"
          />
          <TextField
            label="Term"
            fullWidth
            value={term}
            onChange={(e) => setTerm(e.target.value)}
          />
          <FormControlLabel
            sx={{ mt: 1 }}
            control={
              <Switch
                checked={exact}
                onChange={(e) => setExact(e.target.checked)}
              />
            }
            label="Exact match"
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
