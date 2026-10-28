import java.io.File;
import java.io.IOException;
import java.util.*;

public class FileOrganizerSystem {

    // Scanner for terminal input
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   AUTOMATED FILE ORGANIZATION & DUPLICATE TOOL   ");
        System.out.println("==================================================");

        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Organize Files by Extension");
            System.out.println("2. Detect Duplicate Files (by Size)");
            System.out.println("3. Search for a File");
            System.out.println("4. Exit");
            System.out.print("Enter your choice (1-4): ");

            String choiceInput = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(choiceInput);
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a number between 1 and 4.");
                continue;
            }

            switch (choice) {
                case 1:
                    handleOrganization();
                    break;
                case 2:
                    handleDuplicateDetection();
                    break;
                case 3:
                    handleFileSearch();
                    break;
                case 4:
                    System.out.println("Exiting program. Goodbye!");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("❌ Invalid choice. Please choose between 1 and 4.");
            }
        }
    }

    /**
     * 1. FILE ORGANIZATION LOGIC (Classic java.io.File & renameTo)
     */
    private static void handleOrganization() {
        System.out.print("\nEnter the directory path to organize (e.g., C:/Users/Name/Desktop/Test): ");
        String dirPathStr = scanner.nextLine().trim();
        File dir = new File(dirPathStr);

        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("❌ Error: Provided path is not a valid directory.");
            return;
        }

        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("⚠️ The directory is empty.");
            return;
        }

        int count = 0;
        for (File file : files) {
            // Process only regular files, skip folders
            if (file.isFile()) {
                String fileName = file.getName();
                String ext = getFileExtension(fileName);

                // Determine category folder based on extension
                String folderName = "Others";
                if (ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("gif")) {
                    folderName = "Images";
                } else if (ext.equals("pdf") || ext.equals("txt") || ext.equals("docx") || ext.equals("pptx") || ext.equals("xlsx")) {
                    folderName = "Documents";
                } else if (ext.equals("java") || ext.equals("py") || ext.equals("cpp") || ext.equals("c") || ext.equals("html")) {
                    folderName = "Code";
                } else if (ext.equals("mp4") || ext.equals("mkv")) {
                    folderName = "Videos";
                } else if (ext.equals("mp3") || ext.equals("wav")) {
                    folderName = "Audio";
                } else if (ext.equals("zip") || ext.equals("rar")) {
                    folderName = "Archives";
                }

                // Create category folder if it does not exist
                File targetFolder = new File(dir, folderName);
                if (!targetFolder.exists()) {
                    targetFolder.mkdir();
                    System.out.println("   [CREATE DIR] Created folder: " + folderName);
                }

                // Move file to destination folder
                File destFile = new File(targetFolder, fileName);
                if (file.renameTo(destFile)) {
                    System.out.println("   [MOVE] " + fileName + " ---> " + folderName + "/");
                    count++;
                } else {
                    System.out.println("   [FAILED] Could not move: " + fileName);
                }
            }
        }
        System.out.println("✅ Success! Total files organized: " + count);
    }

    /**
     * 2. DUPLICATE DETECTION LOGIC (Size-based using HashMap)
     */
    private static void handleDuplicateDetection() {
        System.out.print("\nEnter the directory path to check for duplicates: ");
        String dirPathStr = scanner.nextLine().trim();
        File dir = new File(dirPathStr);

        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("❌ Error: Provided path is not a valid directory.");
            return;
        }

        // HashMap to group files by size (Key: File Size in bytes, Value: List of Files)
        Map<Long, List<File>> sizeMap = new HashMap<>();

        // Collect all files recursively (or in this directory)
        List<File> allFiles = new ArrayList<>();
        collectFilesRecursive(dir, allFiles);

        for (File file : allFiles) {
            long fileSize = file.length(); // Get file size in bytes
            sizeMap.putIfAbsent(fileSize, new ArrayList<>());
            sizeMap.get(fileSize).add(file);
        }

        boolean duplicatesFound = false;
        System.out.println("\n--- DUPLICATE SCAN RESULTS (By File Size) ---");
        
        for (Map.Entry<Long, List<File>> entry : sizeMap.entrySet()) {
            List<File> fileList = entry.getValue();
            // If more than one file shares the exact same size, flag them
            if (fileList.size() > 1) {
                duplicatesFound = true;
                System.out.println("\n⚠️ Potential Duplicates Found (Size: " + entry.getKey() + " bytes):");
                for (File f : fileList) {
                    System.out.println("   - " + f.getAbsolutePath());
                }
            }
        }

        if (!duplicatesFound) {
            System.out.println("✨ No duplicate files found!");
        } else {
            System.out.println("\n✅ Duplicate scan complete.");
        }
    }

    /**
     * 3. FILE SEARCH LOGIC
     */
    private static void handleFileSearch() {
        System.out.print("\nEnter the directory path to search in: ");
        String dirPathStr = scanner.nextLine().trim();
        File dir = new File(dirPathStr);

        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("❌ Error: Provided path is not a valid directory.");
            return;
        }

        System.out.print("Enter file name or keyword to search for: ");
        String keyword = scanner.nextLine().trim().toLowerCase();

        List<File> allFiles = new ArrayList<>();
        collectFilesRecursive(dir, allFiles);

        int matchCount = 0;
        System.out.println("\n--- SEARCH RESULTS ---");
        for (File file : allFiles) {
            if (file.getName().toLowerCase().contains(keyword)) {
                System.out.println("   [MATCH] " + file.getAbsolutePath());
                matchCount++;
            }
        }

        if (matchCount == 0) {
            System.out.println("❌ No matching files found.");
        } else {
            System.out.println("\n✅ Found " + matchCount + " matching file(s).");
        }
    }

    /**
     * Helper: Safely extracts file extension
     */
    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * Helper: Recursively collects all files in a folder and its subfolders
     */
    private static void collectFilesRecursive(File dir, List<File> fileList) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                // Skip our own category folders so we don't re-organize sorted files
                String name = file.getName();
                if (!name.equals("Images") && !name.equals("Documents") && 
                    !name.equals("Code") && !name.equals("Videos") && 
                    !name.equals("Audio") && !name.equals("Archives") && !name.equals("Others")) {
                    collectFilesRecursive(file, fileList);
                }
            } else if (file.isFile()) {
                fileList.add(file);
            }
        }
    }
}
