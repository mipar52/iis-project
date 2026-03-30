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
import {
  clearTokens,
  getAccessToken,
  getRefreshToken,
  setAccessToken,
  setRefreshToken,
  httpJson,
  REFRESH_TOKEN_KEY,
} from "../api/http";
import InfoButton from "../components/InfoButton";

type AuthResponse = {
  accessToken: string;
  refreshToken?: string;
};

export default function AuthTab() {
  const [loginUsername, setLoginUsername] = useState("test1");
  const [loginPassword, setLoginPassword] = useState("test1");
  const [regUsername, setRegUsername] = useState("");
  const [regPassword, setRegPassword] = useState("");
  const [regRole, setRegRole] = useState<"USER" | "ADMIN">("USER");

  const [manualToken, setManualToken] = useState("");
  const [status, setStatus] = useState<string | null>(null);
  const [err, setErr] = useState<string | null>(null);

  const stored = useMemo(() => {
    return { accessToken: getAccessToken(), refreshToken: getRefreshToken() };
  }, [status]);

  async function doLogin() {
    setErr(null);
    setStatus(null);

    const body = { username: loginUsername, password: loginPassword };
    const res = await httpJson<AuthResponse>("/api/auth/login", {
      method: "POST",
      body: JSON.stringify(body),
      auth: false,
    });

    setAccessToken(res.accessToken);
    if (res.refreshToken) setRefreshToken(res.refreshToken);

    setStatus("Login OK. Token spremljen u localStorage.");
  }

  async function doRegister() {
    setErr(null);
    setStatus(null);

    const body = {
      username: regUsername,
      password: regPassword,
      role: regRole,
    };
    const res = await httpJson<any>("/api/auth/register", {
      method: "POST",
      body: JSON.stringify(body),
      auth: false,
    });

    setStatus("Register OK: " + JSON.stringify(res));
  }

  async function doRefresh() {
    setErr(null);
    setStatus(null);

    const token = localStorage.getItem(REFRESH_TOKEN_KEY);
    if (!token) {
      setErr("No refresh token saved.");
      return;
    }

    const body = { refreshToken: token };

    const response = await httpJson<any>("/api/auth/refresh", {
      method: "POST",
      body: JSON.stringify(body),
      auth: true,
    });

    if (response.accessToken) setAccessToken(response.accessToken);
    if (response.refreshToken) setRefreshToken(response.refreshToken);

    setStatus("Refreshed!");
  }

  async function doRevoke() {
    setErr(null);
    setStatus(null);
    const body = { refreshToken: localStorage.getItem(REFRESH_TOKEN_KEY) };

    await httpJson<AuthResponse>("api/auth/revoke", {
      method: "POST",
      body: JSON.stringify(body),
      auth: true,
    });
    //if (response.refreshToken) setRefreshToken(response.refreshToken);
    setStatus("Revoked!");
    setAccessToken(null);
    setRefreshToken(null);
  }

  function saveManualToken() {
    setErr(null);
    setStatus(null);

    const t = manualToken.trim();
    if (!t) {
      setErr("Unesi token.");
      return;
    }
    setAccessToken(t);
    setStatus("Token spremljen ručno.");
  }

  function logout() {
    clearTokens();
    setStatus("Tokeni obrisani.");
  }

  return (
    <Box>
      <Typography variant="h5" gutterBottom>
        Tab 1 — Login / Registracija (JWT)
      </Typography>

      <InfoButton
        text="Napišite klijentsku desktop ili web aplikaciju (Java ili C#) koja će sadržavati
grafičko sučelje i omogućiti korisnicima pozivanje usluge iz prvih šest koraka.
Aplikacija mora imati dvije korisničke uloge: samo za čitanje (može pozivati
samo krajnje točke GET) i potpuni pristup (može pozivati sve krajnje
točke). (LO1 – 2 boda, LO3 – 4 boda, LO7 4 boda)"
        title="Login / Registracija + React klijent"
      />

      {err && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {err}
        </Alert>
      )}
      {status && (
        <Alert severity="success" sx={{ mb: 2 }}>
          {status}
        </Alert>
      )}

      <Grid container spacing={2}>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6">Login</Typography>
              <Typography
                variant="body2"
                sx={{ color: "text.secondary", mb: 2 }}
              >
                POST /api/auth/login
              </Typography>

              <TextField
                label="Username"
                fullWidth
                margin="normal"
                value={loginUsername}
                onChange={(e) => setLoginUsername(e.target.value)}
              />
              <TextField
                label="Password"
                type="password"
                fullWidth
                margin="normal"
                value={loginPassword}
                onChange={(e) => setLoginPassword(e.target.value)}
              />

              <Button
                variant="contained"
                onClick={() => doLogin().catch((e) => setErr(e.message))}
              >
                Login
              </Button>

              <Divider sx={{ my: 2 }} />

              <Typography variant="subtitle2">Trenutno spremljeno</Typography>
              <Typography variant="body2" sx={{ wordBreak: "break-all" }}>
                accessToken:{" "}
                {stored.accessToken ? stored.accessToken : "(nema)"}
              </Typography>
              <Typography variant="body2" sx={{ wordBreak: "break-all" }}>
                refreshToken:{" "}
                {stored.refreshToken ? stored.refreshToken : "(nema)"}
              </Typography>

              <Button color="error" sx={{ mt: 2 }} onClick={logout}>
                Clear tokens
              </Button>
              <Button
                variant="contained"
                onClick={() => doRevoke().catch((e) => setErr(e.message))}
              >
                Revoke token
              </Button>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6">Registracija</Typography>
              <Typography
                variant="body2"
                sx={{ color: "text.secondary", mb: 2 }}
              >
                POST /api/auth/register
              </Typography>

              <TextField
                label="Username"
                fullWidth
                margin="normal"
                value={regUsername}
                onChange={(e) => setRegUsername(e.target.value)}
              />
              <TextField
                label="Password"
                type="password"
                fullWidth
                margin="normal"
                value={regPassword}
                onChange={(e) => setRegPassword(e.target.value)}
              />

              <TextField
                select
                label="Role (dev)"
                fullWidth
                margin="normal"
                value={regRole}
                onChange={(e) => setRegRole(e.target.value as "USER" | "ADMIN")}
              >
                <MenuItem value="USER">USER</MenuItem>
                <MenuItem value="ADMIN">ADMIN</MenuItem>
              </TextField>

              <Button
                variant="outlined"
                onClick={() => doRegister().catch((e) => setErr(e.message))}
              >
                Register
              </Button>

              <Divider sx={{ my: 2 }} />

              <Typography variant="h6">Manual token (fallback)</Typography>
              <TextField
                label="Paste JWT access token"
                fullWidth
                margin="normal"
                value={manualToken}
                onChange={(e) => setManualToken(e.target.value)}
              />
              <Button variant="contained" onClick={saveManualToken}>
                Save token
              </Button>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6">Manual token modification</Typography>
              <Typography
                variant="body2"
                sx={{ color: "text.secondary", mb: 2 }}
              >
                POST /api/auth/refresh
              </Typography>

              <Button
                variant="contained"
                onClick={() => doRefresh().catch((e) => setErr(e.message))}
              >
                Refresh token
              </Button>

              <Divider sx={{ my: 2 }} />

              <Typography variant="h6"></Typography>
              <Typography
                variant="body2"
                sx={{ color: "text.secondary", mb: 2 }}
              >
                POST /api/auth/revoke
              </Typography>

              <Button color="error" sx={{ mt: 2 }} onClick={doRevoke}>
                Revoke token
              </Button>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
