package utils

import (
	"errors"
	"fmt"
	"log"
	"os"
	"time"
)

func PrintDuration(label string, start time.Time) {
	now := time.Now()
	fmt.Println(label, "took:", now.UnixMilli()-start.UnixMilli(), "ms", "(", now.UnixMicro()-start.UnixMicro(), "µs )")
}

func IntAbs(x int) int {
	if x < 0 {
		return -x
	}
	return x
}

func Mod(a, b int) int {
	return (a%b + b) % b
}

var EmptySliceError = errors.New("cannot get MaxIndex of empty slice")

func MaxIndex(xs []int) (int, error) {
	if len(xs) == 0 {
		return -1, EmptySliceError
	}
	maxVal := xs[0]
	maxIndex := 0
	for i, x := range xs {
		if x > maxVal {
			maxIndex = i
			maxVal = x
		}
	}
	return maxIndex, nil
}

func Sum(xs []int) int {
	sum := 0
	for _, x := range xs {
		sum += x
	}
	return sum
}

func PuzzleInput(filename string) string {
	content, err := os.ReadFile("./inputs/" + filename)
	if err != nil {
		log.Fatal(err)
	}

	return string(content)
}

func EqSlice[T comparable](xs []T, ys []T) bool {
	if len(xs) != len(ys) {
		return false
	}
	for i, x := range xs {
		if x != ys[i] {
			return false
		}
	}

	return true
}
