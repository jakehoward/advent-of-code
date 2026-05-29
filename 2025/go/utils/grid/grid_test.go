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

func newStringGrid(s string) *Grid[string] {
	g, err := NewStringGrid(s)
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

func TestSurrounding(t *testing.T) {
	smallGrid := newIntGrid("12\n34")
	medGrid := newIntGrid("123\n456\n789")
	largeGrid := newIntGrid("99999\n12345\n00000\n12345\n99999")
	testCases := []struct {
		name     string
		grid     *Grid[int]
		point    Point
		expected *Grid[int]
	}{
		{"small", smallGrid, Point{0, 0}, newIntGrid("12\n34")},
		{"small2", smallGrid, Point{0, 1}, newIntGrid("12\n34")},
		{"small3", smallGrid, Point{1, 0}, newIntGrid("12\n34")},
		{"small4", smallGrid, Point{1, 1}, newIntGrid("12\n34")},
		{"med1", medGrid, Point{1, 1}, newIntGrid("123\n456\n789")},
		{"med2", medGrid, Point{0, 0}, newIntGrid("12\n45")},
		{"med3", medGrid, Point{1, 1}, newIntGrid("123\n456\n789")},
		{"med4", medGrid, Point{2, 2}, newIntGrid("56\n89")},
		{"large", largeGrid, Point{2, 2}, newIntGrid("234\n000\n234")},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			result := tc.grid.Adjacent(tc.point)

			if !reflect.DeepEqual(result, tc.expected) {
				t.Errorf("input: %v, %v, got %v; want %v", tc.grid, tc.point, result, tc.expected)
			}

		})
	}
}

func TestGet(t *testing.T) {
	smallGrid := newIntGrid("12\n34")
	testCases := []struct {
		name     string
		grid     *Grid[int]
		point    Point
		err      error
		expected int
	}{
		{"A", smallGrid, Point{0, 0}, nil, 1},
		{"B", smallGrid, Point{1, 0}, nil, 2},
		{"C", smallGrid, Point{0, 1}, nil, 3},
		{"D", smallGrid, Point{1, 1}, nil, 4},

		{"E1", smallGrid, Point{-1, 0}, PointOutOfRangeErr, 0},
		{"E2", smallGrid, Point{0, -1}, PointOutOfRangeErr, 0},
		{"E3", smallGrid, Point{-1, -1}, PointOutOfRangeErr, 0},
		{"E4", smallGrid, Point{2, 0}, PointOutOfRangeErr, 0},
		{"E5", smallGrid, Point{0, 2}, PointOutOfRangeErr, 0},
		{"E6", smallGrid, Point{2, 2}, PointOutOfRangeErr, 0},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			result, err := tc.grid.Get(tc.point)
			if !errors.Is(err, tc.err) {
				t.Errorf("err: %v; want %v", err, tc.err)
			}
			if !reflect.DeepEqual(result, tc.expected) {
				t.Errorf("input: %v, %v, got %v; want %v", tc.grid, tc.point, result, tc.expected)
			}

		})
	}
}

func TestSet(t *testing.T) {
	testCases := []struct {
		name  string
		grid  *Grid[int]
		point Point
		err   error
	}{
		{"A", newIntGrid("12\n34"), Point{0, 0}, nil},
		{"B", newIntGrid("12\n34"), Point{1, 0}, nil},
		{"C", newIntGrid("12\n34"), Point{0, 1}, nil},
		{"D", newIntGrid("12\n34"), Point{1, 1}, nil},

		{"E1", newIntGrid("12\n34"), Point{-1, 0}, PointOutOfRangeErr},
		{"E2", newIntGrid("12\n34"), Point{0, -1}, PointOutOfRangeErr},
		{"E3", newIntGrid("12\n34"), Point{-1, -1}, PointOutOfRangeErr},
		{"E4", newIntGrid("12\n34"), Point{2, 0}, PointOutOfRangeErr},
		{"E5", newIntGrid("12\n34"), Point{0, 2}, PointOutOfRangeErr},
		{"E6", newIntGrid("12\n34"), Point{2, 2}, PointOutOfRangeErr},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			result, err := tc.grid.Set(tc.point, -1)
			if !errors.Is(err, tc.err) {
				t.Errorf("err: %v; want %v", err, tc.err)
			}
			if v, err := tc.grid.Get(tc.point); v != -1 && err == nil {
				t.Errorf("input: %v, %v, got %v; want %v", tc.grid, tc.point, result, -1)
			}
		})
	}
}

func TestSetPoints(t *testing.T) {
	testCases := []struct {
		name    string
		grid    *Grid[string]
		points  []Point
		newGrid *Grid[string]
		err     error
	}{
		{"A", newStringGrid("12\n34"), []Point{{0, 0}}, newStringGrid("x2\n34"), nil},
		{"B", newStringGrid("12\n34"), []Point{{1, 0}, {0, 1}}, newStringGrid("1x\nx4"), nil},

		{"E1", newStringGrid("12\n34"), []Point{{-1, 0}}, newStringGrid("12\n34"), PointOutOfRangeErr},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			result, err := tc.grid.SetPoints(tc.points, "x")
			if !errors.Is(err, tc.err) {
				t.Errorf("err: %v; want %v", err, tc.err)
			}
			if !reflect.DeepEqual(tc.grid, tc.newGrid) {
				t.Errorf("input: %v, %v, got %v; want %v", tc.grid, tc.points, result, tc.newGrid)
			}
		})
	}
}

func TestPoints(t *testing.T) {
	smallGrid := newIntGrid("12\n34")
	testCases := []struct {
		name     string
		grid     *Grid[int]
		expected []Point
	}{
		{"small", smallGrid, []Point{{0, 0}, {1, 0}, {0, 1}, {1, 1}}},
		{"row only", newIntGrid("12"), []Point{{0, 0}, {1, 0}}},
		{"col only", newIntGrid("1\n2"), []Point{{0, 0}, {0, 1}}},
		{"empty", newIntGrid(""), []Point{}},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			result := tc.grid.Points()

			if !reflect.DeepEqual(result, tc.expected) {
				t.Errorf("input: %v, got %v; want %v", tc.grid, result, tc.expected)
			}

		})
	}
}

func TestPointToIndex(t *testing.T) {
	smallGrid := newIntGrid("12\n34")
	testCases := []struct {
		name     string
		grid     *Grid[int]
		point    Point
		err      error
		expected int
	}{
		{"small", smallGrid, Point{0, 0}, nil, 0},
		{"small", smallGrid, Point{1, 0}, nil, 1},
		{"small", smallGrid, Point{0, 1}, nil, 2},
		{"small", smallGrid, Point{1, 1}, nil, 3},
		//{"row only", newIntGrid("12"), []Point{{0, 0}, {1, 0}}},
		//{"col only", newIntGrid("1\n2"), []Point{{0, 0}, {0, 1}}},
		//{"empty", newIntGrid(""), []Point{}},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			result, err := tc.grid.PointToIndex(tc.point)
			if !errors.Is(err, tc.err) {
				t.Errorf("err: %v; want %v", err, tc.err)
			}
			if result != tc.expected {
				t.Errorf("input: %v, got %v; want %v", tc.grid, result, tc.expected)
			}

		})
	}
}
