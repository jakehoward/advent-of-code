package main

import (
	"adventofcode/utils"
	"adventofcode/utils/grid"
	"fmt"
	"log"
	"time"
)

const example = `987654321111111
811111111111119
234234234234278
818181911112111`

func getJoltage(xs []int, numBatts uint64, ans uint64) uint64 {
	if numBatts == 0 {
		return ans
	}

	maxIndex, err := utils.MaxIndex(xs[:len(xs)-int(numBatts-1)])
	if err != nil {
		log.Fatal(err)
	}
	num := xs[maxIndex]

	return getJoltage(xs[maxIndex+1:], numBatts-1, ans*10+uint64(num))
}

func solve(input string, numBatts uint64) string {
	g, err := grid.NewIntGrid(input)
	if err != nil {
		log.Fatal(err)
	}
	answers := []uint64{}
	for _, row := range g.Rows() {
		ans := getJoltage(row, numBatts, 0)
		answers = append(answers, ans)
	}

	return fmt.Sprint(utils.Sum(answers))
}

func solve1(input string) string {
	return solve(input, 2)
}

func solve2(input string) string {
	return solve(input, 12)
}

func main() {
	defer utils.PrintDuration("Day 3", time.Now())

	exampleAns := solve1(example)
	ans := solve1(utils.PuzzleInput("03.txt"))
	fmt.Println("example:", exampleAns, "input:", ans)

	exampleAns = solve2(example)
	ans = solve2(utils.PuzzleInput("03.txt"))
	fmt.Println("example:", exampleAns, "input:", ans)
}
