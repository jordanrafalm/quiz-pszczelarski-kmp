# Hosting Privacy Policy for App Store

> Quick guide to host `/privacy-policy.md` and obtain a public URL for App Store Connect

---

## Option 1: GitHub Pages (Recommended — Free, Fast)

### Prerequisites
- Repository must be public OR you have GitHub Pro/Team

### Steps

#### 1. Enable GitHub Pages
1. Go to repository: https://github.com/jordanrafalm/quiz-pszczelarski-kmp
2. **Settings** → **Pages**
3. **Source**: Select `main` branch
4. **Folder**: Select `/docs` (recommended) or `/ (root)`
5. Click **Save**

#### 2. Convert MD to HTML (Optional)
GitHub Pages renders `.md` files automatically, but for better control:

```bash
# Install pandoc (if not installed)
brew install pandoc

# Convert privacy-policy.md to HTML
pandoc privacy-policy.md -o docs/privacy-policy.html --standalone --metadata title="Polityka Prywatności — Quiz Pszczelarski"
```

#### 3. Push changes
```bash
git add docs/privacy-policy.html
git commit -m "Add HTML privacy policy for App Store"
git push origin main
```

#### 4. Wait for Deployment
- GitHub Actions will deploy automatically (2-5 minutes)
- Check: https://github.com/jordanrafalm/quiz-pszczelarski-kmp/actions

#### 5. Access URL
- **Markdown**: `https://jordanrafalm.github.io/quiz-pszczelarski-kmp/privacy-policy`
- **HTML**: `https://jordanrafalm.github.io/quiz-pszczelarski-kmp/docs/privacy-policy.html`

#### 6. Verify
Open URL in browser, ensure it displays correctly.

#### 7. Add to App Store Connect
- **App Store Connect** → **App Information** → **Privacy Policy URL**
- Paste URL
- Save

---

## Option 2: Netlify (Alternative — Free)

### Steps

#### 1. Create `netlify.toml`
```toml
[build]
  publish = "docs"
  command = "echo 'No build needed'"
```

#### 2. Deploy
1. Go to https://app.netlify.com/
2. **New site from Git** → Connect GitHub repo
3. Select `quiz-pszczelarski-kmp`
4. Deploy settings:
   - **Build command**: (leave empty)
   - **Publish directory**: `docs`
5. Click **Deploy site**

#### 3. Custom Domain (Optional)
- Netlify provides: `https://random-name.netlify.app/privacy-policy.html`
- Or configure custom domain: `privacy.quizpszczelarski.app`

---

## Option 3: Vercel (Alternative — Free)

### Steps

1. Go to https://vercel.com/
2. **Import Project** → GitHub → Select repo
3. Framework: **Other**
4. Root Directory: `docs`
5. Deploy
6. Access: `https://quiz-pszczelarski-kmp.vercel.app/privacy-policy.html`

---

## Option 4: Custom Domain + Simple Hosting

### If You Own a Domain
- Host `privacy-policy.html` on any web server
- Ensure HTTPS is enabled
- Example: `https://quizpszczelarski.pl/privacy-policy.html`

---

## Option 5: Raw GitHub (Not Recommended)

### Quick Workaround (Temporary)
You can use GitHub's raw file URL:

```
https://raw.githubusercontent.com/jordanrafalm/quiz-pszczelarski-kmp/main/privacy-policy.md
```

**⚠️ Limitations:**
- Not user-friendly (shows raw markdown)
- May not pass Apple review (prefer rendered HTML)

---

## Recommended: GitHub Pages with HTML

### Complete Commands

```bash
# From project root
cd /Users/rafal/projects/quiz-pszczelarski-kmp

# Convert to HTML (optional, for better formatting)
pandoc privacy-policy.md -o docs/privacy-policy.html \
  --standalone \
  --metadata title="Polityka Prywatności — Quiz Pszczelarski" \
  --css=https://cdn.jsdelivr.net/npm/water.css@2/out/water.css

# Commit and push
git add docs/privacy-policy.html
git commit -m "Add HTML privacy policy for App Store submission"
git push origin main

# Enable GitHub Pages (manual — see above)
# Then access: https://jordanrafalm.github.io/quiz-pszczelarski-kmp/docs/privacy-policy.html
```

---

## Verification Checklist

- [ ] URL is public and accessible (no authentication required)
- [ ] Page displays correctly on mobile and desktop
- [ ] HTTPS is enabled
- [ ] Content matches `/privacy-policy.md`
- [ ] URL is stable (won't change)
- [ ] No redirect loops or errors

---

## Next Steps

1. Choose hosting option (recommend: GitHub Pages)
2. Deploy privacy policy
3. Copy public URL
4. Paste URL in **App Store Connect** → **App Information** → **Privacy Policy URL**
5. Save and re-submit app for review
