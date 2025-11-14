const express = require("express");
const https = require("https");
const fs = require("fs");
const path = require("path");

// Load certificates
const options = {
  key: fs.readFileSync(path.join(__dirname, "certs/privkey.pem")),
  cert: fs.readFileSync(path.join(__dirname, "certs/fullchain.pem"))
};

const app = express();

// Serve static files
app.use(express.static("public"));

// Example route
app.get("/", (req, res) => {
  res.sendFile(path.join(__dirname, "public/login.html"));
});

// Start HTTPS server on port 49160
https.createServer(options, app).listen(49160, () => {
  console.log("HTTPS server running at https://cs.merrimack.edu:49160");
});
