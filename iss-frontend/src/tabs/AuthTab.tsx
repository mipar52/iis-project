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
  httpJson,
} from "../api/http";
import InfoButton from "../components/InfoButton";

type AuthResponse = {
  accessToken: string;
  //refreshToken?: string;
};

export default function AuthTab() {
  const [loginUsername, setLoginUsername] = useState("test1");
  const [loginPassword, setLoginPassword] = useState("test1");
  const [regUsername, setRegUsername] = useState("");
  const [regPassword, setRegPassword] = useState("");
  const [regRole, setRegRole] = useState<"USER" | "ADMIN">("USER");
  const [authResponse, setAuthResponse] = useState<AuthResponse>({
    accessToken: "",
  });
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
    setAuthResponse(res);
    // if (res.refreshToken) setRefreshToken(res.refreshToken);

    setStatus("Login prosao, svaka cast..");
  }

  async function doRegister() {
    setErr(null);
    setStatus(null);

    const body = {
      username: regUsername,
      password: regPassword,
      role: regRole,
    };
    const res = await httpJson<AuthResponse>("/api/auth/register", {
      method: "POST",
      body: JSON.stringify(body),
      auth: false,
    });

    setStatus("Registracija prosla, mozes ti to: " + JSON.stringify(res));
  }

  async function doRefresh() {
    setErr(null);
    setStatus(null);

    const res = await httpJson<AuthResponse>("/api/auth/refresh", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: "{}",
      credentials: "include",
    });

    if (res?.accessToken) setAccessToken(res.accessToken);

    setAuthResponse(res);

    setStatus("Dosta osvjezavajuce!");
  }

  async function doRevoke() {
    setErr(null);
    setStatus(null);

    const res = await fetch("/api/auth/revoke", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: "{}",
      credentials: "include",
    });

    const text = await res.text();
    let data: any;
    try {
      data = text ? JSON.parse(text) : null;
    } catch {
      data = text;
    }

    if (!res.ok)
      throw new Error(typeof data === "string" ? data : JSON.stringify(data));

    setStatus("Unistio si ga!");
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
              <Card>
                <CardContent>
                  <Typography variant="subtitle2" gutterBottom>
                    Response
                  </Typography>
                  <pre
                    style={{ overflowX: "auto", margin: 0, color: "#ffffff" }}
                  >
                    {JSON.stringify(authResponse, null, 2)}
                  </pre>
                </CardContent>
              </Card>

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
