import pytest
from days.day_21 import complexity, code_pad_to_robot_options_tree, up, left, press, down, right, get_all_paths, \
    get_shortest_path


class TestDay21:
    def test_complexity(self):
        assert (complexity('002A', '^v<>A') == 10)

    def test_all_paths(self):
        A = (left, up, press)
        B = (up, left, press)
        C = (up, right, press)
        D = (right, up, press)
        E = (down, down, press)
        tree = code_pad_to_robot_options_tree('26A', 'A', ['root', []])
        expected = [
            [A, C, E],
            [A, D, E],
            [B, C, E],
            [B, D, E],
        ]
        assert (sorted(get_all_paths(tree, [])) == sorted(expected))

    def test_get_shortest_path(self):
        assert(len(get_shortest_path('029A', 2)) == 68)
        assert(len(get_shortest_path('980A', 2)) == 60)
        assert(len(get_shortest_path('179A', 2)) == 68)
        assert(len(get_shortest_path('456A', 2)) == 64)
        assert(len(get_shortest_path('379A', 2)) == 64)
