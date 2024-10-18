from utils.grid import make_grid
from utils.viz.viz_grid import viz_grid


def run():
    # r = range(-10,11)
    # m = map(lambda n: n % 3, r)
    #
    # print('\t'.join(map(str, r)))
    # print('\t'.join(map(str, m)))
    viz_grid(make_grid('101\n010', as_ints=True))

if __name__ == '__main__':
    run()