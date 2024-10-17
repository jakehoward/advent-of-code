class Matrix:
    _data = []
    _x_size = -1
    _y_size = -1
    def __init__(self, data, x_size, y_size):
        self._data = data
        self._x_size = x_size
        self._y_size = y_size

    def at(self, x, y):
        return self._data[y * self._x_size + x]

    def in_bounds(self, x, y):
        return 0 <= x and  x < self._x_size and 0 <= y and y < self._y_size


def make_matrix(input, as_ints=False):
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

    return Matrix(data, x_size, y_size)