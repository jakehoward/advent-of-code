package main

import (
	"adventofcode/utils"
	"fmt"
	"log"
	"strconv"
	"strings"
	"time"
)

const example = `3-5
10-14
16-20
12-18

1
5
8
11
17
32`

func solve(input string) string {
	parts := strings.Split(input, "\n\n")
	ranges := buildRanges(parts[0])

	idStrs := strings.Split(parts[1], "\n")
	ids := make([]uint64, len(idStrs))
	for i, id := range idStrs {
		v, err := strconv.ParseUint(id, 10, 64)
		ids[i] = v
		if err != nil {
			log.Fatal("Solve:", err)
		}
	}
	freshCount := 0
	for _, id := range ids {
		for _, r := range ranges {
			if id >= r.Start && id < r.EndExcl {
				freshCount++
				break
			}
		}
	}

	return fmt.Sprintf("%v", freshCount)
}

func buildRanges(rangeStr string) []utils.Range[uint64] {
	rangeStrs := strings.Split(strings.Trim(rangeStr, "\n"), "\n")
	ranges := make([]utils.Range[uint64], len(rangeStrs))
	for i, r := range rangeStrs {
		parts := strings.Split(r, "-")
		start, err := strconv.ParseUint(parts[0], 10, 64)
		if err != nil {
			log.Fatal("New Range(1):", err)
		}
		endIncl, err := strconv.ParseUint(parts[1], 10, 64)
		if err != nil {
			log.Fatal("New Range(2):", err)
		}

		ranges[i] = utils.NewRange(start, endIncl+1)
	}
	return ranges
}

func solve1(input string) string {
	return solve(input)
}

func mergeRanges(a, b utils.Range[uint64]) utils.Range[uint64] {
	start := uint64(0)
	endExcl := uint64(0)
	if a.Start < b.Start {
		start = a.Start
	} else {
		start = b.Start
	}
	if a.EndExcl > b.EndExcl {
		endExcl = a.EndExcl
	} else {
		endExcl = b.EndExcl
	}
	return utils.NewRange[uint64](start, endExcl)
}

func solve2(input string) string {
	parts := strings.Split(input, "\n\n")
	ranges := buildRanges(parts[0])
	emptyRange := utils.EmptyRange[uint64]()

	mergedRanges := make([]utils.Range[uint64], 0)

	for _, r := range ranges {
		mergedRange := r
		for mi, mr := range mergedRanges {
			if utils.Overlaps(mergedRange, mr) {
				mergedRange = mergeRanges(mergedRange, mr)
				mergedRanges[mi] = emptyRange
			}
		}

		mergedRanges = append(mergedRanges, mergedRange)
	}

	total := uint64(0)
	for _, mr := range mergedRanges {
		total += (mr.EndExcl - mr.Start)
	}

	return fmt.Sprintf("%v", total)
}

func main() {
	defer utils.PrintDuration("Day 5", time.Now())
	exampleAns1 := solve1(example)
	ans1 := solve1(utils.PuzzleInput("05.txt"))
	fmt.Println("example:", exampleAns1)
	fmt.Println("pt1:", ans1)

	exampleAns2 := solve2(example)
	ans2 := solve2(utils.PuzzleInput("05.txt"))
	fmt.Println("example:", exampleAns2)
	fmt.Println("pt2:", ans2)
}
