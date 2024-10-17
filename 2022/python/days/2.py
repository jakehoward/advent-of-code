from pathlib import Path
from utils.read import read_input
from enum import IntEnum

example = """A Y
B X
C Z"""


class Hand(IntEnum):
    Rock = 1
    Paper = 2
    Scissors = 3


# scissors paper rock scissors
encoding = {'A': Hand.Rock, 'B': Hand.Paper, 'C': Hand.Scissors, 'X': Hand.Rock, 'Y': Hand.Paper, 'Z': Hand.Scissors}

lose = 0
draw = 3
win = 6


class Outcome(IntEnum):
    Lose = 0
    Draw = 3
    Win = 6


graph_me_elf = {Hand.Rock: {Hand.Paper: lose,
                            Hand.Scissors: win},
                Hand.Paper: {Hand.Rock: win,
                             Hand.Scissors: lose},
                Hand.Scissors: {Hand.Rock: lose,
                                Hand.Paper: win}}

encoding_ii = {'A': Hand.Rock, 'B': Hand.Paper, 'C': Hand.Scissors, 'X': Outcome.Lose, 'Y': Outcome.Draw,
               'Z': Outcome.Win}
graph_me_elf_ii = {Outcome.Win: {Hand.Paper: Hand.Scissors,
                                 Hand.Scissors: Hand.Rock,
                                 Hand.Rock: Hand.Paper},
                   Outcome.Draw: {Hand.Rock: Hand.Rock,
                                  Hand.Scissors: Hand.Scissors,
                                  Hand.Paper: Hand.Paper},
                   Outcome.Lose: {Hand.Rock: Hand.Scissors,
                                  Hand.Paper: Hand.Rock,
                                  Hand.Scissors: Hand.Paper}}


def part1(input):
    # score = your hand + outcome
    score = 0
    for game in input.split('\n'):
        elf, me = [encoding.get(code) for code in game.split(' ')]
        assert (elf and me)
        if elf == me:
            score += draw + int(me)
        else:
            score += graph_me_elf.get(me).get(elf) + int(me)

    answer = score
    print(f'Pt1::ans: {answer}')


def part2(input):
    score = 0
    for game in input.split('\n'):
        elf, me = [encoding_ii.get(code) for code in game.split(' ')]
        score += int(me) + graph_me_elf_ii.get(me).get(elf)

    answer = score
    print(f'Pt2::ans: {answer}')


def run():
    day = Path(__file__).name.split('.')[0]
    input = read_input(day)
    part1(example)
    part1(input)
    part2(example)
    part2(input)


if __name__ == "__main__":
    run()
