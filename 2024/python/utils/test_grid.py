import pytest

from utils.directions import Dir, all_dirs
from utils.grid import make_grid, make_grid_with_points

g =  make_grid('123\n456\n789', True)
g2 = make_grid('1234\n5678', True)


class TestGrid:
    def test_make_grid(self):
        assert (make_grid('12\n34')._data == ['1', '2', '3', '4'])

    def test_make_grid_points(self):
        width = 3
        height = 4
        assert (make_grid_with_points({'#': {(0, 0), (1, 1)}, 'x': {(0,1), (1, 0)}}, width, height, default='.')._data == ['#', 'x', '.', 'x', '#', '.', '.', '.', '.', '.', '.', '.'])

    def test_make_grid_trims(self):
        assert (make_grid('12\n34\n\n')._data == ['1', '2', '3', '4'])

    def test_make_grid_as_ints(self):
        assert (make_grid('12\n34', True)._data == [1, 2, 3, 4])

    def test_make_jagged_grid_raises(self):
        with pytest.raises(Exception):
            make_grid('123\n34', True)

    def test_grid_at(self):
        assert (g2.at_xy(0, 0) == 1)
        assert (g2.at_xy(1, 0) == 2)
        assert (g2.at_xy(2, 0) == 3)
        assert (g2.at_xy(3, 0) == 4)
        assert (g2.at_xy(0, 1) == 5)
        assert (g2.at_xy(1, 1) == 6)
        assert (g2.at_xy(2, 1) == 7)
        assert (g2.at_xy(3, 1) == 8)

    def test_grid_at_p(self):
        assert (g2.at((0, 0)) == 1)
        assert (g2.at((1, 0)) == 2)

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
    def test_grid_in_bounds_manual(self):
        g = make_grid('12\n34', tile_directions=[])
        assert (g.in_bounds((0, 0)) == True)
        assert (g.in_bounds((1, 0)) == True)
        assert (g.in_bounds((0, 1)) == True)
        assert (g.in_bounds((1, 1)) == True)

        assert (g.in_bounds((-1, -1)) == False)
        assert (g.in_bounds((0, -1)) == False)
        assert (g.in_bounds((1, -1)) == False)
        assert (g.in_bounds((2, -1)) == False)

        assert (g.in_bounds((2, 0)) == False)
        assert (g.in_bounds((2, 1)) == False)
        assert (g.in_bounds((2, 2)) == False)

        assert (g.in_bounds((1, 2)) == False)
        assert (g.in_bounds((0, 2)) == False)
        assert (g.in_bounds((-1, 2)) == False)

        assert (g.in_bounds((-1, 1)) == False)
        assert (g.in_bounds((-1, 0)) == False)

    def test_grid_in_bounds(self):
        g = make_grid('12\n34', tile_directions=all_dirs)
        for i in range(-10, 10):
            for j in range(-10, 10):
                assert (g.in_bounds_xy(i, j) == True)
                assert (g.in_bounds((i, j)) == True)

        g = make_grid('12\n34', tile_directions=[])
        for i in range(-10, 10):
            for j in range(-10, 10):
                assert (g.in_bounds_xy(i, j) == bool(0 <= i and i <= 1 and 0 <= j and j <= 1))
                assert (g.in_bounds((i, j)) == bool(0 <= i and i <= 1 and 0 <= j and j <= 1))

    def test_error_on_out_of_bounds_left(self):
        g = make_grid('12\n34', tile_directions=[Dir.Right]) # tile right to "trick" the logic into % x
        with pytest.raises(Exception):
            g.at_xy(-1, 0)

    def test_error_on_out_of_bounds_right(self):
        g = make_grid('12\n34', tile_directions=[Dir.Left])
        with pytest.raises(Exception):
            g.at_xy(g._x_size, 0)

    def test_error_on_out_of_bounds_up(self):
        g = make_grid('12\n34', tile_directions=[Dir.Down])
        with pytest.raises(Exception):
            g.at_xy(0, -1)

    def test_error_on_out_of_bounds_down(self):
        g = make_grid('12\n34', tile_directions=[Dir.Up])
        with pytest.raises(Exception):
            g.at_xy(0, g._y_size)

    def test_grid_at_tile_left(self):
        g = make_grid('12\n34', True, [Dir.Left])
        assert (g.at_xy(-1, 0) == 2)
        assert (g.at_xy(-2, 0) == 1)
        assert (g.at_xy(-1, 1) == 4)
        assert (g.at_xy(-2, 1) == 3)
        assert (g.at_xy(-3, 0) == 2)
        assert (g.at_xy(-4, 0) == 1)
        assert (g.at_xy(-3, 1) == 4)
        assert (g.at_xy(-4, 1) == 3)

    def test_grid_at_tile_right(self):
        g =  make_grid('12\n34', True, [Dir.Right])
        assert (g.at_xy(2, 0) == 1)
        assert (g.at_xy(3, 0) == 2)
        assert (g.at_xy(2, 1) == 3)
        assert (g.at_xy(3, 1) == 4)
        assert (g.at_xy(4, 0) == 1)
        assert (g.at_xy(5, 0) == 2)
        assert (g.at_xy(4, 1) == 3)
        assert (g.at_xy(5, 1) == 4)

    def test_grid_at_tile_up(self):
        g =  make_grid('12\n34', True, [Dir.Up])
        assert (g.at_xy(0, -1) == 3)
        assert (g.at_xy(0, -2) == 1)
        assert (g.at_xy(1, -1) == 4)
        assert (g.at_xy(1, -2) == 2)
        assert (g.at_xy(0, -3) == 3)
        assert (g.at_xy(0, -4) == 1)
        assert (g.at_xy(1, -3) == 4)
        assert (g.at_xy(1, -4) == 2)

    def test_grid_at_tile_down(self):
        g =  make_grid('12\n34', True, [Dir.Down])
        assert (g.at_xy(0, 2) == 1)
        assert (g.at_xy(0, 3) == 3)
        assert (g.at_xy(1, 2) == 2)
        assert (g.at_xy(1, 3) == 4)
        assert (g.at_xy(0, 4) == 1)
        assert (g.at_xy(0, 5) == 3)
        assert (g.at_xy(1, 4) == 2)
        assert (g.at_xy(1, 5) == 4)

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
                assert(g.at_xy(top_left[0] + x, top_left[1] + y) == item)


    def test_grid_at_diagonals_right_down(self):
        g =  make_grid('12\n34', True, [Dir.Right, Dir.Down])
        assert (g.at_xy(2, 2) == 1)
        assert (g.at_xy(3, 2) == 2)
        assert (g.at_xy(2, 3) == 3)
        assert (g.at_xy(3, 3) == 4)

        assert (g.at_xy(4, 4) == 1)
        assert (g.at_xy(5, 4) == 2)
        assert (g.at_xy(4, 5) == 3)
        assert (g.at_xy(5, 5) == 4)

        # and the tile to the right of
        # the first diagonal
        assert (g.at_xy(4, 2) == 1)
        assert (g.at_xy(5, 2) == 2)
        assert (g.at_xy(4, 3) == 3)
        assert (g.at_xy(5, 3) == 4)

    def test_grid_at_out_of_bounds_raises_left(self):
        g =  make_grid('12\n34', as_ints=True, tile_directions=[Dir.Right, Dir.Up, Dir.Down])
        with pytest.raises(Exception):
            g.at_xy(-1, 0)

    def test_grid_at_out_of_bounds_raises_right(self):
        g =  make_grid('12\n34', as_ints=True, tile_directions=[Dir.Left, Dir.Up, Dir.Down])
        with pytest.raises(Exception):
            g.at_xy(3, 0)

    def test_grid_at_out_of_bounds_raises_up(self):
        g =  make_grid('12\n34', as_ints=True, tile_directions=[Dir.Left, Dir.Right, Dir.Down])
        with pytest.raises(Exception):
            g.at_xy(0, -1)

    def test_grid_at_out_of_bounds_raises_down(self):
        g =  make_grid('12\n34', as_ints=True, tile_directions=[Dir.Left, Dir.Right, Dir.Up])
        with pytest.raises(Exception):
            g.at_xy(0, 3)

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

