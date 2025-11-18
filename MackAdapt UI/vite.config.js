import { defineConfig } from 'vite';

export default defineConfig({
  server: {
    host: true,
    allowedHosts: true,
    port: 49160,
    // cors: false,
    proxy: {
      "/api": {
        target: "http://localhost:8080",  
        changeOrigin: true,
        secure: false
      }
    }
  }
});