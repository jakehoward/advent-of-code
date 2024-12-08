from manim import *
from days.day_4 import example_2, mas_4
from utils.grid import make_grid


def build_grid(grid_data, square_size=1, font_size=24):
    grid_group = VGroup()
    squares = VGroup()

    for i, row in enumerate(grid_data):
        for j, val in enumerate(row):
            square = Square(side_length=square_size)
            square.move_to([j, -i, 0])  # Negative i to build downward
            squares.add(square)

            # Add text in square
            text = Text(str(val), font_size=font_size)
            text.move_to(square.get_center())
            grid_group.add(square, text)
    return grid_group, squares


def is_match(grid_data, pattern_grid_data, cell_idx):
    x_size = len(grid_data[0])
    y_size = len(grid_data)
    grid_x = cell_idx % x_size
    grid_y = cell_idx // x_size

    offsets = [(-1, -1), (0, 0), (1, 1), (-1, 1), (1, -1)]
    grid_letters = [grid_data[grid_y + y][grid_x + x] for x, y in offsets if
                    grid_x + x >= 0 and grid_y + y >= 0 and grid_x + x < x_size and grid_y + y < y_size]
    pattern_letters = [pattern_grid_data[y + 1][x + 1] for x, y in offsets]
    return len(grid_letters) == len(pattern_letters) and all(
        map(lambda x: x[0] == x[1], zip(grid_letters, pattern_letters)))


class DayFour(Scene):
    def construct(self):
        # Make the grid
        grid_data = make_grid(example_2).as_2d_array()
        y_size = len(grid_data)
        x_size = len(grid_data[0])

        # Create the base board
        grid_group, squares = build_grid(grid_data)
        grid_group.center()
        grid_group.scale_to_fit_width(config.frame_width - 2)
        grid_group.scale_to_fit_height(config.frame_height - 2)

        self.play(Create(squares))
        self.play(*[Create(m) for m in grid_group if isinstance(m, Text)])

        # Show the pattern on the left
        pattern_grid_data = make_grid(mas_4).as_2d_array()
        pattern_grid_group, pattern_squares = build_grid(pattern_grid_data)

        cell_size = squares[0].width
        pattern_grid_group.scale(cell_size)
        pattern_grid_group.next_to(squares[x_size + 1], LEFT)
        pattern_grid_group.shift(LEFT)

        self.play(Create(pattern_squares))
        self.play(*[Create(m) for m in pattern_grid_group if isinstance(m, Text)])

        # Move the pattern over the grid
        match_count = 0
        counter = DecimalNumber(0, num_decimal_places=0, include_sign=False).set_color(WHITE).scale(2.5).next_to(
            squares[0], LEFT).shift(LEFT).shift(DOWN)
        tracker = ValueTracker(0)
        counter.add_updater(lambda m: m.set_value(tracker.get_value()))
        # counter = Integer(number=match_count).set_color(WHITE).scale(2.5).next_to(squares[0], LEFT).shift(
        #     LEFT).shift(DOWN)
        cell_idx = x_size
        last_cell_idx = x_size * y_size - x_size - 1
        first_run = True
        while cell_idx < last_cell_idx:
            wait_time = 0.45 - (0.4 * cell_idx / last_cell_idx)
            run_time = 0.55 - (0.5 * cell_idx / last_cell_idx)
            self.play(pattern_grid_group.animate.move_to(squares[cell_idx]), run_time=run_time)
            if is_match(grid_data, pattern_grid_data, cell_idx):
                match_count += 1
                # self.play(counter.animate.become(Integer(match_count)))
                self.play(tracker.animate.set_value(match_count))
                self.wait(0.75)
            else:
                self.wait(wait_time)

            if first_run:
                self.play(FadeIn(counter))
                self.wait(0.5)

            cell_idx += 1
            first_run = False

        # Convolve the grid with the pattern
        # cells = [(0, 0), (1, 1), (2, 2), (0, 2), (2, 0)]
        # for x, y in cells:
        #     highlight = Square(
        #         side_length=squares[y * len(grid_data[0]) + x].width,
        #         stroke_color=RED,
        #         stroke_width=4
        #     )
        #     highlight.move_to(squares[y * len(grid_data[0]) + x])
        #     self.play(Create(highlight))
        #     self.wait(0.1)
