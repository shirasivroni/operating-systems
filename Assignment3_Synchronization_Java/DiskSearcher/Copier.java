import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/*
 * A copier thread. Reads files to copy from a queue and copies them to the given destination.
 */
public class Copier implements Runnable {
    public static final int COPY_BUFFER_SIZE = 4096; // Size of buffer used for a single file copy process
    private final File destination;
    private final SynchronizedQueue<File> resultsQueue;

    /**
     * Constructor. Initializes the worker with a destination directory and a queue of files to copy.
     * @param destination Destination directory
     * @param resultsQueue Queue of files found, to be copied
     * 
     */
    public Copier(File destination, SynchronizedQueue<File> resultsQueue) {
        this.destination = destination;
        this.resultsQueue = resultsQueue;
    }

    /**
     * Runs the copier thread. 
     * Thread will fetch files from queue and copy them, one after each other, to the destination directory. 
     * When the queue has no more files, the thread finishes.
     */
    @Override    
    public void run() {
        while (true) {
            // Dequeue a file to copy
            File file = resultsQueue.dequeue();
            if (file == null) {
                break; // No more files to process
            }

            // Copy the file to the destination directory
            File destFile = new File(destination, file.getName());
            destFile = changeIfFileExists(destFile);
            FileInputStream fis = null;
            FileOutputStream fos = null;
            try {
                fis = new FileInputStream(file);
                fos = new FileOutputStream(destFile);
                byte[] buffer = new byte[COPY_BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                System.err.println("Failed to copy file: " + file.getAbsolutePath());
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        System.err.println("Failed to close input stream for file: " + file.getAbsolutePath());
                        e.printStackTrace();
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        System.err.println("Failed to close output stream for file: " + destFile.getAbsolutePath());
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Changes the file name if a file with the same name already exists in the destination directory.
     * 
     * @param file The original file
     * @return A File object with the new name
     */    

    private File changeIfFileExists(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");
        String baseName = fileName;
        String extension = "";
        if (dotIndex > 0) {
            baseName = fileName.substring(0, dotIndex);
            extension = fileName.substring(dotIndex);
        }
        int counter = 1;
        while (file.exists()) {
            String newName = baseName + "(" + counter + ")" + extension;
            file = new File(file.getParent(), newName);
            counter++;
        }
        return file;
    }
}

