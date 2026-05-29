package grid

import (
	"errors"
	"fmt"
	"strconv"
	"strings"
	"unicode/utf8"
)

var ErrJaggedRows = errors.New("rows must all have the same number of columns")

type Grid[T any] struct {
	Data             []T
	NumRows, NumCols int
}

func (g *Grid[T]) Cols() [][]T {
	cols := make([][]T, g.NumCols)
	for col, _ := range cols {
		colSlice := make([]T, g.NumRows)
		cols[col] = colSlice
		for row := 0; row < g.NumRows; row++ {
			colSlice[row] = g.Data[row*g.NumCols+col]
		}
	}
	return cols
}

func (g *Grid[T]) Rows() [][]T {
	rows := make([][]T, g.NumRows)
	for row, _ := range rows {
		rowSlice := make([]T, g.NumCols)
		rows[row] = rowSlice
		for col := 0; col < g.NumCols; col++ {
			rowSlice[col] = g.Data[row*g.NumCols+col]
		}
	}
	return rows
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