class TestGridAsString:
    def test_as_string(self):
        g = make_grid('12\n34', as_ints=False, tile_directions=all_dirs)
        assert (g.as_string() == '12\n34')
        g_int = make_grid('12\n34', as_ints=True, tile_directions=all_dirs)
        assert (g_int.as_string() == '12\n34')

    def test_as_string_with_overlays(self):
        g = make_grid('12\n34', as_ints=True, tile_directions=all_dirs)
        assert (g.as_string(overlays=[((1,1), '#')]) == '12\n3#')
        assert (g.as_string(overlays=[((0,0), 'a'), ((1, 0), 'b'), ((0, 1), 'c'),((1, 1), 'd')]) == 'ab\ncd')

class TestGridAs2DArray:
    def test_as_2d_array(self):
        g = make_grid('12\n34', as_ints=False, tile_directions=all_dirs)
        assert (g.as_2d_array() == [['1', '2'], ['3', '4']])
        g_int = make_grid('12\n34', as_ints=True, tile_directions=all_dirs)
        assert (g_int.as_2d_array() == [[1, 2], [3, 4]])

    def test_as_2d_array_with_overlays(self):
        g = make_grid('12\n34', as_ints=True, tile_directions=all_dirs)
        assert (g.as_2d_array(overlays=[((1,1), '#')]) == [[1, 2], [3, '#']])

class TestGridIterating:
    def test_grid_iterating(self):
        g = make_grid('12\n34', as_ints=True, tile_directions=all_dirs)
        assert ([((x, y), value) for (x, y), value in g] == [((0, 0), 1), ((1, 0), 2), ((0, 1), 3), ((1, 1), 4)])