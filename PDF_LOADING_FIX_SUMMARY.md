# PDF Loading Issue - Quick Fix Summary

## Problem
PDFs not loading in student course-content section with error:
```
No static resource chapters/1/filename.pdf
```

## Root Cause
Absolute file paths stored in database instead of relative paths.

## ✅ FIXES APPLIED

### 1. Code Fix (Prevents Future Issues)
**File:** `src/main/java/com/coaxial/service/ChapterFileService.java`
- **Line 74-75:** Changed from absolute path to relative path
- All new uploads will now work correctly

### 2. Data Migration (Fixes Existing Data)
**Two New Files Created:**
- `ChapterFilePathMigrationService.java` - Service to migrate paths
- `ChapterFilePathMigrationController.java` - API endpoint to trigger migration

**SQL Script:** `fix_chapter_file_paths.sql` - Alternative SQL-based fix

## 🚀 HOW TO APPLY THE FIX

### Step 1: Deploy Code (Already Done ✅)
The code changes are ready in your workspace.

### Step 2: Rebuild Application
```bash
mvn clean package
```

### Step 3: Restart Application
```bash
java -jar target/coaxial-0.0.1-SNAPSHOT.jar
```

### Step 4: Run Migration
**Option A - Using API (Recommended):**
```bash
curl -X POST http://localhost:8080/api/admin/migration/chapter-file-paths \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

**Option B - Using SQL:**
```bash
psql -U postgres -d coaxial -f fix_chapter_file_paths.sql
```

### Step 5: Verify
1. Login as student
2. Go to course content → select chapter
3. Click on PDF → Should load correctly ✅

## 📊 What Changed?

### Before (❌ Broken):
```
Database: D:/coaxial-03-010-25/coaxial-backend/uploads/chapters/1/file.pdf
URL:      /D:/coaxial-03-010-25/coaxial-backend/uploads/chapters/1/file.pdf
Result:   404 Not Found
```

### After (✅ Working):
```
Database: uploads/chapters/1/file.pdf
URL:      /uploads/chapters/1/file.pdf
Result:   200 OK - PDF loads correctly
```

## 📝 Testing Checklist

After applying the fix:

- [ ] New file uploads work
- [ ] Existing PDFs load correctly
- [ ] No console errors
- [ ] PDFs with spaces in filename work
- [ ] PDFs with special characters work

## 🔧 Troubleshooting

### If PDFs still don't load:

1. **Check migration ran successfully:**
   ```sql
   SELECT id, file_name, file_path 
   FROM chapter_uploaded_files 
   LIMIT 5;
   ```
   Paths should start with `uploads/chapters/`

2. **Check file exists:**
   ```bash
   ls -la uploads/chapters/1/
   ```

3. **Check application logs:**
   ```bash
   tail -f logs/coaxial.log
   ```

4. **Test direct URL:**
   ```
   http://localhost:8080/uploads/chapters/1/filename.pdf
   ```
   Should work!

## 📚 Documentation

For detailed information, see:
- **Full Documentation:** `docs/CHAPTER_PDF_LOADING_FIX.md`
- **SQL Script:** `fix_chapter_file_paths.sql`

## ⚡ Quick Commands

```bash
# Rebuild
mvn clean package

# Run migration via API
curl -X POST http://localhost:8080/api/admin/migration/chapter-file-paths \
  -H "Authorization: Bearer $(cat .admin-token)"

# Or run SQL
psql -U postgres -d coaxial -f fix_chapter_file_paths.sql

# Verify
curl http://localhost:8080/uploads/chapters/1/Treewell%20Product%20Catlog.pdf
```

## 🎉 Status: READY TO DEPLOY

All files are ready. Just rebuild, restart, and run the migration!

