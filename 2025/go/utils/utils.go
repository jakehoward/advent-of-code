package utils

import (
	"fmt"
	"log"
	"os"
	"time"
)

func PrintDuration(label string, start time.Time) {
	now := time.Now().UnixMicro()
	fmt.Println(label, "took:", now-start.UnixMicro(), "µs")
}

func IntAbs(x int) int {
	if x < 0 {
		return -x
	}
	return x
}

func Mod(a, b int) int {
	return (a%b + b) % b
}

func PuzzleInput(filename string) string {
	content, err := os.ReadFile("./inputs/" + filename)
	if err != nil {
		log.Fatal(err)
	}

	return string(content)
}
