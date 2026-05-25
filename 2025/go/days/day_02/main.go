package main

import (
	"fmt"
	"strconv"
	"strings"
	"time"

	"adventofcode/utils"
)

const example = `11-22,95-115,998-1012,1188511880-1188511890,222220-222224,1698522-1698528,446443-446449,38593856-38593862,565653-565659,824824821-824824827,2121212118-2121212124`

type Range struct {
	Start uint64
	End   uint64
}

func parseRange(input string) Range {
	startEnd := strings.Split(input, "-")
	start, _ := strconv.Atoi(startEnd[0])
	end, _ := strconv.Atoi(startEnd[1])
	return Range{uint64(start), uint64(end)}
}

func parseInput(input string) []Range {
	rawRanges := strings.Split(input, ",")
	ranges := make([]Range, len(rawRanges))

	for i, rangeString := range rawRanges {
		ranges[i] = parseRange(rangeString)
	}

	return ranges
}

func allEqual(xs []string) bool {
	for _, x := range xs {
		if xs[0] != x {
			return false
		}
	}
	return true
}

func chunk(s string, size int) ([]string, bool) {
	chunks := make([]string, len(s)/size)
	ok := (len(s) % size) == 0

	for i := 0; i < len(chunks); i++ {
		if len(s) >= i*size+size {
			chunks[i] = s[i*size : i*size+size]
		}
	}

	return chunks, ok
}

func repeats(n uint64) bool {
	ns := strconv.FormatUint(n, 10)

	for i := 0; i < len(ns)/2; i++ {
		pattern := ns[0 : i+1]
		chunks, ok := chunk(ns, len(pattern))
		if !ok {
			continue
		}
		if allEqual(chunks) {
			return true
		}
	}
	return false
}

func repeatsTwice(n uint64) bool {
	ns := strconv.FormatUint(n, 10)

	if len(ns)%2 != 0 {
		return false
	}

	return ns[0:len(ns)/2] == ns[len(ns)/2:]
}

func ans(input string) uint64 {
	sumRepeats := uint64(0)

	for _, r := range parseInput(input) {
		for i := r.Start; i <= r.End; i++ {
			if repeatsTwice(i) {
				sumRepeats += i
			}
		}
	}
	return sumRepeats
}

func ansII(input string) uint64 {
	sumRepeats := uint64(0)

	for _, r := range parseInput(input) {
		for i := r.Start; i <= r.End; i++ {
			if repeats(i) {
				sumRepeats += i
			}
		}
	}
	return sumRepeats
}

// func ansII(input string) int {
// return 0
// }

func main() {
	defer utils.PrintDuration("Day 2, main", time.Now())

	fmt.Println("input", parseInput(example))

	fmt.Println("ans (example):", ans(example))
	fmt.Println("ans (file):", ans(utils.PuzzleInput("02.txt")))

	fmt.Println("ans ii (example):", ansII(example))
	fmt.Println("ans ii (file):", ansII(utils.PuzzleInput("02.txt")))
}
