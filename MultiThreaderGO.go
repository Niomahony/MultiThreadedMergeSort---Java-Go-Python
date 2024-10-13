package main

import (
	"fmt"
	"os"
	"runtime"
	"strconv"
	"strings"
	"sync"
	"time"
)

var ThreadThreshold int

func mergeSort(array []int, begin, end int) {
	if begin < end {
		mid := (begin + end) / 2

		if (end - begin) > ThreadThreshold {
			var wg sync.WaitGroup
			wg.Add(2)

			go func() {
				defer wg.Done()
				mergeSort(array, begin, mid)
			}()
			go func() {
				defer wg.Done()
				mergeSort(array, mid+1, end)
			}()
			wg.Wait()
		} else {
			mergeSort(array, begin, mid)
			mergeSort(array, mid+1, end)
		}

		merge(array, begin, mid, end)
	}
}

func merge(array []int, begin, mid, end int) {
	temp := make([]int, end-begin+1)

	i, j, k := begin, mid+1, 0

	for i <= mid && j <= end {
		if array[i] <= array[j] {
			temp[k] = array[i]
			i++
		} else {
			temp[k] = array[j]
			j++
		}
		k++
	}

	for i <= mid {
		temp[k] = array[i]
		i++
		k++
	}

	for j <= end {
		temp[k] = array[j]
		j++
		k++
	}

	for i := 0; i < len(temp); i++ {
		array[begin+i] = temp[i]
	}
}

func readNumbersFromTXT(filename string) ([]int, error) {
	file, err := os.ReadFile(filename)
	if err != nil {
		return nil, err
	}

	content := string(file)
	strNumbers := strings.Split(content, ",")

	var numbers []int

	for _, str := range strNumbers {
		num, err := strconv.Atoi(strings.TrimSpace(str))
		if err != nil {
			return nil, err
		}
		numbers = append(numbers, num)
	}

	return numbers, nil
}

func main() {
	filepath := "C:/Users/omaho/Downloads/random_100000_numbers.txt"

	array, err := readNumbersFromTXT(filepath)
	if err != nil {
		fmt.Println("Error reading the text file:", err)
		return
	}

	if len(array) == 0 {
		fmt.Println("The file was read, but no valid numbers were found.")
		return
	}

	fmt.Println("Unsorted array sample:", array[:min(100, len(array))])

	startTime := time.Now()

	ThreadThreshold = len(array) / (100 * runtime.NumCPU())
	fmt.Printf("Using a dynamic THREAD_THRESHOLD of %d\n", ThreadThreshold)

	mergeSort(array, 0, len(array)-1)

	duration := time.Since(startTime).Milliseconds()

	fmt.Println("Sorted array sample:", array[:min(100, len(array))])
	fmt.Printf("Multithreaded merge sort took: %d milliseconds\n", duration)
}

// Helper function to return the smaller of two integers
func min(a, b int) int {
	if a < b {
		return a
	}
	return b
}
