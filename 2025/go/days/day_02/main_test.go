package main

import (
	"testing"
)

func TestRepeats(t *testing.T) {
	testCases := []struct {
		name     string
		num      uint64
		expected bool
	}{
		{"A", 0, false},
		{"B", 1, false},
		{"C", 22, true},
		{"C", 222, true},
		{"D", 2121, true},
		{"E", 21212, false},
		{"F", 456456, true},
		{"G", 4564567, false},
		{"H", 45645645, false},
		{"I", 456456456, true},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			result := repeats(tc.num)
			if result != tc.expected {
				t.Errorf("ex: %v, got %v; want %v", tc.num, result, tc.expected)
			}
		})
	}
}

func TestRepeatsTwice(t *testing.T) {
	testCases := []struct {
		name     string
		num      uint64
		expected bool
	}{
		{"A", 0, false},
		{"B", 1, false},
		{"C", 22, true},
		{"D", 2222, true},
		{"E", 2121, true},
		{"F", 21212, false},
		{"G", 212121, false},
		{"H", 456456, true},
		{"I", 456456456, false},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			result := repeatsTwice(tc.num)
			if result != tc.expected {
				t.Errorf("ex: %v, got %v; want %v", tc.num, result, tc.expected)
			}
		})
	}
}

func eq[T comparable](xs []T, ys []T) bool {
	if len(xs) != len(ys) {
		return false
	}
	for i, x := range xs {
		if x != ys[i] {
			return false
		}
	}

	return true
}

func TestChunks(t *testing.T) {
	testCases := []struct {
		name     string
		s        string
		size     int
		ok       bool
		expected []string
	}{
		{"A", "abc", 1, true, []string{"a", "b", "c"}},
		{"B", "abc", 2, false, []string{}},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			result, ok := chunk(tc.s, tc.size)
			if ok && tc.ok {
				if !eq(result, tc.expected) {
					t.Errorf("ex: %v (%v), got %v; want %v", tc.s, tc.size, result, tc.expected)
				}
			} else {
				if tc.ok != ok {
					t.Errorf("got ok %v; want ok %v", ok, tc.ok)
				}
			}
		})
	}
}

func TestPartOne(t *testing.T) {
	got := ans(example)
	want := uint64(1227775554)

	if want != got {
		t.Errorf("got %v; want: %v", got, want)
	}
}

func TestPartTwo(t *testing.T) {
	got := ansII(example)
	want := uint64(4174379265)

	if want != got {
		t.Errorf("got %v; want: %v", got, want)
	}
}
