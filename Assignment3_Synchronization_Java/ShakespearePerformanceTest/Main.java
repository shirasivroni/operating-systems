import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Main {
    
    private static String FILE_PATH = "C:\\Temp\\Shakespeare.txt";

    public static void main(String[] args) {

        List<String> lines = getLinesFromFile();
        System.out.println("Number of lines found: " + lines.size());
        System.out.println("Starting to process");

        long startTimeWithoutThreads = System.currentTimeMillis();
        workWithoutThreads(lines);
        long elapsedTimeWithoutThreads = (System.currentTimeMillis() - startTimeWithoutThreads);
        System.out.println("Execution time: " + elapsedTimeWithoutThreads);


        long startTimeWithThreads = System.currentTimeMillis();
        workWithThreads(lines);
        long elapsedTimeWithThreads = (System.currentTimeMillis() - startTimeWithThreads);
        System.out.println("Execution time:" + elapsedTimeWithThreads);

    }

    private static void workWithThreads(List<String> lines) {

        //Get the number of available cores
        int numAvailableCores = Runtime.getRuntime().availableProcessors();

        //Assuming X is the number of cores - Partition the data into x data sets
        List<List<String>> partitions = new ArrayList<>();
        int partitionSize = (int) Math.ceil((double) lines.size() / numAvailableCores);

        for (int i = 0; i < lines.size(); i += partitionSize) {
            partitions.add(lines.subList(i, Math.min(i + partitionSize, lines.size())));
        }

        //Create a fixed thread pool of size X
        ExecutorService executorService = Executors.newFixedThreadPool(numAvailableCores);

        //Submit x workers to the thread pool
        for (List<String> partition : partitions) {
            executorService.submit(new Worker(partition));
        }

        //Wait for termination
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

    }

    private static void workWithoutThreads(List<String> lines) {
        Worker worker = new Worker(lines);
        worker.run();
    }

    private static List<String> getLinesFromFile() {
        List<String> lines = new ArrayList<>();
        try{
            lines = Files.readAllLines(Paths.get(FILE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
