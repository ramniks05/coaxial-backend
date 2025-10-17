# Chapter PDF Loading Issue - Fix Documentation

## Issue Description

**Error Message:**
```
An unexpected error occurred: No static resource chapters/1/2025%20Call%20for%20Brain%20Pool(BP%C2%B7BP+)%20fellowship%20program(ENG).pdf.
```

**Symptoms:**
- PDFs work locally but fail when deployed
- Student login course-content section shows PDF loading errors
- Error occurs while loading PDFs after chapter saving

## Root Cause

The issue was caused by **absolute file paths** being stored in the database instead of **relative paths**.

### Technical Details:

1. **In `ChapterFileService.java` (line 74 - OLD CODE):**
   ```java
   rec.setFilePath(target.toString().replace("\\", "/"));
   ```
   This saved absolute paths like:
   ```
   D:/coaxial-03-010-25/coaxial-backend/uploads/chapters/1/filename.pdf
   ```

2. **Static Resource Configuration:**
   The `StaticResourceConfig.java` maps `/uploads/**` to serve files from the `uploads` directory.

3. **The Problem:**
   - Database stored: `D:/coaxial-03-010-25/coaxial-backend/uploads/chapters/1/filename.pdf`
   - Frontend expected: `/uploads/chapters/1/filename.pdf`
   - Result: File not found error

## Solution Applied

### 1. Code Fix

**File:** `src/main/java/com/coaxial/service/ChapterFileService.java`

**Changed line 74 from:**
```java
rec.setFilePath(target.toString().replace("\\", "/"));
```

**To:**
```java
// Store relative path instead of absolute path for proper URL resolution
rec.setFilePath("uploads/chapters/" + chapter.getId() + "/" + target.getFileName().toString());
```

### 2. Data Migration

Two approaches are provided to fix existing data:

#### Option A: Using REST API (Recommended)

After deploying the code fix, call the migration endpoint:

**Endpoint:** `POST /api/admin/migration/chapter-file-paths`

**Headers:**
```
Authorization: Bearer <ADMIN_JWT_TOKEN>
Content-Type: application/json
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/admin/migration/chapter-file-paths \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -H "Content-Type: application/json"
```

**Response:**
```json
{
  "success": true,
  "message": "Chapter file path migration completed successfully",
  "updatedCount": 15
}
```

**To test with a single file first:**
```bash
curl -X POST http://localhost:8080/api/admin/migration/chapter-file-paths/{fileId} \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -H "Content-Type: application/json"
```

#### Option B: Using SQL Script

If you prefer direct database access, run the provided SQL script:

**File:** `fix_chapter_file_paths.sql`

```sql
-- Connect to your database
psql -U postgres -d coaxial

-- Or using pgAdmin, then run:
\i fix_chapter_file_paths.sql
```

## Verification Steps

After applying the fix and migration:

1. **Check Database:**
   ```sql
   SELECT id, file_name, file_path 
   FROM chapter_uploaded_files 
   WHERE file_path IS NOT NULL
   ORDER BY id;
   ```
   
   All paths should start with `uploads/chapters/`

2. **Test PDF Loading:**
   - Login as a student
   - Navigate to course content
   - Click on a chapter with PDF documents
   - PDFs should load correctly

3. **Check Browser Console:**
   - Open Developer Tools (F12)
   - Go to Network tab
   - Load a chapter with PDFs
   - Verify URLs are like: `http://localhost:8080/uploads/chapters/1/filename.pdf`
   - Should return HTTP 200 (not 404)

4. **Direct URL Test:**
   ```
   http://localhost:8080/uploads/chapters/1/Treewell%20Product%20Catlog.pdf
   ```
   Should display or download the PDF

## Files Modified/Created

### Modified:
- `src/main/java/com/coaxial/service/ChapterFileService.java` (line 74-75)

### Created:
- `src/main/java/com/coaxial/service/ChapterFilePathMigrationService.java`
- `src/main/java/com/coaxial/controller/ChapterFilePathMigrationController.java`
- `fix_chapter_file_paths.sql`
- `docs/CHAPTER_PDF_LOADING_FIX.md` (this file)

