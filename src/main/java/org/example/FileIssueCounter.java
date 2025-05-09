package org.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileIssueCounter {

    private static int javaFileCount = 0;
    private static int issueCount = 0;

    private static final List<String> javaFileList = new ArrayList<>();
    private static final List<String> issueFileList = new ArrayList<>();
    private static final List<Integer> issueCountList = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        File directory = null;

        while (directory == null) {
            System.out.print("Enter the path to the directory (or type 'quit' to exit): ");
            String inputPath = scanner.nextLine().trim();

            if (inputPath.equalsIgnoreCase("quit")) {
                System.out.println("Program terminated.");
                scanner.close();
                return;
            }

            directory = new File(inputPath);
            if (!directory.isDirectory()) {
                System.out.println("Invalid directory! Please try again.");
                directory = null;
            }
        }
        scanner.close();

        File finalDirectory = directory;
        Thread scanThread = new Thread(() -> scanDirectory(finalDirectory));
        System.out.println("Scanning Java files...");
        scanThread.start();

        try {
            scanThread.join();
        } catch (InterruptedException e) {
            System.out.println("Scanning was interrupted.");
        }

        System.out.println("\nSummary:");
        if (javaFileCount == 0) {
            System.out.println("No Java files found.");
        } else {
            System.out.println("Number of Java Files = " + javaFileCount);
            System.out.println("Java Files:");
            for (int i = 0; i < javaFileList.size(); i++) {
                System.out.println(" - " + javaFileList.get(i));
            }
        }

        System.out.println("\nTotal Number of Issues = " + issueCount);
        if (issueCount == 0) {
            System.out.println("No issues found.");
        } else {
            System.out.println("Files with Issues:");
            for (int i = 0; i < issueFileList.size(); i++) {
                System.out.println(" - " + issueFileList.get(i) + " has " + issueCountList.get(i) + " issue(s)");
            }
        }
    }

    private static void scanDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                scanDirectory(file);
            } else if (file.isFile() && file.getName().endsWith(".java")) {
                javaFileCount++;
                javaFileList.add(file.getName());

                try {
                    List<String> lines = Files.readAllLines(file.toPath());
                    int fileIssueCount = 0;

                    for (String line : lines) {
                        String trimmed = line.trim().toUpperCase();

                        if (trimmed.startsWith("// SOLVED") ||
                                trimmed.startsWith("// TODO") ||
                                trimmed.startsWith("// FIXME") ||
                                trimmed.startsWith("// BUG") ||
                                trimmed.startsWith("// HACK")) {
                            fileIssueCount++;
                        }
                    }

                    if (fileIssueCount > 0) {
                        issueCount += fileIssueCount;
                        issueFileList.add(file.getName());
                        issueCountList.add(fileIssueCount);
                    }

                } catch (IOException e) {
                    System.out.println("Failed to read file: " + file.getName());
                }
            }
        }
    }
}
git add .
