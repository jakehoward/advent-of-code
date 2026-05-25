package main

import (
	"fmt"
	"log"
	"os"
	"strconv"
	"strings"
	"time"
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

func printDuration(label string, start time.Time) {
	now := time.Now().UnixMicro()
	fmt.Println(label, "took:", now-start.UnixMicro(), "µs")
}

func intAbs(x int) int {
	if x < 0 {
		return -x
	}
	return x
}

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

func mod(a, b int) int {
	return (a%b + b) % b
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
		num = mod(num, 100)
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

		numFullRotations := intAbs(line.Distance) / 100
		numZeros += numFullRotations

		rem := move % 100
		if num > 0 && (num+rem <= 0 || num+rem >= 100) {
			numZeros += 1
		}

		num = mod(num+move, 100)
	}
	return numZeros
}

func readFile(path string) string {
	content, err := os.ReadFile(path)
	if err != nil {
		log.Fatal(err)
	}

	return string(content)
}

func main() {
	defer printDuration("Day 1, main", time.Now())

	fmt.Println("ans (example):", ans(example))
	fmt.Println("ans (file):", ans(readFile("../inputs/01.txt")))

	fmt.Println("ans ii (example):", ansII(example))
	fmt.Println("ans ii (file):", ansII(readFile("../inputs/01.txt")))
}
