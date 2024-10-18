from enum import Enum
import pygame

class Color(Enum):
    Black = (0, 0, 0)
    White = (255, 255, 255)
    Gray = (128, 128, 128)
    Green = (0, 255, 0)
    Red = (255, 0, 0)
    Blue = (0, 0, 255)
    Yellow = (255, 255, 0)
    Purple = (128, 0, 128)

def viz_grid(grid, value_to_color={0: Color.Black, 1: Color.White}):
    pygame.init()
    cell_size = 10
    window_width = max(grid._x_size * cell_size, 100)
    window_height = max(grid._y_size * cell_size, 100)

    window = pygame.display.set_mode((window_width, window_height), pygame.RESIZABLE)
    pygame.display.set_caption('Grid Visualization')

    running = True
    while running:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False

        window.fill(Color.White.value)

        for y in range(grid._y_size):
            for x in range(grid._x_size):
                rect = pygame.Rect(x * cell_size, y * cell_size, cell_size, cell_size)
                color = value_to_color.get(grid.at(x, y), Color.Gray).value
                pygame.draw.rect(window, color, rect, 0)
                pygame.draw.rect(window, Color.Green.value, rect, 1)  # Grid lines

        pygame.display.update()

    pygame.quit()
