import { defineConfig } from 'vite';
import fs from 'fs';

export default defineConfig({
  server: {
    host: true,
    allowedHosts: true,
    port: 49160,
    https: {
      key: fs.readFileSync('./certs/privkey.pem'),
      cert: fs.readFileSync('./certs/fullchain.pem')
    }
  }
});