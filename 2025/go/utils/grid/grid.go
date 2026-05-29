package grid

import (
	"errors"
	"fmt"
	"strconv"
	"strings"
	"unicode/utf8"
)

var ErrJaggedRows = errors.New("rows must all have the same number of columns")
var PointOutOfRangeErr = errors.New("point out of range")

type Grid[T any] struct {
	Data             []T
	NumRows, NumCols int
}

type Point struct {
	X, Y int
}

func (g *Grid[T]) Cols() [][]T {
	cols := make([][]T, g.NumCols)
	for col := range cols {
		colSlice := make([]T, g.NumRows)
		for row := range colSlice {
			colSlice[row] = g.Data[row*g.NumCols+col]
		}
		cols[col] = colSlice
	}
	return cols
}

func (g *Grid[T]) Rows() [][]T {
	rows := make([][]T, g.NumRows)
	for row := range rows {
		start := row * g.NumCols
		end := (row + 1) * g.NumCols
		capacity := end
		rows[row] = g.Data[start:end:capacity]
	}
	return rows
}

func (g *Grid[T]) Adjacent(point Point) *Grid[T] {
	dys := []int{-1, 0, 1}
	dxs := []int{-1, 0, 1}

	// if we can deduce the number of columns
	// then we know the number of rows
	hitCols := 0
	stopCounting := false

	adj := make([]T, 0, 8)
	i := 0
	for _, dy := range dys {
		stopCounting = hitCols > 0
		if !stopCounting {
			hitCols = 0
		}
		for _, dx := range dxs {
			v, err := g.Get(Point{point.X + dx, point.Y + dy})
			if err == nil {
				if !stopCounting {
					hitCols++
				}
				adj = append(adj, v)
				i++
			}
		}
	}
	return &Grid[T]{adj, len(adj) / hitCols, hitCols}
}

func (g *Grid[T]) Get(point Point) (T, error) {
	if point.X >= 0 && point.X < g.NumCols && point.Y >= 0 && point.Y < g.NumRows {
		return g.Data[point.Y*g.NumCols+point.X], nil
	}

	return *new(T), PointOutOfRangeErr
}

func (g *Grid[T]) Points() []Point {
	points := make([]Point, g.NumRows*g.NumCols)
	i := 0
	for row := 0; row < g.NumRows; row++ {
		for col := 0; col < g.NumCols; col++ {
			points[i] = Point{X: col, Y: row}
			i++
		}
	}
	return points
}

func NewStringGrid(s string) (*Grid[string], error) {
	return New(s, func(s string) (string, error) { return s, nil })
}

func NewIntGrid(s string) (*Grid[int], error) {
	return New(s, strconv.Atoi)
}

func New[T any](s string, convert func(string) (T, error)) (*Grid[T], error) {
	if s == "" {
		return &Grid[T]{Data: []T{}, NumRows: 0, NumCols: 0}, nil
	}

	lines := strings.Split(strings.Trim(s, "\n"), "\n")
	lineLength := utf8.RuneCountInString(lines[0])

	dataLength := len(lines) * lineLength
	data := make([]T, dataLength)

	for row, line := range lines {
		if utf8.RuneCountInString(line) != lineLength {
			return nil, ErrJaggedRows
		}
		col := 0 // range is byte offset, not rune offset
		for _, r := range line {
			cellValue, err := convert(string(r))
			if err != nil {
				return nil, fmt.Errorf("converting %q: %w", string(r), err)
			}
			data[row*lineLength+col] = cellValue
			col++
		}
	}

	return &Grid[T]{Data: data, NumRows: len(lines), NumCols: lineLength}, nil
}
