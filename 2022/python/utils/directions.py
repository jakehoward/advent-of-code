from enum import Enum

class Dir(Enum):
    Up = 1
    Down = 2
    Left = 3
    Right = 4
    Diagonals = 5

all_dirs = [Dir.Up, Dir.Down, Dir.Left, Dir.Right, Dir.Diagonals]
cardinal_dirs = [Dir.Up, Dir.Down, Dir.Left, Dir.Right]