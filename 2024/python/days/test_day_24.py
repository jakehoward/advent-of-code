import pytest

from days.day_24_pt2 import get_all_pairings, x_in, y_in


class TestDay24:
    def test_get_all_pairs(self):
        items = ['a', 'b', 'c', 'd']
        pairings = get_all_pairings(items)
        assert (pairings == [[('a', 'b'), ('c', 'd')], [('a', 'c'), ('b', 'd')], [('a', 'd'), ('b', 'c')]])

    def test_x_in_y_in(self):
        assert (x_in(0) == 'x00')
        assert (x_in(1) == 'x01')
        assert (x_in(10) == 'x10')
        assert (x_in(42) == 'x42')
        assert (y_in(0) == 'y00')
        assert (y_in(1) == 'y01')
        assert (y_in(10) == 'y10')
        assert (y_in(42) == 'y42')
    # Just for eyeballing the output
    # def test_get_all_pairs_three(self):
    #     items = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h']
    #     pairings = get_all_pairings(items)
    #     assert (pairings == [('a', 'b')])