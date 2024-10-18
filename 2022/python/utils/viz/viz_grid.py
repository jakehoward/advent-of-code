from enum import Enum
import pygame
import pygame_widgets
from pygame_widgets.slider import Slider
from pygame_widgets.textbox import TextBox

class Color(Enum):
    Black = (0, 0, 0)
    White = (255, 255, 255)
    Gray = (128, 128, 128)
    Green = (0, 255, 0)
    Red = (255, 0, 0)
    Blue = (0, 0, 255)
    Yellow = (255, 255, 0)
    Purple = (128, 0, 128)

MIN_DELAY=100
MAX_DELAY=5000

MIN_WIDTH = 100
MIN_HEIGHT = 100

SLIDER_WIDTH = 500
SLIDER_HEIGHT = 20
SLIDER_LABEL_WIDTH = 100
SLIDER_LABEL_HEIGHT = 20
MARGIN = 50
SLIDER_PADDING = 10

FRAME_LABEL_HEIGHT = 30
FRAME_LABEL_WIDTH = 100

total_slider_width = SLIDER_WIDTH + SLIDER_LABEL_WIDTH + SLIDER_PADDING + MARGIN * 2
total_frame_label_width = FRAME_LABEL_WIDTH + MARGIN * 2
GUI_CONTROLS_WIDTH = max(total_slider_width, total_frame_label_width)
GUI_CONTROLS_HEIGHT = SLIDER_HEIGHT + MARGIN * 2 + FRAME_LABEL_HEIGHT + MARGIN * 2

BOTTOM_PADDING = 20

def get_slider_label(slider_value):
    return str(slider_value) + 'ms'

clock = pygame.time.Clock()
def viz_grid(grid, value_to_color={0: Color.Black, 1: Color.White}):
    pygame.init()
    cell_size = 10
    window_width = max(grid._x_size * cell_size, MIN_WIDTH, GUI_CONTROLS_WIDTH)
    window_height = max(grid._y_size * cell_size + GUI_CONTROLS_HEIGHT + BOTTOM_PADDING, MIN_HEIGHT)

    window = pygame.display.set_mode((window_width, window_height), pygame.RESIZABLE)
    pygame.display.set_caption('Grid Visualization')

    slider = Slider(window, MARGIN, MARGIN, SLIDER_WIDTH, SLIDER_HEIGHT, min=MIN_DELAY, max=MAX_DELAY, step=100)
    slider_label = TextBox(window, MARGIN * 2 + SLIDER_WIDTH + SLIDER_PADDING, MARGIN, SLIDER_LABEL_WIDTH, SLIDER_HEIGHT + 10, fontSize=24)
    slider_label.setText(get_slider_label(slider.getValue()))

    frame_label = TextBox(window, MARGIN, SLIDER_HEIGHT + MARGIN * 2, FRAME_LABEL_WIDTH, FRAME_LABEL_HEIGHT, fontSize=24)
    frame_label.setText('Frame 1')

    def draw_grid(grid):
        y_start = GUI_CONTROLS_HEIGHT
        for y in range(grid._y_size):
            for x in range(grid._x_size):
                rect = pygame.Rect(x * cell_size, y * cell_size + y_start, cell_size, cell_size)
                color = value_to_color.get(grid.at(x, y), Color.Gray).value
                pygame.draw.rect(window, color, rect, 0)
                pygame.draw.rect(window, Color.Green.value, rect, 1)  # Grid lines

    frames = [1,2,3,4,5,6,7,8,9,10,11,12]

    running = True
    frame_idx = 0
    while running:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False

        window.fill(Color.White.value)

        slider_label.setText(get_slider_label(slider.getValue()))
        frame_label.setText(f"Frame: {frames[frame_idx]}")
        pygame_widgets.update(pygame.event.get())
        draw_grid(grid)

        pygame.display.update()

        # Not this. This freezes the loop. Need to detect
        # enough time has passed using the clock and only update if so
        # pygame.time.delay(int(slider.getValue()))
        # if frame_idx < len(frames) - 1:
        #     frame_idx += 1

        clock.tick(60)

    pygame.quit()