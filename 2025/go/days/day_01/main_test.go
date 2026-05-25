package main

import (
	"testing"
)

func TestParseLine(t *testing.T) {
	l := parseLine("L23")

	if l.Direction != "L" || l.Distance != 23 {
		t.Errorf(`Failed to parse line, got %v, want %v`, l, Line{"L", 23})
	}
}

func TestAns(t *testing.T) {
	numZeros := ans(example)

	if numZeros != 3 {
		t.Errorf(`Got wrong answer for example input, got %v, want %v`, numZeros, 3)
	}
}

func TestAnsII(t *testing.T) {
	n := ansII(example)

	if n != 6 {
		t.Errorf(`Got wrong answer for example input, got %v, want %v`, n, 6)
	}
}
