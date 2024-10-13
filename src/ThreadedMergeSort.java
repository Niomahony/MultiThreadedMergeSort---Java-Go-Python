import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThreadedMergeSort extends Thread {
    private Integer[] array;
    private int begin, end;

    public ThreadedMergeSort(Integer[] array, int begin, int end) {
        this.array = array;
        this.begin = begin;
        this.end = end;
    }

    @Override
    public void run() {
        int threadThreshold = array.length / (2 * Runtime.getRuntime().availableProcessors());
        mergeSort(array, begin, end, threadThreshold);
    }

    public static void mergeSort(Integer[] array, int begin, int end, int threadThreshold) {
        if (begin < end) {
            int mid = (begin + end) / 2;

            if ((end - begin) > threadThreshold) {
                ThreadedMergeSort leftSort = new ThreadedMergeSort(array, begin, mid);
                ThreadedMergeSort rightSort = new ThreadedMergeSort(array, mid + 1, end);

                leftSort.start();
                rightSort.start();

                try {
                    leftSort.join();
                    rightSort.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                mergeSort(array, begin, mid, threadThreshold);
                mergeSort(array, mid + 1, end, threadThreshold);
            }

            merge(array, begin, mid, end);
        }
    }

    public static void merge(Integer[] array, int begin, int mid, int end) {
        Integer[] temp = new Integer[(end - begin) + 1];

        int i = begin, j = mid + 1;
        int k = 0;

        while (i <= mid && j <= end) {
            if (array[i] <= array[j]) {
                temp[k] = array[i];
                i += 1;
            } else {
                temp[k] = array[j];
                j += 1;
            }
            k += 1;
        }

        while (i <= mid) {
            temp[k] = array[i];
            i += 1;
            k += 1;
        }

        while (j <= end) {
            temp[k] = array[j];
            j += 1;
            k += 1;
        }

        for (i = begin, k = 0; i <= end; i++, k++) {
            array[i] = temp[k];
        }
    }

    public static Integer[] readNumbersFromFile(String filename) throws IOException {
        List<Integer> numbers = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] numberStrings = line.split(", ");
            for (String numberStr : numberStrings) {
                numbers.add(Integer.parseInt(numberStr));
            }
        }
        reader.close();

        return numbers.toArray(new Integer[0]);
    }

    public static void main(String[] args) {
        try {
            String filepath = "C:/Users/omaho/Downloads//random_100000_numbers.txt";
            Integer[] array = readNumbersFromFile(filepath);

            System.out.println("Unsorted array sample: " + Arrays.toString(Arrays.copyOf(array, 100)));

            long startTime = System.currentTimeMillis();

            ThreadedMergeSort sorter = new ThreadedMergeSort(array, 0, array.length - 1);
            sorter.start();

            try {
                sorter.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Sorted array sample: " + Arrays.toString(Arrays.copyOf(array, 100)));
            System.out.println("Multithreaded merge sort took: " + duration + " milliseconds");

        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
    }
}