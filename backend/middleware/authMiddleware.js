const jwt = require("express-jwt");
const jwksRsa = require("jwks-rsa");
const config = require("../config/authConfig");

// Protect API routes
const checkJwt = jwt({
  secret: jwksRsa.expressJwtSecret({
    cache: true,
    jwksUri: `${config.authority}/discovery/v2.0/keys`
  }),
  audience: config.audience,
  issuer: config.issuer,
  algorithms: ["RS256"]
});

module.exports = checkJwt;
