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
        assert (g.in_bounds_no_tile(-1, 0) == False)
        assert (g.in_bounds_no_tile(0, -1) == False)
        assert (g.in_bounds_no_tile(3, 3) == False)
        assert (g.in_bounds_no_tile(2, 3) == False)
        assert (g.in_bounds_no_tile(3, 2) == False)
        for i in range(3):
            for j in range(3):
                assert (g.in_bounds_no_tile(i, j) == True)

    def test_max_vals(self):
        g = make_grid('123\n456\n789', True)
        assert(g.x_max == 2)
        assert(g.y_max == 2)


class TestGridTiling:
    def test_grid_in_bounds(self):
        g = make_grid('12\n34', tile_directions=all_dirs)
        for i in range(-10, 10):
            for j in range(-10, 10):
                assert (g.in_bounds(i, j) == True)

        g = make_grid('12\n34', tile_directions=[])
        for i in range(-10, 10):
            for j in range(-10, 10):
                assert (g.in_bounds(i, j) == bool(0 <= i and i <= 1 and 0 <= j and j <= 1))

    def test_error_on_out_of_bounds_left(self):
        g = make_grid('12\n34', tile_directions=[Dir.Right]) # tile right to "trick" the logic into % x
        with pytest.raises(Exception):
            g.at(-1, 0)

    def test_error_on_out_of_bounds_right(self):
        g = make_grid('12\n34', tile_directions=[Dir.Left])
        with pytest.raises(Exception):
            g.at(g._x_size, 0)

    def test_error_on_out_of_bounds_up(self):
        g = make_grid('12\n34', tile_directions=[Dir.Down])
        with pytest.raises(Exception):
            g.at(0, -1)

    def test_error_on_out_of_bounds_down(self):
        g = make_grid('12\n34', tile_directions=[Dir.Up])
        with pytest.raises(Exception):
            g.at(0, g._y_size)

    def test_grid_at_tile_left(self):
        g = make_grid('12\n34', True, [Dir.Left])
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

class TestNbrsGrid:
    def test_nbrs_grid(self):
        g = make_grid('12\n34', as_ints=True, tile_directions=[])
        assert(sorted(g.get_nbrs(0, 0)) == sorted([3, 2]))
        assert(sorted(g.get_nbrs(1, 0)) == sorted([4, 1]))
        assert(sorted(g.get_nbrs(0, 1)) == sorted([1, 4]))
        assert(sorted(g.get_nbrs(1, 1)) == sorted([2, 3]))

    def test_nbrs_grid_tiling(self):
        g = make_grid('12\n34', as_ints=True, tile_directions=all_dirs)
        # 1 2 1 2
        # 3 4 3 4
        # 1 2 - 2
        # 3 4 3 4
        assert(sorted(g.get_nbrs(0, 0, include_diagonals=True)) == sorted([2,4,3,4,2,4,3,4]))

    def test_nbrs_grid_diagonals(self):
        g = make_grid('123\n456\n789', as_ints=True, tile_directions=[])
        # 123
        # 456
        # 789
        assert (sorted(g.get_nbrs(1, 1, include_diagonals=True)) == sorted([1, 2, 3, 4, 6, 7, 8, 9]))
        assert (sorted(g.get_nbrs(0, 0, include_diagonals=True)) == sorted([2, 4, 5]))
        assert (sorted(g.get_nbrs(2, 2, include_diagonals=True)) == sorted([6, 5, 8]))
        assert (sorted(g.get_nbrs(2, 0, include_diagonals=True)) == sorted([2,5,6]))
        assert (sorted(g.get_nbrs(0, 2, include_diagonals=True)) == sorted([4,5,8]))
