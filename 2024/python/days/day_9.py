from pathlib import Path

from utils.misc import timer
from utils.read import read_input

example = """2333133121414131402"""


def find_last_block(arr, in_last_block_idx):
    out_last_block_idx = in_last_block_idx
    while arr[out_last_block_idx] == -1:
        out_last_block_idx -= 1
    return out_last_block_idx


def create_fs_arr(input):
    compact_nums = [int(c) for c in list(input)]
    arr = []
    free_blocks = 0
    for i, num in enumerate(compact_nums):
        if i % 2 != 0:
            arr += [-1] * num
            free_blocks += 1
        else:
            arr += [i - free_blocks] * num
    return arr

def part1(input):
    if len(input) % 2 == 0:
        print("Input is even length!")

    arr = create_fs_arr(input)

    last_block_idx = find_last_block(arr, len(arr) - 1)
    assert arr[last_block_idx] > 0 and arr[last_block_idx] != -1, "Looks like input has even number..."
    for i in range(len(arr)):
        if i >= last_block_idx:
            break
        if arr[i] == -1:
            arr[i] = arr[last_block_idx]
            arr[last_block_idx] = -1
            last_block_idx = find_last_block(arr, last_block_idx - 1)

    # print(arr)
    answer = sum([i * n for i, n in enumerate(arr) if n != -1])
    return answer

def swap(arr, i, j):
    arr[i], arr[j] = arr[j], arr[i]
    return arr

FREE_ID = -1

def part2(input):
    compact_nums = [int(c) for c in list(input)]
    arr = []
    free_blocks = 0
    for i, size in enumerate(compact_nums):
        if i % 2 == 0:
            arr.append((i - free_blocks, size))
        else:
            if size != 0:
                arr.append((FREE_ID, size))
            free_blocks += 1

    file_idx = len(arr) - 1
    while file_idx >= 0:
        id, file_size = arr[file_idx]
        if id != FREE_ID:
            for j in range(len(arr)):
                if j >= file_idx:
                    break
                maybe_space_id, space_size = arr[j]
                if maybe_space_id != FREE_ID:
                    continue
                if space_size >= file_size:
                    arr[file_idx] = (FREE_ID, file_size)
                    arr[j] = (id, file_size)
                    if space_size > file_size:
                        arr.insert(j + 1, (FREE_ID, space_size - file_size))
                        file_idx += 1
                    break
        file_idx -= 1

    full_arr = []
    for id, size in arr:
        full_arr += [id] * size
    answer = sum([i * n for i, n in enumerate(full_arr) if n != -1])
    return answer

def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        ans = part1(example)
        assert ans == 1928, "Expected 1928, Got: {}".format(ans)
        print(f'Pt1(example)::ans: {ans}')

    with timer():
        ans = part1(input)
        assert ans == 6334655979668, "Got {}".format(ans)
        print(f'Pt1::ans: {ans}')

    with timer():
        ans = part2(example)
        assert ans == 2858, "Got: {}".format(ans)
        print(f'Pt2_v2(example)::ans: {ans}')

    with timer():
        ans = part2(input)
        assert ans < 6377480737853, "Answer too high: {}".format(ans)
        assert ans == 6349492251099
        print(f'Pt2::ans: {ans}')


if __name__ == "__main__":
    run()
