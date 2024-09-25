#include "index.h"
#include "utils.h"
#include "matrix.h"
#include <set>
#include <iterator>
#include <algorithm>
#include <tuple>
#include <queue>
#include <utility> // for std::pair

using std::vector;
namespace Day17 {
//    std::string example = R"(011112
//999919
//999915)";
//    std::string example = R"(111111111111
//999999999991
//999999999991
//999999999991
//999999999991)";

// pt2 => too low
    std::string example = R"(2413432311323
3215453535623
3255245654254
3446585845452
4546657867536
1438598798454
4457876987766
3637877979653
4654967986887
4564679986453
1224686865563
2546548887735
4322674655533)";

    enum class Dir {
        Up, Down, Left, Right, None
    };

    struct Point {
        long x{-1};
        long y{-1};

        bool operator<(const Point &other) const {
            return std::tie(x, y) < std::tie(other.x, other.y);
        }
    };

    struct Node {
        Point point{};
        int num_in_same_dir{0};
        Dir dir;

        bool operator<(const Node &other) const {
            return std::tie(point.x, point.y, num_in_same_dir, dir) <
                   std::tie(other.point.x, other.point.y, other.num_in_same_dir, other.dir);
        }
    };

    auto cmp = [](const std::pair<Node, int> &left, const std::pair<Node, int> &right) {
        return left.second > right.second;
    };
    // auto [node, cost] = next_nodes.top(); // similar to peek(), doesn't remove
    // auto [node, cost] = next_nodes.pop(); // removes element
    // next_nodes.push({node, cost}); // add new node-cost pair
    // next_nodes.emplace(node, cost); // cheaper push
    // next_nodes.empty();
    // next_nodes.size();

    //  if (visited.find(node1) != visited.end()) {
    //        std::cout << "Node1 has been visited.\n";
    //    }
    std::map<std::pair<long, long>, Dir> delta_to_dir{{{-1, 0},  Dir::Left},
                                                      {{1,  0},  Dir::Right},
                                                      {{0,  -1}, Dir::Up},
                                                      {{0,  1},  Dir::Down}};

    vector<Node> get_nbrs(const Node &node, const Matrix<int, long> &heat, const std::set<Node> &visited) {
        long max_in_same_dir = 3;

        auto nbr_xys = Utils::get_nbrs(node.point.x, node.point.y, heat.x_max, heat.y_max);
        vector<Node> nbr_nodes{};
        for (const auto &[new_x, new_y]: nbr_xys) {
            if (new_x == 0 && new_y == 0) {
                continue; // we don't want to go back to first node, even though it hasn't been seen in given direction
            }

            std::pair<long, long> delta = {new_x - node.point.x, new_y - node.point.y};
            auto new_dir = delta_to_dir[delta];
            if ((node.dir == Dir::Right && new_dir == Dir::Left) || (node.dir == Dir::Left && new_dir == Dir::Right) ||
                (node.dir == Dir::Up && new_dir == Dir::Down) || (node.dir == Dir::Down && new_dir == Dir::Up)) {
                continue; // don't backtrack
            }

            if (new_dir == node.dir && node.num_in_same_dir >= max_in_same_dir) {
                continue;
            }

            auto next_node = Node{{new_x, new_y}, new_dir == node.dir ? node.num_in_same_dir + 1 : 1, new_dir};
            if (visited.contains(next_node)) {
                continue;
            }
            nbr_nodes.emplace_back(next_node);
        }
        return nbr_nodes;
    }

    vector<Node> get_nbrs_ii(const Node &node, const Matrix<int, long> &heat, const std::set<Node> &visited) {
        long min_in_same_dir = 4;
        long max_in_same_dir = 10;

        vector<Node> nbr_nodes{};
        if (node.point.x != 0 && node.point.y != 0 && node.num_in_same_dir < min_in_same_dir) {
            long new_x = node.point.x;
            long new_y = node.point.y;
            if (node.dir == Dir::Right) {
                ++new_x;
            } else if (node.dir == Dir::Left) {
                --new_x;
            } else if (node.dir == Dir::Up) {
                --new_y;
            } else if (node.dir == Dir::Down) {
                ++new_y;
            }
            nbr_nodes.emplace_back(Node{{new_x, new_y}, node.num_in_same_dir + 1, node.dir});
            return nbr_nodes;
        }
        auto nbr_xys = Utils::get_nbrs(node.point.x, node.point.y, heat.x_max, heat.y_max);
        for (const auto &[new_x, new_y]: nbr_xys) {
            if (new_x == 0 && new_y == 0) {
                continue; // we don't want to go back to first node, even though it hasn't been seen in given direction
            }

            std::pair<long, long> delta = {new_x - node.point.x, new_y - node.point.y};
            auto new_dir = delta_to_dir[delta];
            if ((node.dir == Dir::Right && new_dir == Dir::Left) || (node.dir == Dir::Left && new_dir == Dir::Right) ||
                (node.dir == Dir::Up && new_dir == Dir::Down) || (node.dir == Dir::Down && new_dir == Dir::Up)) {
                continue; // don't backtrack
            }

            int new_num_in_same_dir = new_dir == node.dir ? node.num_in_same_dir + 1 : 1;
            auto next_node = Node{{new_x, new_y}, new_num_in_same_dir, new_dir};

            if (next_node.num_in_same_dir > max_in_same_dir) {
                continue;
            }

            // if the node is going in a given direction, and it
            // can't do a total of min_in_same_dir in that direction,
            // then it's not a valid nbr
            // 0 1 2 3 4 5
            // - - <
            long distance_to_edge{};
            if (next_node.dir == Dir::Right) {
                distance_to_edge = heat.x_max - next_node.point.x;
            } else if (next_node.dir == Dir::Left) {
                distance_to_edge = next_node.point.x;
            } else if (next_node.dir == Dir::Down) {
                distance_to_edge = heat.y_max - next_node.point.y;
            } else if (next_node.dir == Dir::Up) {
                distance_to_edge = next_node.point.y;
            }

            if ((distance_to_edge + next_node.num_in_same_dir) < min_in_same_dir) {
                continue;
            }

            if (visited.contains(next_node)) {
                continue;
            }
            nbr_nodes.emplace_back(next_node);
        }
        return nbr_nodes;
    }

    enum class Mode {
        Part1, Part2
    };

    int
    get_heat_loss(const Matrix<int, long> &heat, const Point &start, const Point &end, const Mode &mode = Mode::Part1) {
        std::priority_queue<std::pair<Node, int>, std::vector<std::pair<Node, int>>, decltype(cmp)> next_nodes(cmp);

        const Node start_node{start, 0, Dir::None};
        next_nodes.emplace(start_node, 0);

        std::set<Node> visited{};
        // consider hash set (unordered_set) for better perf, will need a hash_fn for node
        //    0 1 2 3 4 5
        //    -----------
        // 0| 0 1 1 1 1 2
        // 1| 9 9 9 9 1 9
        // 2| 9 9 9 9 1 5
        while (!next_nodes.empty()) {
//            auto [node, cost] = next_nodes.top();
            auto foo = next_nodes.top();
            auto node = foo.first;
            auto cost = foo.second;
            auto nx = node.point.x;
            auto ny = node.point.y;
            auto dir = node.dir;
            auto m = node.num_in_same_dir;
            next_nodes.pop();
            if (visited.contains(node)) {
                continue;
            }
            visited.insert(node);

            vector<Node> nbrs{};
            if (mode == Mode::Part1) {
                nbrs = get_nbrs(node, heat, visited);
            }
            if (mode == Mode::Part2) {
                nbrs = get_nbrs_ii(node, heat, visited);
            }

            for (const auto &nbr: nbrs) {
                if (nbr.point.x == end.x && nbr.point.y == end.y) {
                    return cost + heat.at(end.x, end.y);
                }
                next_nodes.emplace(nbr, cost + heat.at(nbr.point.x, nbr.point.y));
            }
        }
        return -1;
    }


    void part_ii(std::string const &input) {
        auto city = Utils::buildIntMatrix(input);
        auto ans = get_heat_loss(city, Point{0, 0}, Point{city.x_size - 1, city.y_size - 1}, Mode::Part2);
        std::cout << "The answer is: " << ans << std::endl;
    }

    void part_i(std::string const &input) {
        auto city = Utils::buildIntMatrix(input);
        auto ans = get_heat_loss(city, Point{0, 0}, Point{city.x_size - 1, city.y_size - 1});
        std::cout << "The answer is: " << ans << std::endl;
    }
}

void Days::run17(Utils::Mode mode, int part) {
    std::string input;
    switch (mode) {
        case Utils::Mode::Example:
            input = Day17::example;
            break;
        case Utils::Mode::Input:
            input = Utils::readFile("./input/17.txt");
            break;
        default:
            throw std::runtime_error("No option found for mode");
    }
    if (part == 1) {
        return Day17::part_i(input);
    }
    if (part == 2) {
        return Day17::part_ii(input);
    }

    throw std::runtime_error("No function for part: " + std::to_string(part));
}