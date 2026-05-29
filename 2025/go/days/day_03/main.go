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

func solve(input string) string {
	g, err := grid.NewIntGrid(input)
	if err != nil {
		log.Fatal(err)
	}
	answers := []int{}
	for _, row := range g.Rows() {
		butLast := row[:len(row)-1]

		maxIndexFirst, err := utils.MaxIndex(butLast)
		if err != nil {
			log.Fatal(err)
		}
		maxIndexSecond, err := utils.MaxIndex(row[maxIndexFirst+1:])
		if err != nil {
			log.Fatal(err)
		}

		ans := row[maxIndexFirst]*10 + row[maxIndexSecond+maxIndexFirst+1]
		answers = append(answers, ans)
	}
	return fmt.Sprint(utils.Sum(answers))
}

func solve2(input string) string {
	return ""
}

func main() {
	defer utils.PrintDuration("Day 3", time.Now())
	exampleAns := solve(example)
	fmt.Println("solve(example):", exampleAns)

	ans := solve(utils.PuzzleInput("03.txt"))
	fmt.Println("solve(input):", ans)
}
