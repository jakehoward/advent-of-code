from utils.read import read_input

 # https://www.youtube.com/watch?v=lNFMyI3JBeY&list=PLnNm9syGLD3yf-YW-a5XNh1CJN07xr0Kz&index=2
 # Same logic as https://github.com/maxmil/advent-of-code-2022/blob/main/src/02/02.ts

example = """A Y
B X
C Z"""

print('mod3(me:rock - elf:rock)         (0 - 0) % 3 =  (0 % 3):', (0 - 0) % 3, 'draw')
print('mod3(me:rock - elf:paper)        (0 - 1) % 3 = (-1 % 3):', (0 - 1) % 3, 'lose')
print('mod3(me:rock - elf:scissors)     (0 - 2) % 3 = (-2 % 3):', (0 - 2) % 3, 'win')

print('mod3(me:paper - elf:rock)        (1 - 0) % 3 =  (1 % 3):', (1 - 0) % 3, 'win')
print('mod3(me:paper - elf:paper)       (1 - 1) % 3 = (0 % 3):',  (1 - 1) % 3, 'draw')
print('mod3(me:paper - elf:scissors)    (1 - 2) % 3 = (-1 % 3):', (1 - 2) % 3, 'lose')

print('mod3(me:scissors - elf:rock)     (2 - 0) % 3 =  (2 % 3):', (2 - 0) % 3, 'lose')
print('mod3(me:scissors - elf:paper)    (2 - 1) % 3 = (1 % 3):',  (2 - 1) % 3, 'win')
print('mod3(me:scissors - elf:scissors) (2 - 2) % 3 =  (0 % 3):', (2 - 2) % 3, 'draw')

lose = 0
draw = 3
win = 6

def part1(input):
    # score = your hand + outcome
    score = 0
    for game in input.split('\n'):
        # 0 for A, 1 for B, ..., 0 for X, 1 for Y, ...
        elf = ord(game.split()[0]) - ord('A')
        me = ord(game.split()[1]) - ord('X')

        # listed in order such that the next one beats the current one
        # ROCK <--beats-- PAPER <--beats-- SCISSORS
        # so in simple case, me - elf = 1 => I win, but there's also the -2 case for me rock, elf scissors
        # we also can't break on the case where I lose in the negative direction, me - elf = -1

        # Me        Elf        me - elf      outcome (me)  mod3
        # ROCK      PAPER      0 - 1 = -1    lose          2
        # ROCK      SCISSORS   0 - 2 = -2    win           1
        # PAPER     ROCK       1 - 0 = 1     win           1
        # PAPER     SCISSORS   1 - 2 = -1    lose          2
        # SCISSORS  ROCK       2 - 0 = 2     lose          2
        # SCISSORS  PAPER      2 - 1 = 1     win           1

        if elf == me:
            score += draw
        elif (me - elf) % 3 == 1:
            score += win

        # don't need the lose case because
        # there are zero points for it

        score += me + 1

    answer = score
    print(f'Pt1::ans: {answer}')

# (me - elf) % 3 = outcome
# (me - elf) = outcome % 3
# me = (elf + outcome) % 3
def part2(input):
    score = 0
    for game in input.split('\n'):
        elf = ord(game.split()[0]) - ord('A')
        outcome = ord(game.split()[1]) - ord('X')
        if outcome == 1:
            score += draw
        if outcome == 2:
            score += win

        # before outcome: 0 = Draw, 1 = Win, 2 = Lose
        # now,   outcome: 0 = Lose, 1 = Draw, 2 = Win
        # so to map back to the original domain, minus 1 from outcome
        me = (elf + outcome - 1) % 3
        score += me + 1
    answer = score
    print(f'Pt2::ans: {answer}')


def run():
    day = 2
    input = read_input(day)
    part1(example)
    part1(input)
    part2(example)
    part2(input)


if __name__ == "__main__":
    run()
