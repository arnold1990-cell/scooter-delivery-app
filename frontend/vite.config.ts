import path from 'node:path';
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

const appBase = process.env.VITE_APP_BASE || '/';

export default defineConfig({
  base: appBase,
  plugins: [react()],
  resolve: {
    alias: [
      {
        find: 'mapbox-gl/dist/mapbox-gl.css',
        replacement: path.resolve(__dirname, 'src/vendor/mapbox-gl.css')
      },
      {
        find: /^mapbox-gl$/,
        replacement: path.resolve(__dirname, 'src/lib/mapbox-gl-shim.ts')
      }
    ]
  },
  server: {
    port: 5173,
    strictPort: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
});