## Deployment Steps

### Development Environment:

1. **Pull latest code:**
   ```bash
   git pull origin main
   ```

2. **Rebuild the application:**
   ```bash
   mvn clean package
   ```

3. **Restart the application:**
   ```bash
   # If using command line:
   java -jar target/coaxial-0.0.1-SNAPSHOT.jar
   
   # Or if using IDE, restart the application
   ```

4. **Run migration:**
   ```bash
   curl -X POST http://localhost:8080/api/admin/migration/chapter-file-paths \
     -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
   ```

### Production Environment:

1. **Backup database:**
   ```bash
   pg_dump -U postgres coaxial > backup_before_pdf_fix.sql
   ```

2. **Deploy code changes:**
   ```bash
   # Pull latest code
   git pull origin main
   
   # Build
   mvn clean package
   
   # Stop application
   systemctl stop coaxial
   
   # Deploy new JAR
   cp target/coaxial-0.0.1-SNAPSHOT.jar /opt/coaxial/
   
   # Start application
   systemctl start coaxial
   ```

3. **Run migration:**
   ```bash
   curl -X POST https://your-domain.com/api/admin/migration/chapter-file-paths \
     -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
   ```

4. **Verify:**
   - Check logs for migration success
   - Test PDF loading from student account
   - Monitor for any errors

## Rollback Plan

If issues occur after deployment:

1. **Restore database backup:**
   ```bash
   psql -U postgres coaxial < backup_before_pdf_fix.sql
   ```

2. **Revert code:**
   ```bash
   git revert <commit-hash>
   mvn clean package
   systemctl restart coaxial
   ```

## Future Uploads

All new file uploads after this fix will automatically use relative paths. No additional configuration is needed.

## Testing Checklist

- [ ] New file uploads save with relative paths
- [ ] Existing PDFs load correctly after migration
- [ ] Student can view all chapter documents
- [ ] No 404 errors in browser console
- [ ] PDFs with special characters in filename work
- [ ] PDFs with spaces in filename work (URL encoded)

## Support

If issues persist:

1. Check application logs:
   ```bash
   tail -f logs/coaxial.log
   ```

2. Verify file system permissions:
   ```bash
   ls -la uploads/chapters/
   ```

3. Check static resource configuration:
   - Verify `file.upload.base-dir=uploads` in `application.properties`
   - Ensure `StaticResourceConfig.java` is properly configured

4. Contact development team with:
   - Error logs
   - Browser console errors
   - Database query results for affected files

## Additional Notes

### URL Encoding
File names with special characters are automatically URL-encoded by the browser:
- Spaces become `%20`
- Special chars like `·` become `%C2%B7`

This is normal and handled automatically by Spring's static resource handler.

### File Path Format

**Correct format:**
```
uploads/chapters/1/filename.pdf
```

**Incorrect formats (old issue):**
```
D:/path/to/uploads/chapters/1/filename.pdf  ❌
/D:/path/to/uploads/chapters/1/filename.pdf ❌
chapters/1/filename.pdf                      ❌ (missing uploads prefix)
```

### Related Configuration

The following configuration in `StaticResourceConfig.java` maps URLs to file system:
```java
registry.addResourceHandler("/uploads/**")
        .addResourceLocations(absolute);
```

This means:
- URL: `/uploads/chapters/1/file.pdf`
- Maps to: `{baseDir}/chapters/1/file.pdf`
- Where `{baseDir}` = `uploads` directory

## Swagger API Documentation

After deployment, you can access the migration endpoint in Swagger UI:

**URL:** `http://localhost:8080/swagger-ui.html`

**Navigate to:** Data Migration > POST /api/admin/migration/chapter-file-paths

You can test the migration directly from Swagger UI with your admin JWT token.

---

**Last Updated:** October 17, 2025  
**Version:** 1.0  
**Author:** Development Team

