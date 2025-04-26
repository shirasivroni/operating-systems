import java.io.File;

/**
 * A searcher thread. Searches for files containing a given pattern and that end with a specific extension in all directories listed in a directory queue.
 */

public class Searcher implements Runnable {
    private final String pattern;
    private final String extension;
    private final SynchronizedQueue<File> directoryQueue;
    private final SynchronizedQueue<File> resultsQueue;

    /**
     * Constructor. Initializes the searcher thread.
     * 
     * @param pattern Pattern to look for
     * @param extension Wanted extension
     * @param directoryQueue A queue with directories to search in (as listed by the scouter)
     * @param resultsQueue A queue for files found (to be copied by a copier)
     * 
     */
    public Searcher(String pattern, String extension, SynchronizedQueue<File> directoryQueue, SynchronizedQueue<File> resultsQueue) {
        this.pattern = pattern;
        this.extension = extension;
        this.directoryQueue = directoryQueue;
        this.resultsQueue = resultsQueue;
    }
    
    /**
     * Runs the searcher thread. 
     * Thread will fetch a directory to search in from the directory queue, then search all files inside it (but will not recursively search subdirectories!). 
     * Files that a contain the pattern and have the wanted extension are enqueued to the results queue. 
     * This method begins by registering to the results queue as a producer and when finishes, it unregisters from it.
     * 
     */
    @Override
    public void run() {
        try {
            // Register as a producer before starting to enqueue directories
            resultsQueue.registerProducer();
            while (true) {
                // Dequeue a directory to search
                File directory = directoryQueue.dequeue();
                if (directory == null) {
                    break; // No more directories to process
                }

                // List all files in the directory
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        // Check if the file name contains the pattern and has the correct extension.
                        if (file.isFile() && file.getName().contains(pattern) && file.getName().endsWith(extension)) {
                            resultsQueue.enqueue(file);

                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Searcher encountered an error: " + e.getMessage());
            e.printStackTrace();

        } finally {
            // Unregister as a producer after finishing
            resultsQueue.unregisterProducer();
        }
    }
}

