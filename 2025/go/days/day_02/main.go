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
	Start int
	End   int
}

func parseRange(input string) Range {
	startEnd := strings.Split(input, "-")
	start, _ := strconv.Atoi(startEnd[0])
	end, _ := strconv.Atoi(startEnd[1])
	return Range{start, end}
}

func parseInput(input string) []Range {
	rawRanges := strings.Split(input, ",")
	ranges := make([]Range, len(rawRanges))

	for i, rangeString := range rawRanges {
		ranges[i] = parseRange(rangeString)
	}

	return ranges
}

func ans(input string) int {
	fmt.Println(parseInput(input))
	return 0
}

// func ansII(input string) int {
// return 0
// }

func main() {
	defer utils.PrintDuration("Day 2, main", time.Now())

	fmt.Println("ans (example):", ans(example))
	fmt.Println("ans (file):", ans(utils.PuzzleInput("02.txt")))

	// fmt.Println("ans ii (example):", ansII(example))
	// fmt.Println("ans ii (file):", ansII(utils.PuzzleInput("02.txt")))
}
