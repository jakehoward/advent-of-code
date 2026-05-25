package main

import (
	"fmt"
	"strconv"
	"strings"
	"time"

	"adventofcode/utils"
)

const example = `L68
L30
R48
L5
R60
L55
L1
L99
R14
L82`

type Line struct {
	Direction string
	Distance  int
}

func parseLine(input string) Line {
	direction := input[0:1]
	distance, err := strconv.Atoi(input[1:])
	if err != nil {
		panic("could not parse distance from code")
	}
	return Line{direction, distance}
}

func ans(input string) int {
	num := 50
	numZeros := 0
	for _, rawLine := range strings.Fields(input) {
		line := parseLine(rawLine)

		if line.Direction == "L" {
			num -= line.Distance
		} else {
			num += line.Distance
		}
		num = utils.Mod(num, 100)
		if num == 0 {
			numZeros += 1
		}
	}
	return numZeros
}

func ansII(input string) int {
	num := 50
	numZeros := 0
	for _, rawLine := range strings.Fields(input) {
		line := parseLine(rawLine)

		move := 0
		if line.Direction == "L" {
			move = line.Distance * -1
		} else {
			move = line.Distance
		}

		numFullRotations := utils.IntAbs(line.Distance) / 100
		numZeros += numFullRotations

		rem := move % 100
		if num > 0 && (num+rem <= 0 || num+rem >= 100) {
			numZeros += 1
		}

		num = utils.Mod(num+move, 100)
	}
	return numZeros
}

func main() {
	defer utils.PrintDuration("Day 1, main", time.Now())

	fmt.Println("ans (example):", ans(example))
	fmt.Println("ans (file):", ans(utils.PuzzleInput("01.txt")))

	fmt.Println("ans ii (example):", ansII(example))
	fmt.Println("ans ii (file):", ansII(utils.PuzzleInput("01.txt")))
}
