module.exports = {
    authority: `https://login.microsoftonline.com/<TENANT_ID>/v2.0`,
    clientId: process.env.AZURE_CLIENT_ID,
    clientSecret: process.env.AZURE_CLIENT_SECRET,
    audience: process.env.AZURE_CLIENT_ID,
    issuer: `https://sts.windows.net/<TENANT_ID>/`
  };
  