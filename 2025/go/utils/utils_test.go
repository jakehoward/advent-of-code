package utils

import "testing"

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
