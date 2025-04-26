import java.io.File;
/**
 * A scouter thread This thread lists all sub-directories from a given root path. Each sub-directory is enqueued to be searched for files by Searcher threads.
 */

public class Scouter implements Runnable {
    private final SynchronizedQueue<File> directoryQueue;
    private final File root;

    /**
     * Constructor. Initializes the scouter with a queue for the directories to be searched and a root directory to start from.
     * 
     * @param directoryQueue A queue for directories to be searched
     * @param root Root directory to start from
     */
    public Scouter(SynchronizedQueue<File> directoryQueue, File root) {
        this.directoryQueue = directoryQueue;
        this.root = root;
    }

    /**
     * Starts the scouter thread. Lists directories under root directory and adds them to queue, then lists directories in the next level and enqueues them and so on. 
     * This method begins by registering to the directory queue as a producer and when finishes, it unregisters from it.
     * 
     */
    @Override
    public void run() {
        try {
            // Register as a producer before starting to enqueue directories
            directoryQueue.registerProducer();
            // Enqueue directories starting from the root
            enqueueDirectories(root);
        } catch (Exception e) {
            System.err.println("Scouter encountered an error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Unregister as a producer after finishing
            directoryQueue.unregisterProducer();
        }
    }

    /**
     * Enqueues directories found under the specified root directory.
     * 
     * @param directory The root directory to start traversing from
     */
    private void enqueueDirectories(File directory) {
        if (directory != null && directory.isDirectory()) {
            // Enqueue the current directory
            directoryQueue.enqueue(directory);

            // List subdirectories and enqueue them
            File[] subDirs = directory.listFiles(file -> file.isDirectory());
            if (subDirs != null) {
                for (File subDir : subDirs) {
                    enqueueDirectories(subDir);
                }
            }
        }
    }
}