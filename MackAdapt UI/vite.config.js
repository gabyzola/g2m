import { defineConfig } from 'vite';
import path from 'path'

export default defineConfig({
  build: {
    rollupOptions: {
      input: {
        index: path.resolve(__dirname, 'index.html'),
        login: path.resolve(__dirname, 'login.html'),
        classes: path.resolve(__dirname, 'classes.html'),
        // add all other pages
      }
    }
  },
  server: {
    host: true,
    allowedHosts: true,
    port: 49160,
    //cors: false,
    proxy: {
      "/api": {
        target: "http://localhost:49161",  
        changeOrigin: true,
        secure: false
      }
    }
  }
}); 