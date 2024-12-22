import pytest
from days.day_21 import complexity, code_pad_to_robot, arrow_pad_to_robot
from days.day_22 import get_all_sn, get_bananas_to_sequence, get_sequence_to_max_bananas


class TestDay22:
    def test_seq(self):
        all_sn = get_all_sn(123, 10)
        ans = get_bananas_to_sequence(all_sn)
        expected = {6: [(-1, -1, 0, 2)],
                    4: [(-3, 6, -1, -1), (6, -1, -1, 0), (-1, 0, 2, -2), (0, 2, -2, 0)],
                    2: [(2, -2, 0, -2)]}
        assert(expected == ans)

    def test_invert_one_to_many_index(self):
        ans = get_sequence_to_max_bananas({1: [(1, 2), (3, 4)], 2: [(3,4), (5, 6)]})
        expected = {(1,2): 1, (3, 4): 2, (5, 6): 2}
        assert(expected == ans)