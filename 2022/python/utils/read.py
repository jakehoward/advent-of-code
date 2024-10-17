from pathlib import Path

def read_input(day):
    with open(f'{Path(__file__).parent.parent}/input/{day}.txt') as f:
        return f.read()