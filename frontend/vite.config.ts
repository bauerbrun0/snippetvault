import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'
import runtimeEnv from 'vite-plugin-runtime-env'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(), tailwindcss(), runtimeEnv()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
})
