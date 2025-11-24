import { defineConfig } from 'vite';

export default defineConfig({
  server: {
    host: true,
    allowedHosts: true,
    port: 49160,
    // cors: false,
    proxy: {
      "/api": {
        target: "http://cs.merrimack.edu:49161",  
        changeOrigin: true,
        secure: false
      }
    }
  }
}); 