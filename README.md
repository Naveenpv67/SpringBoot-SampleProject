import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RecursiveLineCounterWithTotal {
    private static int totalFileCount = 0;

    public static void main(String[] args) {
        // Replace "/path/to/folder" with the actual path of the folder you want to analyze
        String folderPath = "/path/to/folder";

        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            countLinesInFiles(folder);
            System.out.println("Total files: " + totalFileCount);
        } else {
            System.out.println("Specified folder does not exist or is not a directory.");
        }
    }

    private static void countLinesInFiles(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    try {
                        long lineCount = Files.lines(Paths.get(file.getAbsolutePath())).count();
                        System.out.println("File: " + file.getName() + ", Lines: " + lineCount);
                        totalFileCount++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (file.isDirectory()) {
                    countLinesInFiles(file); // Recursive call for subfolder
                }
            }
        } else {
            System.out.println("No files found in the specified folder.");
        }
    }
}
