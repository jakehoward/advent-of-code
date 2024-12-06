#include <string>
#include <unordered_set>
#include <utility>
#include <utils/misc.hpp>

std::string example(R"(....#.....
.........#
..........
..#.......
.......#..
..........
.#..^.....
........#.
#.........
......#...)");

class StringGrid {
public:
    explicit StringGrid(const std::string &s) : s(s),
                                                line_length(s.find('\n') + 1),
                                                num_lines(s.ends_with('\n') ? (s.length() / line_length) : (
                                                        (s.length() + 1) / line_length)) {}

    [[nodiscard]] char at(int x, int y) const {
        return s.at(line_length * y + x);
    }

    [[nodiscard]] bool in_bounds(int x, int y) const {
        return y < num_lines && x < (line_length - 1);
    }

    [[nodiscard]] std::pair<int, int> find(char c) const {
        auto idx = s.find(c);
        int x = idx % line_length;
        int y = idx / line_length;

        return {x, y};
    }

    const std::string &s;
    const size_t line_length;
    const size_t num_lines;
};

enum Dir {
    Up, Down, Left, Right
};

namespace std {
    template<>
    struct hash<Dir> {
        std::size_t operator()(Dir dir) const {
            return static_cast<std::size_t>(dir);
        }
    };
}

struct PairHash {
    template<class T1, class T2>
    std::size_t operator()(const std::pair<T1, T2> &p) const {
        auto h1 = std::hash<T1>{}(p.first);
        auto h2 = std::hash<T2>{}(p.second);
        return h1 ^ (h2 << 1);
    }
};

struct NestedPairHash {
    template<class T1, class T2, class T3>
    std::size_t operator()(const std::pair<std::pair<T1, T2>, T3> &p) const {
        auto h1 = std::hash<T1>{}(p.first.first);
        auto h2 = std::hash<T2>{}(p.first.second);
        auto h3 = std::hash<T3>{}(p.second);
        return h1 ^ (h2 << 1) ^ (h3 << 2);
    }
};

Dir turn_right(Dir dir) {
    if (dir == Dir::Up)
        return Dir::Right;
    if (dir == Dir::Right)
        return Dir::Down;
    if (dir == Dir::Down)
        return Dir::Left;
    if (dir == Dir::Left)
        return Dir::Up;
}

std::pair<int, int> move(const std::pair<int, int> &p, Dir d) {
    if (d == Dir::Up)
        return {p.first, p.second - 1};
    if (d == Dir::Down)
        return {p.first, p.second + 1};
    if (d == Dir::Left)
        return {p.first - 1, p.second};
    if (d == Dir::Right)
        return {p.first + 1, p.second};
}

std::unordered_set<std::pair<int, int>, PairHash>
get_visited_locations(const StringGrid &grid, const std::pair<int, int> &start, const Dir &start_dir) {
    auto pos = start;
    auto dir = start_dir;
    std::unordered_set<std::pair<int, int>, PairHash> seen{};
    while (grid.in_bounds(pos.first, pos.second)) {
        seen.insert(pos);
        auto next_pos = move(pos, dir);
        if (grid.in_bounds(next_pos.first, next_pos.second) && grid.at(next_pos.first, next_pos.second) == '#') {
            dir = turn_right(dir);
        } else {
            pos = next_pos;
        }
    }
    return seen;
}

bool is_loop(const StringGrid &grid, const std::pair<int, int> &start_pos, const Dir &start_direction,
             const std::pair<int, int> &obstacle_pos) {
    std::unordered_set<std::pair<std::pair<int, int>, Dir>, NestedPairHash> seen{};
    auto dir = start_direction;
    auto pos = start_pos;
    while (grid.in_bounds(pos.first, pos.second)) {
        if (seen.contains({pos, dir})) {
            return true;
        }
        seen.insert({pos, dir});

        auto next_pos = move(pos, dir);
        if (grid.in_bounds(next_pos.first, next_pos.second) && (grid.at(next_pos.first, next_pos.second) == '#' || next_pos == obstacle_pos)) {
            dir = turn_right(dir);
        } else {
            pos = next_pos;
        }
    }
    return false;
}

void part_ii(const std::string &input) {
    auto grid = StringGrid(input);
    const auto guard_start = grid.find('^');

    auto visited_locations = get_visited_locations(grid, guard_start, Dir::Up);

    std::unordered_set<std::pair<int, int>, PairHash> loop_locations{};
    for (const auto &visited_location: visited_locations) {
        if (is_loop(grid, guard_start, Dir::Up, visited_location)) {
            loop_locations.insert(visited_location);
        }
    }
    std::println("Part i: {}", loop_locations.size());
}

void part_i(const std::string &input) {
    std::println("Part i: {}", "Not implemented");
}

int main() {
    auto input = Utils::read_input(6);
//    Utils::with_timer("Parse input",  [&input](){ parse_input(input); });

//    Utils::with_timer("Part i (example)", []() {
//        part_i(example);
//    });

//    Utils::with_timer("Part i (input)", [&input]() {
//        part_i(input);
//    });

    Utils::with_timer("Part ii (example)", []() {
        part_ii(example);
    });

    Utils::with_timer("Part ii (input)", [&input]() {
        part_ii(input);
    });
}