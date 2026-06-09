package main

import (
	"adventofcode/utils"
	"adventofcode/utils/grid"
	"fmt"
	"log"
	"regexp"
	"strconv"
	"strings"
	"time"
)

const example = `
123 328  51 64 
 45 64  387 23 
  6 98  215 314
*   +   *   +  `

func solve(input string) string {
	lines := strings.Split(input, "\n")
	cleanedLines := make([]string, len(lines))
	for _, line := range lines {
		cleanedLines = append(cleanedLines, strings.TrimSpace(line))
	}
	cleanInput := strings.Join(cleanedLines, "\n")

	whitespace, regexErr := regexp.Compile("\\s+")
	if regexErr != nil {
		log.Fatal(regexErr)
	}

	g, err := grid.NewSeparatedStringGrid(cleanInput, whitespace)
	if err != nil {
		log.Fatal(err)
	}

	total := uint64(0)
	for _, col := range g.Cols() {
		nums := col[0 : len(col)-1]
		symbol := col[len(col)-1]
		subTotal := uint64(0)
		if symbol == "*" {
			subTotal = 1
		}
		for _, numStr := range nums {
			num, err := strconv.ParseUint(numStr, 10, 64)
			if err != nil {
				log.Fatalf(`Failed to parse %s`, numStr)
			}
			if symbol == "*" {
				subTotal *= num
			} else {
				subTotal += num
			}
		}
		total += subTotal
	}

	return fmt.Sprintf("%v", total)
}

func solve1(input string) string {
	return solve(input)
}

func isEmptyCol(c []string) bool {
	for _, s := range c {
		if strings.TrimSpace(s) != "" {
			return false
		}
	}
	return true
}

func solve2(input string) string {
	g, gridErr := grid.NewStringGrid(input)
	if gridErr != nil {
		log.Fatal(gridErr)
	}

	cols := g.Cols()

	total := uint64(0)
	for i := 0; i < len(cols); i++ {
		start := i
		for j := i + 1; j < len(cols); j++ {
			i = j
			if isEmptyCol(cols[j]) {
				break
			}
		}
		end := i
		if i == len(cols)-1 {
			end = len(cols)
		}

		var symbol string
		nums := make([]uint64, 0)
		for _, col := range cols[start:end] {
			numStr := ""
			for _, val := range col {
				if val == "*" || val == "+" {
					symbol = val
				} else if strings.TrimSpace(val) != "" {
					numStr += val
				}
			}
			n, err := strconv.ParseUint(numStr, 10, 64)
			if err != nil {
				log.Fatalf(`Failed to parse %s`, numStr)
			}
			nums = append(nums, n)
		}

		if symbol == "*" {
			total += utils.Mult(nums)
		} else {
			total += utils.Sum(nums)
		}
	}
	return fmt.Sprintf("%v", total)
}

func main() {
	defer utils.PrintDuration("Day 6", time.Now())
	exampleAns1 := solve1(example)
	ans1 := solve1(utils.PuzzleInput("06.txt"))
	fmt.Println("example:", exampleAns1)
	fmt.Println("pt1:", ans1)

	exampleAns2 := solve2(example)
	ans2 := solve2(utils.PuzzleInput("06.txt"))
	fmt.Println("example2:", exampleAns2)
	fmt.Println("pt2:", ans2)
}
