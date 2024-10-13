# Java, Go or Python

# - Which language to multithread with?

This article serves to provide insight into the basics of multithreading, explore the process of
multithreading with Java, Go and Python and help you decide which language to write your first
multithreading programs in. The main goal of this article is to help you make this decision and
does not serve as an accurate benchmark for Java, Go or Python multithreading speeds.

## What is a thread and what is a process?

```
A process is in essence, a program in execution and
represents an instance of a running application.
Processes run independently and are isolated from
other processes. Every process is allocated its own
memory space
```
```
Within each process, there can be one or more
threads. Threads are sometimes referred to as
“lightweight processes” because they exist within
processes, share the processes memory and
resources. Unlike processes, threads can interact with
each other and executes different parts of the overall
processes tasks.
```
## What is multithreading?

Most people are familiar with sequential programming, where a program completes one task
before starting the next. Parallel programming allows a computer to perform multiple tasks at
once. Think of sequential programming as a single mechanic changing the wheels on a car. The
mechanic must remove and replace each wheel one by one. Parallel programming, however, is
like a Formula 1 pit stop, where multiple mechanics work on each wheel at the same time - one
team removes the old wheels while another team installs the new ones. This speeds up the
whole process.

Multithreading allows your computer's processor to execute multiple threads simultaneously.
The number of threads that can run at the same time is limited by the number of CPU cores.
Returning to the F1 example, if the pit crew only had eight mechanics instead of twelve, some
mechanics would have to do both tasks - removing and replacing the tires - slowing down the
process.

```
Figure 1 - Source - Javatpoint
```

## What is merge sort and how can we multithread it?

Merge sort implements the divide and conquer technique. It takes an array and recursively
divides the array into smaller subarrays and sorts these subarrays. The steps are as follows:
Divide the list recursively into two halves until it can no longer be divided. Sort: Each subarray is
sorted individually. Merge: The sorted subarrays are merged back together in the sorted order,
and this continues until all elements from both subarrays are merged. i

The process I followed to use multithreading with merge sort is as follows. When creating a
subarray, if the size of the array is larger than a specified threshold, two new threads are created
to sort the left side and right side of the array. The threshold is used to avoid creating excessive
amounts of threads and is calculated by taking the array size and dividing it by the cores
multiplied by two. Once the two halves are sorted, they are merged back into a sorted array. The
merging method is single threaded but only occurs after each thread completes sorting its half
of the array. This can be optimised, however that is not the goal of this article.

## How to multithread in each language and implementations

To compare the process of multithreading with Java, Go and Python, I have written this
algorithm and utilised each language’s implementation of multithreading. Each program sorts
the same array of 100,000 numbers and ran on my personal machine which has an 11th
generation Intel i5 1135G7, 2.4GHz 8 Core CPU. I ran the programs 10 times; calculated the
length of time it took to perform the sorting operation and calculated the average runtime.

The full source code for each program is available on my GitHub:
https://github.com/Niomahony/MultiThreadedMergeSort---Java-Go-Python

## Java

When multithreading with Java, you can either extend the Thread class or implement the
Runnable interface on your class. In my example below, I have extended Thread. Because Java
does not allow multiple inheritance, you cannot extend any other classes if you extend thread
but does allow you to use more Thread inbuilt methods. ii

The process for creating a multithreaded merge sort with Java was as follows:

- Create a constructor to hold the data for each threaded merge
- Create two objects for each half of the array
- Assign each object to a thread
- Wait for each merge sort to complete
- Merge the sorted halves

### Java implementation iii

The threading portion of this code was very quick to set up. By overlooking the merge sort
functionality, basic implementation of multithreading in java is quite simple for this task. We
begin by using the start method on our leftSort and rightSort objects to initialise the threads.

leftSort.start();
rightSort.start();


Then use the join method. The join method is incredibly important in this scenario, as it forces
the mergeSort method to wait for both threads to complete their respective tasks before
proceeding to complete the final merge. If we did not implement the join method, this would
cause incorrect sorting as one thread may not have completed sorting before merging.

leftSort.join();
rightSort.join();

On average, this code would perform the sorting of 100,000 numbers in 34 milliseconds.

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
int threadThreshold = array.length / ( 2 * Runtime. _getRuntime_ ().availableProcessors());
_mergeSort_ (array, begin, end, threadThreshold);
}

public static void mergeSort(Integer[] array, int begin, int end, int threadThreshold) {
if (begin < end) {
int mid = (begin + end) / 2 ;

if ((end - begin) > threadThreshold) {
ThreadedMergeSort leftSort = new ThreadedMergeSort(array, begin, mid);
ThreadedMergeSort rightSort = new ThreadedMergeSort(array, mid + 1 , end);

leftSort.start();
rightSort.start();

try {
leftSort.join();
rightSort.join();
} catch (InterruptedException e) {
e.printStackTrace();
}
} else {
_mergeSort_ (array, begin, mid, threadThreshold);
_mergeSort_ (array, mid + 1 , end, threadThreshold);
}

_merge_ (array, begin, mid, end);
}
}

## Golang

Go was originally developed due to the creator’s dislike of C++. Go has very readable code and
static typing, making it a great choice for those familiar with Java or C++ for implementing
multithreading. Although it does not outperform C, it is preferable to many due to the shorter
development times due to the lower barrier to entry that it has. iv

Go makes multithreading extremely simple with its Goroutines. A Goroutine is a lightweight
thread and can be initialised with the go keyword. You will see that the code for multithreading
in Go is much shorter than Java. v


### Golang Implementation

