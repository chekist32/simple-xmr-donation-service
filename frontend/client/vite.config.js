import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

import path from 'path';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@shared-components': path.resolve('../shared/global_components'),
      '@shared-views': path.resolve('../shared/global_views'),
      '@components': path.resolve('./src/components'),
      '@views': path.resolve('./src/views')
    }
  }
});
