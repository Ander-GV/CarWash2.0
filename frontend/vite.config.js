import { defineConfig } from 'vite';
import { resolve } from 'path';

export default defineConfig({
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  },
  build: {
    rollupOptions: {
      input: {
        main: resolve(__dirname, 'index.html'),
        admin: resolve(__dirname, 'admin/index.html'),
        cliente: resolve(__dirname, 'cliente/index.html'),
        empleado: resolve(__dirname, 'empleado/index.html'),
        encargado: resolve(__dirname, 'encargado/index.html'),
      }
    }
  }
});

