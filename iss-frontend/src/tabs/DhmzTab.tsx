import React, { useMemo, useState } from "react";
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from "@mui/material";
import { getAccessToken, httpJson } from "../api/http";
import InfoButton from "../components/InfoButton";

type WeatherMatch = {
  city: string;
  temperature: number;
  unit?: string;
  weather?: string;
};

type WeatherResponse = {
  matches: WeatherMatch[];
};

function weatherKind(w?: string): "sun" | "cloud" | "rain" | "wind" | "other" {
  const s = (w ?? "").toLowerCase();

  if (
    s.includes("kiša") ||
    s.includes("kisa") ||
    s.includes("pljus") ||
    s.includes("grml")
  )
    return "rain";
  if (s.includes("obla")) return "cloud";
  if (s.includes("vedro") || s.includes("sun")) return "sun";
  if (s.includes("vjet")) return "wind";
  return "other";
}

function weatherRowSx(w?: string) {
  const k = weatherKind(w);
  switch (k) {
    case "rain":
      return { backgroundColor: "rgba(33, 150, 243, 0.08)" }; // plava
    case "cloud":
      return { backgroundColor: "rgba(158, 158, 158, 0.10)" }; // siva
    case "sun":
      return { backgroundColor: "rgba(255, 193, 7, 0.12)" }; // žuta
    case "wind":
      return { backgroundColor: "rgba(0, 188, 212, 0.08)" }; // cyan
    default:
      return {};
  }
}

function weatherChip(w?: string) {
  const k = weatherKind(w);
  const label = w ?? "-";
  const color =
    k === "rain"
      ? "primary"
      : k === "sun"
        ? "warning"
        : k === "cloud"
          ? "default"
          : k === "wind"
            ? "info"
            : "default";
  return (
    <Chip
      size="small"
      label={label}
      color={color as any}
      variant={k === "cloud" ? "outlined" : "filled"}
    />
  );
}

export default function DhmzTab() {
  const [query, setQuery] = useState("zag");
  const [data, setData] = useState<WeatherResponse | null>(null);
  const [err, setErr] = useState<string | null>(null);

  const headers = useMemo(() => {
    const h: Record<string, string> = {};
    const token = getAccessToken();
    if (token) h["Authorization"] = `Bearer ${token}`;
    return h;
  }, []);

  async function search() {
    setErr(null);
    setData(null);

    const wres = await httpJson<WeatherResponse>(
      `/api/weather/search?q=${encodeURIComponent(query)}`,
      {
        method: "GET",
        headers: headers,
      },
    );
    setData(wres);
  }

  return (
    <Box>
      <Typography variant="h5" gutterBottom>
        Tab 4 — DHMZ (gRPC server) — kroz REST proxy
      </Typography>

      <InfoButton
        text="Izradite gRPC poslužiteljsku aplikaciju koja će, koristeći DHMZ
(https://vrijeme.hr/hrvatska_n.xml), omogućiti dohvaćanje trenutne
temperature prema zadanom nazivu grada ili dijelu naziva grada. Ako postoji
više unosa koji odgovaraju dijelu naziva grada, svi bi trebali biti ispisani.
Usluga mora biti dostupna s klijentske radne površine ili web aplikacije. ( LO2
– 4 boda, LO3 – 2 boda, LO5 – 2 boda)"
        title="Tab 4 — DHMZ (gRPC server) — kroz REST proxy"
      />

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
                    <TableCell>Weather</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {(data.matches || []).map((m, i) => (
                    <TableRow key={i} sx={weatherRowSx(m.weather)}>
                      <TableCell>{m.city}</TableCell>
                      <TableCell>{m.temperature}</TableCell>
                      <TableCell>{m.unit ?? "°C"}</TableCell>
                      <TableCell>{weatherChip(m.weather)}</TableCell>
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
