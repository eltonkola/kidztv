# KidzTV Website Assets

## Directory Structure

```
public/images/
├── logo.png                    # Main logo (with white text)
└── screenshots/
    ├── home.png               # Home screen screenshot
    ├── playback.png           # Video playback screenshot
    ├── controls.png           # Parental controls screenshot
    └── library.png            # Video library screenshot
```

## Asset Requirements

### Logo (logo.png)
- The logo with white text will be displayed on the colored gradient header
- Recommended format: PNG with transparent background
- Recommended height: 48px (width will scale automatically)
- The logo will also be used as the favicon

### Screenshots (screenshots/*.png)
- All screenshots should be in **landscape orientation**
- Recommended aspect ratio: 16:9 (landscape)
- Recommended resolution: 1920x1080px or similar landscape resolution
- Format: PNG or JPG
- Screenshots will be displayed in a 2-column grid on desktop

## How to Add Your Assets

1. Place your logo file at: `public/images/logo.png`
2. Place your screenshots at:
   - `public/images/screenshots/home.png`
   - `public/images/screenshots/playback.png`
   - `public/images/screenshots/controls.png`
   - `public/images/screenshots/library.png`

The website will automatically load these images when they are present. If images are not found, fallback placeholders will be shown.
