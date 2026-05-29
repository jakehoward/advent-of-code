package main

import "testing"

func TestSolve(t *testing.T) {
	ans := solve(example)
	expected := "357"
	if ans != expected {
		t.Errorf(`Got wrong answer for example input, got %v, want %v`, ans, expected)
	}
}

func TestSolve2(t *testing.T) {
	ans := solve2(example)
	expected := "3121910778619"
	if ans != expected {
		t.Errorf(`Got wrong answer for example input, got %v, want %v`, ans, expected)
	}
}
