import pytest

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


