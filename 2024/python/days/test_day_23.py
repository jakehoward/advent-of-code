import pytest
from days.day_21 import complexity, code_pad_to_robot_options_tree, up, left, press, down, right, get_all_paths, \
    get_shortest_path
from days.day_23 import get_densely_connected_clusters, add_edge, edge_exists


class TestDay23:
    def test_graph(self):
        graph = {}
        add_edge(graph, 'a', 'b')
        assert ([edge_exists(graph, 'a', 'b'), edge_exists(graph, 'b', 'a')] == [True, True])