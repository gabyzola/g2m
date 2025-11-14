import { defineConfig } from 'vite';
import fs from 'fs';

export default defineConfig({
  server: {
    host: '0.0.0.0',
    port: 49160,
    https: {
      key: fs.readFileSync('./certs/privkey.pem'),
      cert: fs.readFileSync('./certs/fullchain.pem')
    }
  }
});
  
