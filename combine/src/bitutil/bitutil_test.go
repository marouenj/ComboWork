package bitutil

import (
	"testing"
)

func TestLeftmost(t *testing.T) {
	cases := []struct {
		in, want uint
	}{
		{0, 0},
		{1, 1},
		{2, 2},
		{3, 2},
		{10, 4},
	}

	for _, c := range cases {
		got := Leftmost(c.in)
		if got != c.want {
			t.Errorf("Leftmost(%d) == %d, want %d", c.in, got, c.want)
		}
	}
}

func TestBitsPerVal(t *testing.T) {
	cases := []struct {
		in, want uint
	}{
		{0, 0},
		{1, 1},
		{2, 1},
		{4, 2},
		{8, 3},
		{9, 4},
		{10, 4},
	}

	for _, c := range cases {
		got := BitsPerVal(c.in)
		if got != c.want {
			t.Errorf("BitsPerVal(%d) == %d, want %d", c.in, got, c.want)
		}
	}
}
