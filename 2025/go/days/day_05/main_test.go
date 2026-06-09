package main

import "testing"

func TestSolve(t *testing.T) {
	ans := solve1(example)
	expected := "3"
	if ans != expected {
		t.Errorf(`Got wrong answer for example input, got %v, want %v`, ans, expected)
	}
}

func TestSolve2(t *testing.T) {
	ans := solve2(example)
	expected := "14"
	if ans != expected {
		t.Errorf(`Got wrong answer for example input, got %v, want %v`, ans, expected)
	}
}

func TestSolve2_2(t *testing.T) {
	var ex = `1-5
7-10
4-8

xx
`
	ans := solve2(ex)
	expected := "10"
	if ans != expected {
		t.Errorf(`Got wrong answer for example input, got %v, want %v`, ans, expected)
	}
}
