import { defineConfig } from 'vite';

export default defineConfig({
  server: {
    host: true,
    allowedHosts: true,
    port: 49160,
    cors: false,
    proxy: {
      "/api": {
        target: "http://localhost:8080",  //gaby if you read this i am seriously crossing my fingers that this doesnt mess stuff up
        changeOrigin: true,
        secure: false
      }
    }
  }
});