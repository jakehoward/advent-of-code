package utils

import (
	"errors"
	"testing"
)

func TestEqSlice(t *testing.T) {
	testCases := []struct {
		name     string
		a        []string
		b        []string
		expected bool
	}{
		{"A", []string{}, []string{}, true},
		{"B", []string{"a"}, []string{}, false},
		{"C", []string{"a"}, []string{"a"}, true},
		{"D", []string{"a", "b"}, []string{"a"}, false},
		{"E", []string{"a", "b"}, []string{"a", "b"}, true},
		{"F", []string{"a", "b"}, []string{"A", "B"}, false},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			result := EqSlice(tc.a, tc.b)
			if result != tc.expected {
				t.Errorf("ex: %v, %v got %v; want %v", tc.a, tc.b, result, tc.expected)
			}
		})
	}
}

func TestMaxIndex(t *testing.T) {
	testCases := []struct {
		name     string
		input    []int
		err      error
		expected int
	}{
		{"Finds largest", []int{0, 1, 1, 5, 1}, nil, 3},
		{"Finds first", []int{0, 5, 5}, nil, 1},
		{"Errors on empty", []int{}, EmptySliceError, -1},
		{"Handles one entry", []int{0}, nil, 0},
		{"Handles negative", []int{0, -1}, nil, 0},
		{"Handles all negative", []int{-20, -1, -8}, nil, 1},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			result, err := MaxIndex(tc.input)
			if !errors.Is(err, tc.err) {
				t.Errorf("err: %v; want %v", err, tc.err)
			}
			if result != tc.expected {
				t.Errorf("ex: %v got %v; want %v", tc.input, result, tc.expected)
			}
		})
	}
}
