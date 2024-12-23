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

    def __iter__(self):
        # Iterate over all coordinates
        for y in range(self._y_size):
            for x in range(self._x_size):
                yield ((x, y), self.at_xy(x, y))

    def get_nbr_xys(self, x, y, include_diagonals=False):
        deltas = ((0, -1), (0, 1), (-1, 0), (1, 0))
        if include_diagonals:
            deltas += ((1, 1), (-1, -1), (-1, 1), (1, -1))
        return {(x + d[0], y + d[1]) for d in deltas if self.in_bounds_xy(x + d[0], y + d[1])}

    def get_nbrs(self, x, y, include_diagonals=False):
        # up, down, left, right
        nbr_xys = self.get_nbr_xys(x, y, include_diagonals)
        return [self.at_xy(nxy[0], nxy[1]) for nxy in nbr_xys]

    def at_xy(self, x, y):
        # Even though we always do co-ord % size, we only
        # want to do it if the repeat is actually set, else
        # we want an out-of-bounds assertion error
        if (Dir.Left in self._tile_directions and x < 0) or (Dir.Right in self._tile_directions and x > 0):
            x = x % self._x_size
        if (Dir.Up in self._tile_directions and y < 0) or (Dir.Down in self._tile_directions and y > 0):
            y = y % self._y_size

        assert self.in_bounds_no_tile(x, y), f"({x},{y}) out of bounds of ({self._x_size - 1},{self._y_size - 1})"
        return self._data[y * self._x_size + x]

    def at(self, point):
        return self.at_xy(point[0], point[1])

    def in_bounds_no_tile(self, x, y):
        return 0 <= x and x < self._x_size and 0 <= y and y < self._y_size

    def in_bounds_xy(self, x, y):
        if (Dir.Left in self._tile_directions and x < 0) or (Dir.Right in self._tile_directions and x > 0):
            x = x % self._x_size
        if (Dir.Up in self._tile_directions and y < 0) or (Dir.Down in self._tile_directions and y > 0):
            y = y % self._y_size

        return self.in_bounds_no_tile(x, y)

    def in_bounds(self, point):
        return self.in_bounds_xy(point[0], point[1])

    def as_string(self, overlays=[], allow_duplicates=False):
        """
        Get the grid as a string, presumably for printing
        :param overlays: [((x, y), char), ...]
        :return: grid as string
        """
        overlay_lookup = {}
        for point, char in overlays:
            if point in overlay_lookup and not allow_duplicates:
                raise ValueError(f"Overlay {(point, char)} already exists: {overlay_lookup[point]}")
            overlay_lookup[point] = char

        chars = []
        for line in range(self._y_size):
            for col in range(self._x_size):
                char = overlay_lookup.get((col, line), self.at_xy(col, line))
                chars.append(str(char))
            chars.append('\n')
        return "".join(chars).strip()

    def as_2d_array(self, overlays=[]):
        overlay_lookup = {}
        for point, char in overlays:
            if point in overlay_lookup:
                raise ValueError(f"Overlay {(point, char)} already exists: {overlay_lookup[point]}")
            overlay_lookup[point] = char

        grid_2d = []
        for line in range(self._y_size):
            line_arr = []
            for col in range(self._x_size):
                cell = overlay_lookup.get((col, line), self.at_xy(col, line))
                line_arr.append(cell)
            grid_2d.append(line_arr)
        return grid_2d

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

def make_grid_with_points(char_to_points, width, height, default=' '):
    items = []
    for y in range(height):
        for x in range(width):
            for v, points in char_to_points.items():
                if (x, y) in points:
                    items.append(v)
                    break # watch out for duplicates!
            else:
                items.append(default)

    return Grid(items, width, height, [])
