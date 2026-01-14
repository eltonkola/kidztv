# KidzTV Website

Official landing page for [KidzTV](https://github.com/eltonkola/kidztv) - A safe, offline video player made for kids.

## Features

- Modern, responsive single-page design
- Beautiful gradient color scheme matching the app's branding
- Screenshot gallery showcasing the app
- Feature highlights and FAQ section
- SEO optimized with proper meta tags

## Development

### Prerequisites

- Node.js 16+ and npm

### Installation

```bash
npm install
```

### Running Locally

```bash
npm run dev
```

Visit `http://localhost:5173` to view the website.

### Building for Production

```bash
npm run build
```

The built files will be in the `dist/` directory.

## Adding Your Assets

Before deploying, add your images to the following locations:

### Logo
Place your logo (with white text) at:
```
public/images/logo.png
```

### Screenshots
Place your landscape screenshots at:
```
public/images/screenshots/home.png
public/images/screenshots/playback.png
public/images/screenshots/controls.png
public/images/screenshots/library.png
```

**Screenshot Requirements:**
- Format: PNG or JPG
- Orientation: Landscape (16:9 recommended)
- Resolution: 1920x1080px or similar

## Deployment

### Option 1: GitHub Pages (Recommended)

#### Automatic Deployment with GitHub Actions

This repository includes a GitHub Actions workflow that automatically builds and deploys the site to GitHub Pages whenever you push to the `main` branch.

**Setup Steps:**

1. **Enable GitHub Pages:**
   - Go to your repository settings
   - Navigate to **Settings > Pages**
   - Under "Build and deployment", select **Source: GitHub Actions**

2. **Push your code:**
   ```bash
   git add .
   git commit -m "Initial commit"
   git push origin main
   ```

3. **Wait for deployment:**
   - Go to the **Actions** tab in your repository
   - Wait for the deployment workflow to complete
   - Your site will be available at: `https://[username].github.io/[repo-name]/`

#### Manual Deployment to GitHub Pages

If you prefer manual deployment:

```bash
npm run build
npx gh-pages -d dist
```

**Note:** You may need to update `vite.config.ts` to set the correct `base` path:

```typescript
export default defineConfig({
  base: '/kidztv-website/', // Replace with your repo name
  plugins: [react()],
})
```

### Option 2: Vercel

[![Deploy with Vercel](https://vercel.com/button)](https://vercel.com/new)

1. Push your code to GitHub
2. Go to [vercel.com](https://vercel.com)
3. Click "New Project"
4. Import your GitHub repository
5. Vercel will auto-detect Vite and configure everything
6. Click "Deploy"

### Option 3: Netlify

[![Deploy to Netlify](https://www.netlify.com/img/deploy/button.svg)](https://app.netlify.com/start)

1. Push your code to GitHub
2. Go to [netlify.com](https://netlify.com)
3. Click "New site from Git"
4. Choose your repository
5. Build settings:
   - Build command: `npm run build`
   - Publish directory: `dist`
6. Click "Deploy site"

### Option 4: Cloudflare Pages

1. Push your code to GitHub
2. Go to [Cloudflare Pages](https://pages.cloudflare.com/)
3. Click "Create a project"
4. Connect your GitHub repository
5. Build settings:
   - Build command: `npm run build`
   - Build output directory: `dist`
6. Click "Save and Deploy"

### Option 5: Custom Server

Build the site and upload the `dist/` folder to any static hosting:

```bash
npm run build
```

Upload the contents of `dist/` to your web server's public directory.

## Custom Domain

### GitHub Pages

1. Add a `CNAME` file to the `public/` directory with your domain:
   ```
   www.kidztv.com
   ```

2. Configure DNS:
   - Add a CNAME record pointing to `[username].github.io`
   - Or add A records pointing to GitHub's IPs:
     ```
     185.199.108.153
     185.199.109.153
     185.199.110.153
     185.199.111.153
     ```

### Vercel/Netlify/Cloudflare

Follow their respective documentation for custom domains - typically just add the domain in their dashboard and update your DNS records.

## Technologies Used

- **React 18** - UI framework
- **TypeScript** - Type safety
- **Vite** - Build tool and dev server
- **Tailwind CSS** - Styling
- **Lucide React** - Icons

## Project Structure

```
.
├── public/
│   └── images/
│       ├── logo.png              # App logo
│       └── screenshots/          # App screenshots
├── src/
│   ├── App.tsx                   # Main application component
│   ├── main.tsx                  # Application entry point
│   └── index.css                 # Global styles
├── .github/
│   └── workflows/
│       └── deploy.yml            # GitHub Actions deployment
└── index.html                    # HTML template
```

## License

This website is for the KidzTV project.

KidzTV is Copyright © 2019 Elton Kola. Licensed under the Apache License 2.0.

## Support

For issues related to the KidzTV app, please visit the [main repository](https://github.com/eltonkola/kidztv).

For website-specific issues, please open an issue in this repository.
