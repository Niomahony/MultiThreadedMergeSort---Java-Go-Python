from concurrent.futures import ThreadPoolExecutor
import time
import os


def merge_sort(array, begin, end, THREAD_THRESHOLD, executor):
    if begin < end:
        mid = (begin + end) // 2

        if (end - begin) > THREAD_THRESHOLD:

            future1 = executor.submit(merge_sort, array, begin, mid, THREAD_THRESHOLD, executor)
            future2 = executor.submit(merge_sort, array, mid + 1, end, THREAD_THRESHOLD, executor)

            future1.result()
            future2.result()
        else:
            merge_sort(array, begin, mid, THREAD_THRESHOLD, executor)
            merge_sort(array, mid + 1, end, THREAD_THRESHOLD, executor)

        merge(array, begin, mid, end)


def merge(array, begin, mid, end):
    temp = []
    i, j = begin, mid + 1

    while i <= mid and j <= end:
        if array[i] <= array[j]:
            temp.append(array[i])
            i += 1
        else:
            temp.append(array[j])
            j += 1

    while i <= mid:
        temp.append(array[i])
        i += 1

    while j <= end:
        temp.append(array[j])
        j += 1

    for i in range(len(temp)):
        array[begin + i] = temp[i]


def read_numbers_from_file(filename):
    with open(filename, 'r') as file:
        numbers = file.read().split(', ')
    return [int(number) for number in numbers]


if __name__ == "__main__":
    filepath = "C:/Users/omaho/Downloads/random_100000_numbers.txt"

    try:
        array = read_numbers_from_file(filepath)
        print("Unsorted array sample:", array[:100])

        start_time = time.time() * 1000

        THREAD_THRESHOLD = len(array) // (2 * os.cpu_count())

        with ThreadPoolExecutor() as executor:
            merge_sort(array, 0, len(array) - 1, THREAD_THRESHOLD, executor)

        end_time = time.time() * 1000
        duration = end_time - start_time

        print(len(array))
        print("Sorted array sample:", array[:100])
        print("Multithreaded merge sort took:", duration, "milliseconds")

    except IOError as e:
        print("Error reading the file:", e)
