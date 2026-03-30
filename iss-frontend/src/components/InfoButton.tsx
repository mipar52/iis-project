import React, { useState } from "react";
import InfoOutlinedIcon from "@mui/icons-material/InfoOutlined";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  Tooltip,
  Typography,
} from "@mui/material";

type Props = {
  readonly title: string;
  readonly text: string;
};

export default function InfoButton({ title, text }: Props) {
  const [open, setOpen] = useState(false);

  return (
    <>
      <Tooltip title="Info">
        <IconButton size="small" onClick={() => setOpen(true)}>
          <InfoOutlinedIcon fontSize="small" />
        </IconButton>
      </Tooltip>

      <Dialog
        open={open}
        onClose={() => setOpen(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>{title}</DialogTitle>
        <DialogContent dividers>
          <Typography
            component="pre"
            sx={{
              whiteSpace: "pre-wrap",
              fontFamily: "system-ui",
              m: 0,
            }}
          >
            {text}
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)}>Close</Button>
        </DialogActions>
      </Dialog>
    </>
  );
}
