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
import { httpJson } from "../api/http";

type WeatherMatch = {
  city: string;
  temperature: number;
  unit?: string;
};

type WeatherResponse = {
  matches: WeatherMatch[];
};

export default function DhmzTab() {
  const [query, setQuery] = useState("zag");
  const [data, setData] = useState<WeatherResponse | null>(null);
  const [err, setErr] = useState<string | null>(null);

  async function search() {
    setErr(null);
    setData(null);

    // REST proxy endpoint (treba postojati na backendu)
    const res = await httpJson<WeatherResponse>(
      `/api/weather/search?query=${encodeURIComponent(query)}`,
      {
        method: "GET",
      },
    );
    setData(res);
  }

  return (
    <Box>
      <Typography variant="h5" gutterBottom>
        Tab 4 — DHMZ (gRPC server) — kroz REST proxy
      </Typography>

      <Alert severity="info" sx={{ mb: 2 }}>
        <b> GET /api/weather/search?query=...</b>
      </Alert>

      {err && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {err}
        </Alert>
      )}

      <Card>
        <CardContent>
          <TextField
            label="City (substring)"
            fullWidth
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
          <Button
            sx={{ mt: 2 }}
            variant="contained"
            onClick={() => search().catch((e) => setErr(e.message))}
          >
            Search temperature
          </Button>

          {data && (
            <Box sx={{ mt: 2 }}>
              <Typography variant="h6">
                Matches ({data.matches?.length ?? 0})
              </Typography>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>City</TableCell>
                    <TableCell>Temperature</TableCell>
                    <TableCell>Unit</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {(data.matches || []).map((m, i) => (
                    <TableRow key={i}>
                      <TableCell>{m.city}</TableCell>
                      <TableCell>{m.temperature}</TableCell>
                      <TableCell>{m.unit ?? "°C"}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>

              <Typography variant="subtitle2" sx={{ mt: 2 }}>
                RAW
              </Typography>
              <pre style={{ overflowX: "auto" }}>
                {JSON.stringify(data, null, 2)}
              </pre>
            </Box>
          )}
        </CardContent>
      </Card>
    </Box>
  );
}
