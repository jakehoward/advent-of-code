from utils.directions import Dir


class Matrix:
    def __init__(self, data, x_size, y_size, tile_directions=[]):
        self._data = data
        self._x_size = x_size
        self._y_size = y_size
        self._tile_directions = tile_directions

    def at(self, x, y):
        if Dir.Left in self._tile_directions and x < 0:
            mod = abs(x) % self._x_size
            x = mod if mod == 0 else self._x_size - mod
        if Dir.Right in self._tile_directions:
            x = x % self._x_size
        if Dir.Up in self._tile_directions and y < 0:
            mod = abs(y) % self._y_size
            y = mod if mod == 0 else self._y_size - mod
        if Dir.Down in self._tile_directions:
            y = y % self._y_size

        assert(self.in_bounds(x, y), f"({x},{y}) out of bounds of ({self._x_size-1},{self._y_size-1})")
        return self._data[y * self._x_size + x]

    def in_bounds(self, x, y):
        return 0 <= x and  x < self._x_size and 0 <= y and y < self._y_size


def make_matrix(input, as_ints=False, tile_directions=[]):
    rows = input.strip().split('\n')
    y_size = len(rows)
    x_size = -1
    data = []
    for row in rows:
        if x_size != -1:
            if x_size != len(row):
                raise Exception("Cannot make matrix with irregular row length")
        else:
            x_size = len(row)
        for i in list(row):
            data.append(int(i) if as_ints else i)

    return Matrix(data, x_size, y_size, tile_directions)