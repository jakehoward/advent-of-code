from itertools import permutations, combinations
from pathlib import Path

from utils.misc import timer
from utils.read import read_input

example = """
kh-tc
qp-kh
de-cg
ka-co
yn-aq
qp-ub
cg-tb
vc-aq
tb-ka
wh-tc
yn-cg
kh-ub
ta-co
de-co
tc-td
tb-wq
wh-td
ta-ka
td-qp
aq-cg
wq-ub
ub-vc
de-ta
wq-aq
wq-vc
wh-yn
ka-de
kh-ta
co-tc
wh-qp
tb-vc
td-yn""".strip()


def parse(input):
    return [tuple(l.split('-')) for l in input.splitlines()]


def add_edge(graph, a, b):
    if a in graph:
        graph[a].add(b)
    else:
        graph[a] = {b}
    if b in graph:
        graph[b].add(a)
    else:
        graph[b] = {a}


def edge_exists(graph, a, b):
    return a in graph and b in graph[a]


def build_graph_from_pairs(pairs):
    graph = {}
    for a, b in pairs:
        add_edge(graph, a, b)
    return graph


def get_all_nodes(graph):
    return list(graph.keys())


def get_densely_connected_clusters(pairs):
    graph = build_graph_from_pairs(pairs)

    all_nodes = list(graph.keys())
    clusters = [{n} for n in all_nodes]
    for node in all_nodes:
        new_clusters = []
        for cluster in clusters:
            if len(cluster.intersection(graph[node])) == len(cluster):
                cluster.add(node)
            else:
                new_clusters.append(cluster.intersection(graph[node]))
                new_clusters.append(cluster.difference(graph[node]))
        clusters += [c for c in new_clusters if len(c)]

    clusters = {tuple(sorted(list(c))) for c in clusters}
    new_clusters = []
    for cluster in clusters:
        for i in range(1, len(cluster)):
            for p in permutations(cluster, i):
                sorted_p = tuple(sorted(list(p)))
                if sorted_p not in clusters:
                    new_clusters.append(sorted_p)

    clusters |= set(new_clusters)

    return sorted([set(c) for c in clusters], key=len, reverse=True)


def get_three_clusters(pairs):
    graph = build_graph_from_pairs(pairs)
    all_nodes = get_all_nodes(graph)
    three_combinations = combinations(all_nodes, 3)

    three_clusters = []
    for a, b, c in three_combinations:
        if edge_exists(graph, a, b) and edge_exists(graph, a, c) and edge_exists(graph, b, c):
            three_clusters.append({a, b, c})

    return three_clusters


def find_largest_cluster(graph):
    # greedy search, start with node and consume all densely connected nodes
    all_nodes = get_all_nodes(graph)
    largest_cluster = {}
    for node in all_nodes:
        dense_cluster = {node}
        for connected_node in graph[node]:
            if all([edge_exists(graph, connected_node, cn) for cn in dense_cluster]):
                dense_cluster.add(connected_node)
        if len(dense_cluster) > len(largest_cluster):
            largest_cluster = dense_cluster
    return largest_cluster



def part1(input):
    pairs = parse(input)
    three_clusters = get_three_clusters(pairs)
    three_ts = [c for c in three_clusters if any([n.startswith('t') for n in c])]
    return len(three_ts)


def part2(input):
    graph = build_graph_from_pairs(parse(input))
    largest_cluster = find_largest_cluster(graph)
    return ','.join(sorted(list(largest_cluster)))


def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        ans = part1(example)
        assert ans == 7, "Got: {}".format(ans)
        print(f'Pt1(example)::ans: {ans}')
        ans = None

    with timer():
        ans = part1(input)
        assert ans == 1330, "Got: {}".format(ans)
        print(f'Pt1::ans: {ans}')
        ans = None

    with timer():
        ans = part2(example)
        assert ans == 'co,de,ka,ta', "Got: {}".format(ans)
        print(f'Pt2(example)::ans: {ans}')
        ans = None

    with timer():
        ans = part2(input)
        assert ans == 'hl,io,ku,pk,ps,qq,sh,tx,ty,wq,xi,xj,yp', "Got: {}".format(ans)
        print(f'Pt2::ans: {ans}')
        ans = None


if __name__ == "__main__":
    run()
