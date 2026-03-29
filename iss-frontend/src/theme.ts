import { createTheme } from "@mui/material/styles";

export const theme = createTheme({
  palette: {
    mode: "light",
    primary: { main: "#7c3aed" },
    secondary: { main: "#06b6d4" },
  },
  shape: {
    borderRadius: 14,
  },
  typography: {
    fontFamily: [
      "system-ui",
      "-apple-system",
      "Segoe UI",
      "Roboto",
      "sans-serif",
    ].join(","),
    h5: {
      fontWeight: 700,
      letterSpacing: "-0.3px",
    },
  },
  components: {
    MuiAppBar: {
      styleOverrides: {
        root: {
          background:
            "linear-gradient(90deg, rgba(124,58,237,1) 0%, rgba(147,51,234,1) 40%, rgba(6,182,212,1) 100%)",
        },
      },
    },
    MuiTabs: {
      styleOverrides: {
        root: {
          backdropFilter: "blur(10px)",
        },
        indicator: {
          height: 4,
          borderRadius: 999,
        },
      },
    },
    MuiTab: {
      styleOverrides: {
        root: {
          textTransform: "none",
          fontWeight: 600,
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          border: "1px solid rgba(0,0,0,0.06)",
          boxShadow:
            "rgba(0, 0, 0, 0.10) 0 10px 20px -10px, rgba(0, 0, 0, 0.06) 0 6px 14px -12px",
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          fontWeight: 700,
        },
      },
    },
    MuiTextField: {
      defaultProps: {
        size: "small",
      },
    },
  },
});
