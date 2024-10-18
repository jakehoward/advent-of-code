def run():
    r = range(-10,11)
    m = map(lambda n: n % 3, r)

    print('\t'.join(map(str, r)))
    print('\t'.join(map(str, m)))

if __name__ == '__main__':
    run()