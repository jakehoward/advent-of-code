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
	rangeStrs := strings.Split(parts[0], "\n")
	ranges := make([]utils.Range, len(rangeStrs))
	for i, r := range rangeStrs {
		ranges[i] = utils.NewRange(r)
	}

	idStrs := strings.Split(parts[1], "\n")
	ids := make([]uint64, len(idStrs))
	for i, id := range idStrs {
		v, err := strconv.ParseUint(id, 10, 64)
		ids[i] = v
		if err != nil {
			log.Fatal(err)
		}
	}
	freshCount := 0
	for _, id := range ids {
		for _, r := range ranges {
			if id >= r.Start && id <= r.EndIncl {
				freshCount++
				break
			}
		}
	}
	//return fmt.Sprintf("ranges: %v, ids: %v", ranges, ids)
	return fmt.Sprintf("%v", freshCount)
}

func solve1(input string) string {
	return solve(input)
}

func solve2(input string) string {
	return solve(input)
}

func main() {
	defer utils.PrintDuration("Day 3", time.Now())
	ans := "¯\\_(ツ)_/¯"
	exampleAns := solve1(example)
	ans = solve1(utils.PuzzleInput("05.txt"))
	fmt.Println("example:", exampleAns, "input:", ans)

	//exampleAns = solve2(example)
	//ans = solve2(utils.PuzzleInput("04.txt"))
	//fmt.Println("example:", exampleAns, "input:", ans)
}
