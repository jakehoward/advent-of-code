from utils.directions import Dir


class Grid:
    def __init__(self, data, x_size, y_size, tile_directions=[]):
        self._data = data
        self._x_size = x_size
        self._y_size = y_size
        self._tile_directions = tile_directions
        # design decision: give the "nominal" max, even if the grid tiles
        self.x_max = x_size - 1
        self.y_max = y_size - 1

    def get_nbr_xys(self, x, y, include_diagonals=False):
        deltas = ((0, -1), (0, 1), (-1, 0), (1, 0))
        if include_diagonals:
            deltas += ((1, 1), (-1, -1), (-1, 1), (1, -1))
        return {(x + d[0], y + d[1]) for d in deltas if self.in_bounds(x + d[0], y + d[1])}

    def get_nbrs(self, x, y, include_diagonals=False):
        # up, down, left, right
        nbr_xys = self.get_nbr_xys(x, y, include_diagonals)
        return [self.at(nxy[0], nxy[1]) for nxy in nbr_xys]

    def at(self, x, y):
        # Even though we always do co-ord % size, we only
        # want to do it if the repeat is actually set, else
        # we want an out-of-bounds assertion error
        if (Dir.Left in self._tile_directions and x < 0) or (Dir.Right in self._tile_directions and x > 0):
            x = x % self._x_size
        if (Dir.Up in self._tile_directions and y < 0) or (Dir.Down in self._tile_directions and y > 0):
            y = y % self._y_size

        assert self.in_bounds_no_tile(x, y), f"({x},{y}) out of bounds of ({self._x_size - 1},{self._y_size - 1})"
        return self._data[y * self._x_size + x]

    def at_p(self, point):
        return self.at(point[0], point[1])

    def in_bounds_no_tile(self, x, y):
        return 0 <= x and x < self._x_size and 0 <= y and y < self._y_size

    def in_bounds(self, x, y):
        if (Dir.Left in self._tile_directions and x < 0) or (Dir.Right in self._tile_directions and x > 0):
            x = x % self._x_size
        if (Dir.Up in self._tile_directions and y < 0) or (Dir.Down in self._tile_directions and y > 0):
            y = y % self._y_size

        return self.in_bounds_no_tile(x, y)

    def in_bounds_p(self, point):
        return self.in_bounds(point[0], point[1])


def make_grid(input, as_ints=False, tile_directions=[]):
    rows = input.strip().split('\n')
    y_size = len(rows)
    x_size = -1
    data = []
    for row in rows:
        if x_size != -1:
            if x_size != len(row):
                raise Exception("Cannot make grid with irregular row length")
        else:
            x_size = len(row)
        for i in list(row):
            data.append(int(i) if as_ints else i)

    return Grid(data, x_size, y_size, tile_directions)
