import React, { useMemo, useState } from "react";
import {
  AppBar,
  Box,
  Container,
  Tab,
  Tabs,
  Toolbar,
  Typography,
} from "@mui/material";

import AuthTab from "./tabs/AuthTab";
import ImportTab from "./tabs/ImportTab";
import SoapTab from "./tabs/SoapTab";
import DhmzTab from "./tabs/DhmzTab";
import GraphqlTab from "./tabs/GraphQlTab";

type TabKey = "auth" | "import" | "soap" | "dhmz" | "graphql";

function tabIndexFromHash(): TabKey {
  const h = (location.hash || "").replace("#", "");
  if (
    h === "auth" ||
    h === "import" ||
    h === "soap" ||
    h === "dhmz" ||
    h === "graphql"
  )
    return h;
  return "auth";
}

export default function App() {
  const [tab, setTab] = useState<TabKey>(() => tabIndexFromHash());

  useMemo(() => {
    const handler = () => setTab(tabIndexFromHash());
    window.addEventListener("hashchange", handler);
    return () => window.removeEventListener("hashchange", handler);
  }, []);

  const idx = ["auth", "import", "soap", "dhmz", "graphql"].indexOf(tab);

  return (
    <Box sx={{ minHeight: "100vh", bgcolor: "#fafafa" }}>
      <AppBar position="sticky">
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            IIS Dashboard (React)
          </Typography>
        </Toolbar>
        <Tabs
          value={idx}
          onChange={(_, newIdx) => {
            const key = ["auth", "import", "soap", "dhmz", "graphql"][
              newIdx
            ] as TabKey;
            location.hash = key;
          }}
          variant="scrollable"
          scrollButtons="auto"
        >
          <Tab label="1) Auth" />
          <Tab label="2) REST XML/JSON import" />
          <Tab label="3) TURBO SAPUN" />
          <Tab label="4) MEGA VRIJEME (gRPC)" />
          <Tab label="5) GraphQL" />
        </Tabs>
      </AppBar>

      <Container sx={{ py: 3 }}>
        {tab === "auth" && <AuthTab />}
        {tab === "import" && <ImportTab />}
        {tab === "soap" && <SoapTab />}
        {tab === "dhmz" && <DhmzTab />}
        {tab === "graphql" && <GraphqlTab />}
      </Container>
    </Box>
  );
}
