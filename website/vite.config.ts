import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({
  // Uncomment and set the base path if deploying to GitHub Pages with a repo name
  // For example: base: '/kidztv-website/' if your repo is github.com/username/kidztv-website
  // Leave as '/' if using a custom domain or deploying to the root of the domain
  // base: '/kidztv-website/',

  plugins: [react()],
  optimizeDeps: {
    exclude: ['lucide-react'],
  },
});
