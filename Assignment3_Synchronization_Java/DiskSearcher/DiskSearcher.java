/*
 * Main application class. 
 * This application searches for all files under some given path that contain a given textual pattern. 
 * All files found are copied to some specific directory.
 */

import java.io.File;

public class DiskSearcher {
    public static final int DIRECTORY_QUEUE_CAPACITY = 50; // Capacity of the queue that holds the directories to be searched
    public static final int RESULTS_QUEUE_CAPACITY = 50; //Capacity of the queue that holds the files found

    /**
     * Main method. 
     * Reads arguments from command line and starts the search.
     * @param args Command line arguments
     **/
    public static void main(java.lang.String[] args) {
        if (args.length != 6) {
            System.err.println(
                    "Usage: java DiskSearcher <filename-pattern> <file-extension> <root directory> <destination directory> <# of searchers> <# of copiers>");
            System.exit(1);
        }
        
        String pattern = args[0];
        String extension = args[1];
        File root = new File(args[2]);
        File destination = new File(args[3]);
        int numSearchers = Integer.parseInt(args[4]);
        int numCopiers = Integer.parseInt(args[5]);

        // Validate input directories
        if (!root.isDirectory()) {
            System.err.println("Invalid root directory: " + root);
            System.exit(1);
        }

        if (!destination.isDirectory()) {
            if (destination.mkdirs()) {
                System.out.println("Destination directory created: " + destination);
            }
            else {
                System.err.println("Failed to create destination directory: " + destination);
                System.exit(1);
            }
        }
        // if (!destination.isDirectory()) {
        //     System.err.println("Invalid destination directory: " + destination);
        //     System.exit(1);
        // }

        // Initialize queues
        SynchronizedQueue<File> directoryQueue = new SynchronizedQueue<>(DIRECTORY_QUEUE_CAPACITY);
        SynchronizedQueue<File> resultsQueue = new SynchronizedQueue<>(RESULTS_QUEUE_CAPACITY);
        
        // Start scouter thread
        Thread scouterThread = new Thread(new Scouter(directoryQueue, root));
        scouterThread.start();

        // Start searcher threads
        Thread[] searcherThreads = new Thread[numSearchers];
        for (int i = 0; i < numSearchers; i++) {
            searcherThreads[i] = new Thread(new Searcher(pattern, extension, directoryQueue, resultsQueue));
            searcherThreads[i].start();
        }

        // Start copier threads
        Thread[] copierThreads = new Thread[numCopiers];
        for (int i = 0; i < numCopiers; i++) {
            copierThreads[i] = new Thread(new Copier(destination, resultsQueue));
            copierThreads[i].start();
        }

        // Wait for scouter to finish
        try {
            scouterThread.join();
        } catch (InterruptedException e) {
            System.err.println("Scouter thread interrupted");
            e.printStackTrace();
        }

        // Wait for searcher threads to finish
        for (Thread searcherThread : searcherThreads) {
            try {
                searcherThread.join();
            } catch (InterruptedException e) {
                System.err.println("Searcher thread interrupted");
                e.printStackTrace();
            }
        }

        // Wait for copier threads to finish
        for (Thread copierThread : copierThreads) {
            try {
                copierThread.join();
            } catch (InterruptedException e) {
                System.err.println("Copier thread interrupted");
                e.printStackTrace();
            }
        }
    }

}
