import { defineConfig } from 'vite';
import path from 'path'

export default defineConfig({
  build: {
    rollupOptions: {
      input: {
        main: path.resolve(__dirname, "index.html"),
        login: path.resolve(__dirname, "login.html"),
        classes: path.resolve(__dirname, "classes.html"),
        "student-dashboard": path.resolve(__dirname, "student-dashboard.html")
      }
    },
    outDir: "dist"
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