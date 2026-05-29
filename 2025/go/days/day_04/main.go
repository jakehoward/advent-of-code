package main

import (
	"adventofcode/utils"
	"adventofcode/utils/grid"
	"fmt"
	"log"
	"time"
)

const example = `..@@.@@@@.
@@@.@.@.@@
@@@@@.@.@@
@.@@@@..@.
@@.@@@@.@@
.@@@@@@@.@
.@.@.@.@@@
@.@@@.@@@@
.@@@@@@@@.
@.@.@@@.@.`

func solve(input string, iterations int) string {
	g, err := grid.NewStringGrid(input)
	if err != nil {
		log.Fatal(err)
	}

	numMoveableRolls := 0
	for _, point := range g.Points() {
		if v, err := g.Get(point); err == nil && v == "@" {
			adj := g.Adjacent(point)
			numAdjacentRolls := -1 // don't count self
			for _, v := range adj.Data {
				if v == "@" {
					numAdjacentRolls++
				}
			}

			if numAdjacentRolls < 4 {
				numMoveableRolls++
			}
		}
	}

	return fmt.Sprint(numMoveableRolls)
}

func solve1(input string) string {
	return solve(input, 1)
}

func solve2(input string) string {
	return solve(input, -1)
}

func main() {
	defer utils.PrintDuration("Day 3", time.Now())
	ans := "¯\\_(ツ)_/¯"
	exampleAns := solve1(example)
	ans = solve1(utils.PuzzleInput("04.txt"))
	fmt.Println("example:", exampleAns, "input:", ans)

	//exampleAns = solve2(example)
	//ans = solve2(utils.PuzzleInput("04.txt"))
	//fmt.Println("example:", exampleAns, "input:", ans)
}
