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

type Signed interface {
	~int | ~int8 | ~int16 | ~int32 | ~int64
}
type Unsigned interface {
	~uint | ~uint8 | ~uint16 | ~uint32 | ~uint64 | ~uintptr
}
type Integer interface {
	Signed | Unsigned
}

func Sum[T Integer](xs []T) T {
	sum := T(0)
	for _, x := range xs {
		sum += x
	}
	return sum
}

func Mult[T Integer](xs []T) T {
	mult := T(1)
	for _, x := range xs {
		mult *= x
	}
	return mult
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

type Range[T Integer] struct {
	Start   T
	EndExcl T
}

func NewRange[T Integer](a, b T) Range[T] {
	if a > b {
		log.Fatalf("error NewRange - a must be <= b (%v, %v)", a, b)
	}
	return Range[T]{a, b}
}

func EmptyRange[T Integer]() Range[T] {
	return NewRange(T(0), T(0))
}

func Overlaps[T Integer](a Range[T], b Range[T]) bool {
	return !(a.EndExcl <= b.Start || a.Start >= b.EndExcl)
}
