import { defineConfig } from 'vite';
import path from 'path'

export default defineConfig({
  build: {
    rollupOptions: {
      input: {
        main: path.resolve(__dirname, "index.html"),
        login: path.resolve(__dirname, "login.html"),
        classes: path.resolve(__dirname, "classes.html"),
        "student-dashboard": path.resolve(__dirname, "student-dashboard.html"),
        quiz: path.resolve(__dirname, "quiz.html"),          
        "class-enroll": path.resolve(__dirname, "class-enroll.html"),
        "quiz-create": path.resolve(__dirname, "quiz-create.html"),
        "class-module": path.resolve(__dirname, "class-module.html"),
        "results": path.resolve(__dirname, "results.html")
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