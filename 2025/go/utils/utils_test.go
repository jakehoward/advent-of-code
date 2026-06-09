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

func TestOverlaps(t *testing.T) {
	testCases := []struct {
		name     string
		a        Range[int]
		b        Range[int]
		expected bool
	}{
		{"no left", Range[int]{0, 5 + 1}, Range[int]{6, 10 + 1}, false},
		{"partial left", Range[int]{0, 6 + 1}, Range[int]{6, 10 + 1}, true},
		{"fully contained", Range[int]{7, 9 + 1}, Range[int]{6, 10 + 1}, true},
		{"partial right", Range[int]{10, 12 + 1}, Range[int]{6, 10 + 1}, true},
		{"no right", Range[int]{11, 12 + 1}, Range[int]{6, 10 + 1}, false},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			result := Overlaps(tc.a, tc.b)
			symmetricResult := Overlaps(tc.b, tc.a)
			if result != symmetricResult {
				t.Errorf("err: result %v; symmetric result %v should be the same", result, symmetricResult)
			}
			if result != tc.expected {
				t.Errorf("ex: %v %v got %v; want %v", tc.a, tc.b, result, tc.expected)
			}
		})
	}
}
