import pytest

from utils.directions import Dir, all_dirs
from utils.grid import make_grid

g =  make_grid('123\n456\n789', True)
g2 = make_grid('1234\n5678', True)


class TestGrid:
    def test_make_grid(self):
        assert (make_grid('12\n34')._data == ['1', '2', '3', '4'])

    def test_make_grid_trims(self):
        assert (make_grid('12\n34\n\n')._data == ['1', '2', '3', '4'])

    def test_make_grid_as_ints(self):
        assert (make_grid('12\n34', True)._data == [1, 2, 3, 4])

    def test_make_jagged_grid_raises(self):
        with pytest.raises(Exception):
            make_grid('123\n34', True)

    def test_grid_at(self):
        assert (g2.at(0, 0) == 1)
        assert (g2.at(1, 0) == 2)
        assert (g2.at(2, 0) == 3)
        assert (g2.at(3, 0) == 4)
        assert (g2.at(0, 1) == 5)
        assert (g2.at(1, 1) == 6)
        assert (g2.at(2, 1) == 7)
        assert (g2.at(3, 1) == 8)

    def test_grid_in_bounds(self):
        assert (g.in_bounds(-1, 0) == False)
        assert (g.in_bounds(0, -1) == False)
        assert (g.in_bounds(3, 3) == False)
        assert (g.in_bounds(2, 3) == False)
        assert (g.in_bounds(3, 2) == False)
        for i in range(3):
            for j in range(3):
                assert (g.in_bounds(i, j) == True)


class TestGridRepeat:
    def test_grid_at_tile_left(self):
        g =  make_grid('12\n34', True, [Dir.Left])
        assert (g.at(-1, 0) == 2)
        assert (g.at(-2, 0) == 1)
        assert (g.at(-1, 1) == 4)
        assert (g.at(-2, 1) == 3)
        assert (g.at(-3, 0) == 2)
        assert (g.at(-4, 0) == 1)
        assert (g.at(-3, 1) == 4)
        assert (g.at(-4, 1) == 3)

    def test_grid_at_tile_right(self):
        g =  make_grid('12\n34', True, [Dir.Right])
        assert (g.at(2, 0) == 1)
        assert (g.at(3, 0) == 2)
        assert (g.at(2, 1) == 3)
        assert (g.at(3, 1) == 4)
        assert (g.at(4, 0) == 1)
        assert (g.at(5, 0) == 2)
        assert (g.at(4, 1) == 3)
        assert (g.at(5, 1) == 4)

    def test_grid_at_tile_up(self):
        g =  make_grid('12\n34', True, [Dir.Up])
        assert (g.at(0, -1) == 3)
        assert (g.at(0, -2) == 1)
        assert (g.at(1, -1) == 4)
        assert (g.at(1, -2) == 2)
        assert (g.at(0, -3) == 3)
        assert (g.at(0, -4) == 1)
        assert (g.at(1, -3) == 4)
        assert (g.at(1, -4) == 2)

    def test_grid_at_tile_down(self):
        g =  make_grid('12\n34', True, [Dir.Down])
        assert (g.at(0, 2) == 1)
        assert (g.at(0, 3) == 3)
        assert (g.at(1, 2) == 2)
        assert (g.at(1, 3) == 4)
        assert (g.at(0, 4) == 1)
        assert (g.at(0, 5) == 3)
        assert (g.at(1, 4) == 2)
        assert (g.at(1, 5) == 4)

    def test_grid_diagonals(self):
        g =  make_grid('12\n34', as_ints=True, tile_directions=all_dirs)
        top_left = (-4, -2)
        ans = [[1, 2, 1, 2, 1, 2, 1, 2, 1, 2],
               [3, 4, 3, 4, 3, 4, 3, 4, 3, 4],
               [1, 2, 1, 2, 1, 2, 1, 2, 1, 2],
               [3, 4, 3, 4, 3, 4, 3, 4, 3, 4],
               [1, 2, 1, 2, 1, 2, 1, 2, 1, 2],
               [3, 4, 3, 4, 3, 4, 3, 4, 3, 4]]

        for y, row in enumerate(ans):
            for x, item in enumerate(row):
                assert(g.at(top_left[0] + x, top_left[1] + y) == item)


    def test_grid_at_diagonals_right_down(self):
        g =  make_grid('12\n34', True, [Dir.Right, Dir.Down])
        assert (g.at(2, 2) == 1)
        assert (g.at(3, 2) == 2)
        assert (g.at(2, 3) == 3)
        assert (g.at(3, 3) == 4)

        assert (g.at(4, 4) == 1)
        assert (g.at(5, 4) == 2)
        assert (g.at(4, 5) == 3)
        assert (g.at(5, 5) == 4)

        # and the tile to the right of
        # the first diagonal
        assert (g.at(4, 2) == 1)
        assert (g.at(5, 2) == 2)
        assert (g.at(4, 3) == 3)
        assert (g.at(5, 3) == 4)

    def test_grid_at_out_of_bounds_raises_left(self):
        g =  make_grid('12\n34', as_ints=True, tile_directions=[Dir.Right, Dir.Up, Dir.Down])
        with pytest.raises(Exception):
            g.at(-1, 0)

    def test_grid_at_out_of_bounds_raises_right(self):
        g =  make_grid('12\n34', as_ints=True, tile_directions=[Dir.Left, Dir.Up, Dir.Down])
        with pytest.raises(Exception):
            g.at(3, 0)

    def test_grid_at_out_of_bounds_raises_up(self):
        g =  make_grid('12\n34', as_ints=True, tile_directions=[Dir.Left, Dir.Right, Dir.Down])
        with pytest.raises(Exception):
            g.at(0, -1)

    def test_grid_at_out_of_bounds_raises_down(self):
        g =  make_grid('12\n34', as_ints=True, tile_directions=[Dir.Left, Dir.Right, Dir.Up])
        with pytest.raises(Exception):
            g.at(0, 3)