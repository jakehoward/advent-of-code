package grid

import (
	"errors"
	"fmt"
	"reflect"
	"strconv"
	"testing"
)

func TestNewGrid(t *testing.T) {
	testCases := []struct {
		name     string
		input    string
		expected Grid[string]
	}{
		{"empty", "", Grid[string]{[]string{}, 0, 0}},
		{"single row", "abc", Grid[string]{[]string{"a", "b", "c"}, 1, 3}},
		{"matrix", "abc\ndef", Grid[string]{[]string{"a", "b", "c", "d", "e", "f"}, 2, 3}},
		{"trailing newlines", "abc\ndef\n", Grid[string]{[]string{"a", "b", "c", "d", "e", "f"}, 2, 3}},
		{"leading newlines", "\nabc\ndef", Grid[string]{[]string{"a", "b", "c", "d", "e", "f"}, 2, 3}},
		{"bracket newlines", "\nabc\ndef\n", Grid[string]{[]string{"a", "b", "c", "d", "e", "f"}, 2, 3}},
		{"spaces", "ab \ndef", Grid[string]{[]string{"a", "b", " ", "d", "e", "f"}, 2, 3}},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			result, err := New(tc.input, func(s string) (string, error) { return s, nil })
			if err != nil {
				t.Errorf("unexpected error: %v", err)
			}
			if !reflect.DeepEqual(*result, tc.expected) {
				t.Errorf("input: %v, got %v; want %v", tc.input, result, tc.expected)
			}
		})
	}
}

func TestNewGridError(t *testing.T) {
	_, err := New("12A", strconv.Atoi)
	expected := fmt.Errorf("converting \"A\": strconv.Atoi: parsing \"A\": invalid syntax")

	if err.Error() != expected.Error() {
		t.Errorf("Expected '%v', got: '%v'", expected, err)
	}
}

func TestNewGridJaggedError(t *testing.T) {
	_, err := New("123\n45\n678", strconv.Atoi)

	if !errors.Is(err, ErrJaggedRows) {
		t.Errorf("Expected '%v', got: '%v'", ErrJaggedRows, err)
	}
}

func TestNewStringGrid(t *testing.T) {
	testCases := []struct {
		name     string
		input    string
		expected Grid[string]
	}{
		{"empty", "", Grid[string]{[]string{}, 0, 0}},
		{"single row", "abc", Grid[string]{[]string{"a", "b", "c"}, 1, 3}},
		{"matrix", "abc\ndef", Grid[string]{[]string{"a", "b", "c", "d", "e", "f"}, 2, 3}},
		{"utf-8", "推己及人\n方能交友", Grid[string]{[]string{"推", "己", "及", "人", "方", "能", "交", "友"}, 2, 4}},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			result, err := NewStringGrid(tc.input)
			if err != nil {
				t.Errorf("unexpected error: %v", err)
			}
			if !reflect.DeepEqual(*result, tc.expected) {
				t.Errorf("input: %v, got %v; want %v", tc.input, result, tc.expected)
			}
		})
	}
}

func TestNewIntGrid(t *testing.T) {
	testCases := []struct {
		name     string
		input    string
		expected Grid[int]
	}{
		{"empty", "", Grid[int]{[]int{}, 0, 0}},
		{"single row", "123", Grid[int]{[]int{1, 2, 3}, 1, 3}},
		{"matrix", "123\n456", Grid[int]{[]int{1, 2, 3, 4, 5, 6}, 2, 3}},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			result, err := NewIntGrid(tc.input)
			if err != nil {
				t.Errorf("unexpected error: %v", err)
			}
			if !reflect.DeepEqual(*result, tc.expected) {
				t.Errorf("input: %v, got %v; want %v", tc.input, result, tc.expected)
			}
		})
	}
}

func newIntGrid(s string) *Grid[int] {
	g, err := NewIntGrid(s)
	if err != nil {
		panic("unexpected error")
	}
	return g
}

func TestCols(t *testing.T) {
	testCases := []struct {
		name     string
		grid     *Grid[int]
		expected [][]int
	}{
		{"empty", newIntGrid(""), [][]int{}},
		{"single row", newIntGrid("123"), [][]int{{1}, {2}, {3}}},
		{"matrix", newIntGrid("123\n456"), [][]int{{1, 4}, {2, 5}, {3, 6}}},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			result := tc.grid.Cols()

			if !reflect.DeepEqual(result, tc.expected) {
				t.Errorf("input: %v, got %v; want %v", tc.grid, result, tc.expected)
			}

		})
	}
}

func TestRows(t *testing.T) {
	testCases := []struct {
		name     string
		grid     *Grid[int]
		expected [][]int
	}{
		{"empty", newIntGrid(""), [][]int{}},
		{"single row", newIntGrid("123"), [][]int{{1, 2, 3}}},
		{"matrix", newIntGrid("123\n456"), [][]int{{1, 2, 3}, {4, 5, 6}}},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			result := tc.grid.Rows()

			if !reflect.DeepEqual(result, tc.expected) {
				t.Errorf("input: %v, got %v; want %v", tc.grid, result, tc.expected)
			}

		})
	}
}
