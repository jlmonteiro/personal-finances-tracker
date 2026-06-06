import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  base: '/finances/',
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/finances/api': {
        target: 'http://localhost:8080',
        rewrite: (path) => path.replace(/^\/finances/, ''),
      },
    },
  },
  build: {
    outDir: 'dist',
    lib: {
      entry: 'src/parcel.tsx',
      formats: ['es'],
      fileName: () => 'assets/index.js',
    },
    rollupOptions: {
      output: {
        assetFileNames: 'assets/style[extname]',
      },
    },
  },
  define: {
    'process.env.NODE_ENV': JSON.stringify('production'),
  },
  test: {
    reporters: ['default', 'junit'],
    outputFile: {
      junit: 'test-results/junit.xml',
    },
  },
})
