import pytest

from days.day_24_pt2 import get_all_pairings


class TestDay24:
    def test_get_all_pairs(self):
        items = ['a', 'b', 'c', 'd']
        pairings = get_all_pairings(items)
        assert (pairings == [[('a', 'b'), ('c', 'd')], [('a', 'c'), ('b', 'd')], [('a', 'd'), ('b', 'c')]])

    # Just for eyeballing the output
    # def test_get_all_pairs_three(self):
    #     items = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h']
    #     pairings = get_all_pairings(items)
    #     assert (pairings == [('a', 'b')])