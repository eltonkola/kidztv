# Quick Deployment Guide

## Fastest Way to Deploy (GitHub Pages with Actions)

### Prerequisites
- Your code is in a GitHub repository
- You have admin access to the repository

### Steps

1. **Add Your Assets First**
   ```bash
   # Add your logo to:
   public/images/logo.png

   # Add your screenshots to:
   public/images/screenshots/home.png
   public/images/screenshots/playback.png
   public/images/screenshots/controls.png
   public/images/screenshots/library.png
   ```

2. **Enable GitHub Pages**
   - Go to repository **Settings > Pages**
   - Under "Build and deployment":
     - Source: Select **GitHub Actions**

3. **Configure Base Path (if needed)**

   If your site will be at `username.github.io/repo-name`:

   Edit `vite.config.ts` and uncomment the base path:
   ```typescript
   export default defineConfig({
     base: '/repo-name/', // Replace with your actual repo name
     plugins: [react()],
     // ...
   });
   ```

   If using a custom domain (like `kidztv.com`), leave base as default.

4. **Push Your Code**
   ```bash
   git add .
   git commit -m "Deploy KidzTV website"
   git push origin main
   ```

5. **Wait for Deployment**
   - Go to **Actions** tab in your GitHub repository
   - Watch the "Deploy to GitHub Pages" workflow
   - Once complete (green checkmark), your site is live!
   - Access it at: `https://username.github.io/repo-name/`

### Setting Up a Custom Domain

1. **Add CNAME file**
   ```bash
   # Create file: public/CNAME
   # Contents: your-domain.com
   ```

2. **Configure DNS**

   Add these A records to your domain's DNS:
   ```
   185.199.108.153
   185.199.109.153
   185.199.110.153
   185.199.111.153
   ```

   Or add a CNAME record:
   ```
   CNAME: username.github.io
   ```

3. **Update vite.config.ts**
   ```typescript
   export default defineConfig({
     base: '/', // Use root for custom domain
     // ...
   });
   ```

4. **Push changes**
   ```bash
   git add .
   git commit -m "Add custom domain"
   git push origin main
   ```

5. **Verify in GitHub Settings**
   - Go to **Settings > Pages**
   - Enter your custom domain
   - Check "Enforce HTTPS"

## Alternative: Manual Deployment

If you prefer not to use GitHub Actions:

```bash
# Build the site
npm run build

# Install gh-pages (one time)
npm install -D gh-pages

# Deploy
npx gh-pages -d dist
```

## Troubleshooting

### Images not loading
- Make sure images are in `public/images/` directory
- Check file names match exactly (case-sensitive)
- Verify base path in `vite.config.ts` is correct

### 404 errors on GitHub Pages
- Verify the base path matches your repo name
- Check that GitHub Actions completed successfully
- Ensure GitHub Pages is enabled in settings

### Workflow fails
- Check the Actions tab for error details
- Verify `package.json` and `package-lock.json` are committed
- Ensure all dependencies are listed in package.json

## Testing Locally Before Deploy

```bash
# Install dependencies
npm install

# Run development server
npm run dev

# Build and preview production version
npm run build
npm run preview
```

Visit `http://localhost:5173` (dev) or `http://localhost:4173` (preview)

## Need Help?

See the full [README.md](./README.md) for more deployment options including:
- Vercel
- Netlify
- Cloudflare Pages
- Custom servers
