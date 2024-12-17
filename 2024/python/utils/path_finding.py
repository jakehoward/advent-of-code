from heapq import heappush, heappop
import math

# https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
# (Indentation mine)
#  1 function Dijkstra(Graph, source):
#  2
#  3 for each vertex v in Graph.Vertices:
#  4    dist[v] ← INFINITY
#  5    prev[v] ← UNDEFINED
#  6    add v to Q
#  7    dist[source] ← 0
#  8
#  9 while Q is not empty:
# 10    u ← vertex in Q with minimum dist[u]
# 11    remove u from Q
# 12
# 13    for each neighbor v of u still in Q:
# 14        alt ← dist[u] + Graph.Edges(u, v)
# 15        if alt < dist[v]:
# 16        dist[v] ← alt
# 17        prev[v] ← u
# 18        add v to Q with distance alt
# 19
# 20 return dist[], prev[]

def solve_graph_for_single_node(graph, get_all_vertices, get_nbrs, get_cost, start):
    """
    Finds the lowest cost of travelling from start to all other graph vertices and tracks
    all shortest paths from start to a given node.

    :param graph: any, not used directly by algorithm, passed to other functions as context
    :param get_all_vertices: function(graph) -> list<vertex>, get all vertices in the graph
    :param get_nbrs: function(graph, vertex) -> list<vertex>, all possible neighbours from vertex
    :param get_cost: function(vertex, neighbour_vertex) -> Number, the cost of moving from vertex to neighbour
    :param start: a vertex of the graph, costs from here to all other vertices is calculated
    :return: start_to_vertex_cost, vertex_to_prev_vertices
    """
    Q = []
    start_to_vertex_cost = {}
    vertex_to_prev_vertices = {}
    for vertex in get_all_vertices(graph):
        start_to_vertex_cost[vertex] = math.inf
        vertex_to_prev_vertices[vertex] = []
        heappush(Q, (start_to_vertex_cost[vertex], vertex))

    start_to_vertex_cost[start] = 0
    heappush(Q, (0, start))

    while Q:
        cost, vertex = heappop(Q)
        nbr_vertices = [v for v in get_nbrs(graph, vertex) if v in set({v for _, v in Q})]
        print(len(Q))

        for nbr_vertex in nbr_vertices:
            nbr_cost = cost + get_cost(vertex, nbr_vertex)
            if nbr_cost == start_to_vertex_cost[nbr_vertex]:
                vertex_to_prev_vertices[nbr_vertex].append(vertex)
                heappush(Q, (nbr_cost, nbr_vertex))
            elif nbr_cost < start_to_vertex_cost[nbr_vertex]:
                start_to_vertex_cost[nbr_vertex] = nbr_cost
                vertex_to_prev_vertices[nbr_vertex] = [vertex]
                heappush(Q, (nbr_cost, nbr_vertex))
    return start_to_vertex_cost, vertex_to_prev_vertices