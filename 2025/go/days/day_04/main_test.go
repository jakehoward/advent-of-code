package main

import "testing"

func TestSolve(t *testing.T) {
	ans := solve1(example)
	expected := "13"
	if ans != expected {
		t.Errorf(`Got wrong answer for example input, got %v, want %v`, ans, expected)
	}
}

func TestSolve2(t *testing.T) {
	ans := solve2(example)
	expected := ""
	if ans != expected {
		t.Errorf(`Got wrong answer for example input, got %v, want %v`, ans, expected)
	}
}
