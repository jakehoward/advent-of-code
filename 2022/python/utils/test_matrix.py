import pytest

from utils.directions import Dir
from utils.matrix import make_matrix

m = make_matrix('123\n456\n789', True)
m2 = make_matrix('1234\n5678', True)

class TestMatrix:
    def test_make_matrix(self):
        assert (make_matrix('12\n34')._data == ['1', '2', '3', '4'])

    def test_make_matrix_trims(self):
        assert (make_matrix('12\n34\n\n')._data == ['1', '2', '3', '4'])

    def test_make_matrix_as_ints(self):
        assert (make_matrix('12\n34', True)._data == [1, 2, 3, 4])

    def test_make_jagged_matrix_raises(self):
        with pytest.raises(Exception):
            make_matrix('123\n34', True)

    def test_matrix_at(self):
        assert(m2.at(0, 0) == 1)
        assert(m2.at(1, 0) == 2)
        assert(m2.at(2, 0) == 3)
        assert(m2.at(3, 0) == 4)
        assert(m2.at(0, 1) == 5)
        assert(m2.at(1, 1) == 6)
        assert(m2.at(2, 1) == 7)
        assert(m2.at(3, 1) == 8)

    def test_matrix_in_bounds(self):
        assert(m.in_bounds(-1, 0) == False)
        assert(m.in_bounds(0, -1) == False)
        assert(m.in_bounds(3, 3) == False)
        assert(m.in_bounds(2, 3) == False)
        assert(m.in_bounds(3, 2) == False)
        for i in range(3):
            for j in range(3):
                assert(m.in_bounds(i, j) == True)

class TestMatrixRepeat:
    def test_matrix_at_tile_left(self):
        m = make_matrix('12\n34', True, [Dir.Left])
        assert(m.at(-1, 0) == 2)
        assert(m.at(-2, 0) == 1)
        assert(m.at(-1, 1) == 4)
        assert(m.at(-2, 1) == 3)
        assert (m.at(-3, 0) == 2)
        assert (m.at(-4, 0) == 1)
        assert (m.at(-3, 1) == 4)
        assert (m.at(-4, 1) == 3)

    def test_matrix_at_tile_right(self):
        m = make_matrix('12\n34', True, [Dir.Right])
        assert(m.at(2, 0) == 1)
        assert(m.at(3, 0) == 2)
        assert(m.at(2, 1) == 3)
        assert(m.at(3, 1) == 4)
        assert (m.at(4, 0) == 1)
        assert (m.at(5, 0) == 2)
        assert (m.at(4, 1) == 3)
        assert (m.at(5, 1) == 4)

    def test_matrix_at_tile_up(self):
        m = make_matrix('12\n34', True, [Dir.Up])
        assert(m.at(0, -1) == 3)
        assert(m.at(0, -2) == 1)
        assert(m.at(1, -1) == 4)
        assert(m.at(1, -2) == 2)
        assert (m.at(0, -3) == 3)
        assert (m.at(0, -4) == 1)
        assert (m.at(1, -3) == 4)
        assert (m.at(1, -4) == 2)

    def test_matrix_at_tile_down(self):
        m = make_matrix('12\n34', True, [Dir.Down])
        assert(m.at(0, 2) == 1)
        assert(m.at(0, 3) == 3)
        assert(m.at(1, 2) == 2)
        assert(m.at(1, 3) == 4)
        assert (m.at(0, 4) == 1)
        assert (m.at(0, 5) == 3)
        assert (m.at(1, 4) == 2)
        assert (m.at(1, 5) == 4)