First, I initialise the sync.WaitGroup and add 2 to its counter. This is because I will have two
Goroutines, one for each half of the array. The WaitGroup is used to track when the Goroutines
have finished, once the WaitGroup wg’s counter is reduced to 0, the program is allowed to
continue from the wg.Wait() function onward.

var wg sync.WaitGroup
wg.Add( 2 )

Now, I initialise the Goroutines which will be concurrently handling the task of sorting the array.
The defer wg.Done() will reduce the WaitGroup’s counter by 1, letting the WaitGroup know that it
has completed its task.

go func() {
defer wg.Done()
mergeSort(array, begin, mid)
}()
go func() {
defer wg.Done()
mergeSort(array, mid+ 1 , end)
Go is incredibly light-weight, fast, and simple to set up multithreading with. It is in my opinion,
ideal for learning multithreading concepts.

Overall, Go was able to sort 100,000 numbers on average in 8 milliseconds.

func mergeSort(array []int, begin, end int) {
if begin < end {
mid := (begin + end) / 2

if (end - begin) > ThreadThreshold {
var wg sync.WaitGroup
wg.Add( 2 )

go func() {
defer wg.Done()
mergeSort(array, begin, mid)
}()
go func() {
defer wg.Done()
mergeSort(array, mid+ 1 , end)
}()
wg.Wait()
} else {
mergeSort(array, begin, mid)
mergeSort(array, mid+ 1 , end)
}

merge(array, begin, mid, end)
}
}

## Python

While python supports multithreading through its threading module, the fact of the matter is
that python cannot perform parallelised multithreading due to the Global Interpreter Lock (GIL).


Before writing the code for this article, I did not expect that Python would be the slowest
language for multithreading. It is important to note the difference between parallel processes
and parallel multithreading. The GIL prevents threads contained within one process from
executing Python bytecode at the same time, causing the issue with parallel multithreading. To
avoid the GIL issue, we can instead use parallel processing, however that is not the target for
this article. Additionally, we can use a different python interpreter to run our program, but as the
target demographic for this article is users who may be setting up multithreading for the first
time, I decided against changing the interpreter and following the same algorithm that Java and
Golang used. vi

Something to note is that Python does not have this same limitation on Input/Output (I/O) tasks.
If you plan on using threads to parallelise I/O tasks, Python may be a great choice.

### Python Implementation

To create new threads, I use executor.submit() with the parameters for the functions they will
run.

future1 = executor.submit(merge_sort, array, begin, mid, THREAD_THRESHOLD,
executor)
future2 = executor.submit(merge_sort, array, mid + 1 , end,
THREAD_THRESHOLD, executor)
After creating the threads, similarly to the other languages, we use .result() to wait for the
threads to complete their tasks before proceeding with the rest of the merge.

future1.result()
future2.result()

When running in main, we call the ThreadPoolExecutor() with the following line:

with ThreadPoolExecutor() as executor:
merge_sort(array, 0 , len(array) - 1 , THREAD_THRESHOLD, executor)
Due to the limitations of the GIL in Python’s default interpreter, Python averaged a sorting time
of 360ms. This is due to the fact that it is not truly running in parallel due to the GIL lock. This
displays the tremendous efficiency of parallelising your code.

from concurrent.futures import ThreadPoolExecutor
import time
import os

def merge_sort(array, begin, end, THREAD_THRESHOLD, executor):
if begin < end:
mid = (begin + end) // 2

if (end - begin) > THREAD_THRESHOLD:

future1 = executor.submit(merge_sort, array, begin, mid,
THREAD_THRESHOLD, executor)
future2 = executor.submit(merge_sort, array, mid + 1 , end,
THREAD_THRESHOLD, executor)

future1.result()
future2.result()
else:
merge_sort(array, begin, mid, THREAD_THRESHOLD, executor)


merge_sort(array, mid + 1 , end, THREAD_THRESHOLD, executor)

merge(array, begin, mid, end)

## Conclusion:

From this analysis into multithreading, Java, Go and Python, you have learned the basics of
multithreading and techniques for setting up multithreading in each language. Every language
has its own unique advantages and challenges and comparing them can be challenging.

Java has an established, robust multithreading model with its Thread class and Runnable
interface. Implementing multithreading is as simple as extending a class and calling its
methods within your code. The main issue with multithreading in Java, is that you do it in Java.
While multithreading is easy to complete, Java can be slower to develop multithreading
programs in due to more convoluted syntax with the rest of your program and the requirement
for more boilerplate code.

Python, despite its limitations with the GIL, remains a popular choice for I/O tasks where
multithreading can still apply. Unfortunately, for our purposes within this article, it cannot
achieve parallel multithreading without changing the interpreter.

Go’s lightweight Goroutines stand out as a clear winner in my opinion as they are easy to set up,
easy to manage and can output incredibly fast sorting speeds.

```
Language Average Sort Speed
Java 34 ms
Golang 8ms
Python 360ms
```
Ultimately, the decision will depend on your specific circumstances. If you are comfortable
writing code in Java, setting up multithreading is a simple process. If you are performing I/O
tasks, I would look towards Python. For overall ease of use and performance, Golang is perfect
for building rapid multithreading programs.

i https://www.geeksforgeeks.org/merge-sort/#illustration-of-merge-sort

ii https://www.geeksforgeeks.org/multithreading-in-java/

iii https://www.javatpoint.com/multithreading-in-java

iv https://medium.com/geekculture/learn-go-part- 1 - the-beginning-723746f2e8b

v https://medium.com/@denniswon/goroutines-and-channels-golang-md-57dbb76d8bed

vi https://xantygc.medium.com/python-and-the-never-ending-history-about-multithreading-

15b625d561cb


