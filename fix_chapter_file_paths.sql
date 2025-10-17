-- Fix Chapter Uploaded File Paths
-- This script converts absolute file paths to relative paths for proper static resource serving
-- Run this script to fix existing data after deploying the code fix

-- Update file paths that contain absolute Windows-style paths
UPDATE chapter_uploaded_files
SET file_path = 'uploads/chapters/' || 
                SUBSTRING(file_path FROM 'chapters/(.*)')
WHERE file_path LIKE '%chapters/%' 
  AND file_path NOT LIKE 'uploads/chapters/%'
  AND file_path LIKE '%:%';  -- Contains drive letter (absolute path)

-- Update file paths that contain absolute Unix-style paths
UPDATE chapter_uploaded_files
SET file_path = 'uploads/chapters/' || 
                SUBSTRING(file_path FROM 'chapters/(.*)')
WHERE file_path LIKE '%chapters/%' 
  AND file_path NOT LIKE 'uploads/chapters/%'
  AND file_path LIKE '/%';  -- Starts with / (absolute path)

-- Verify the fix
SELECT id, file_name, file_path 
FROM chapter_uploaded_files 
WHERE file_path IS NOT NULL
ORDER BY id;

