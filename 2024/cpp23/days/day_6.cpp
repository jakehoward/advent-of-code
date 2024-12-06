#include <string>
#include <unordered_set>
#include <utility>
#include <vector>
#include <thread>
#include <cstdint>
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

    [[nodiscard]] char at(uint32_t x, uint32_t y) const {
        return s.at(line_length * y + x);
    }

    [[nodiscard]] bool in_bounds(uint32_t x, uint32_t y) const {
        return y < num_lines && x < (line_length - 1);
    }

    [[nodiscard]] std::pair<uint32_t, uint32_t> find(char c) const {
        auto idx = s.find(c);
        uint32_t x = idx % line_length;
        uint32_t y = idx / line_length;

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

std::pair<uint32_t, uint32_t> move(const std::pair<uint32_t, uint32_t> &p, Dir d) {
    if (d == Dir::Up)
        return {p.first, p.second - 1};
    if (d == Dir::Down)
        return {p.first, p.second + 1};
    if (d == Dir::Left)
        return {p.first - 1, p.second};
    if (d == Dir::Right)
        return {p.first + 1, p.second};
}

void fast_move(const std::array<uint32_t, 2> &p, Dir d, std::array<uint32_t, 2> &dest) {
    if (d == Dir::Up) {
        dest[0] = p[0];
        dest[1] = p[1] - 1;
    }
    if (d == Dir::Down) {
        dest[0] = p[0];
        dest[1] = p[1] + 1;
    }
    if (d == Dir::Left) {
        dest[0] = p[0] - 1;
        dest[1] = p[1];
    }
    if (d == Dir::Right) {
        dest[0] = p[0] + 1;
        dest[1] = p[1];
    }
}

std::unordered_set<std::pair<uint32_t, uint32_t>, PairHash>
get_visited_locations(const StringGrid &grid, const std::pair<uint32_t, uint32_t> &start, const Dir &start_dir) {
    auto pos = start;
    auto dir = start_dir;
    std::unordered_set<std::pair<uint32_t, uint32_t>, PairHash> seen{};
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

bool is_loop(const StringGrid &grid, const std::pair<uint32_t, uint32_t> &start_pos, const Dir &start_direction,
             const std::pair<uint32_t, uint32_t> &obstacle_pos) {

    std::array<uint32_t, 2> pos{start_pos.first, start_pos.second};
    std::array<uint32_t, 2> next_pos{0, 0};
    auto dir = start_direction;
    std::vector<bool> seen(grid.line_length * grid.num_lines * 4, false); // 4 directions
    while (grid.in_bounds(pos[0], pos[1])) {
        const auto dir_idx = static_cast<size_t>(dir);
        uint64_t seen_idx = (grid.line_length * pos[1] + pos[0]) * 4 + dir_idx;
        if (seen[seen_idx]) {
            return true;
        }
        seen[seen_idx] = true;

        fast_move(pos, dir, next_pos);
        if (grid.in_bounds(next_pos[0], next_pos[1]) &&
            (grid.at(next_pos[0], next_pos[1]) == '#' ||
             (next_pos[0] == obstacle_pos.first && next_pos[1] == obstacle_pos.second))) {
            dir = turn_right(dir);
        } else {
            pos[0] = next_pos[0];
            pos[1] = next_pos[1];
        }
    }
    return false;
}

void part_ii(const std::string &input) {
    auto grid = StringGrid(input);
    const auto guard_start = grid.find('^');

    auto visited_locations = get_visited_locations(grid, guard_start, Dir::Up);

    uint32_t num = 0;
    uint32_t num_batches = 5;
    std::vector<std::unordered_set<std::pair<uint32_t, uint32_t>, PairHash>> batches(num_batches);
    for (const auto &visited_location: visited_locations) {
        batches.at(num % num_batches).insert(visited_location);
        num += 1;
    }

    std::vector<std::thread> threads;
    threads.reserve(num_batches);
    std::vector<std::unordered_set<std::pair<uint32_t, uint32_t>, PairHash>> thread_results(num_batches);
    for (uint32_t i = 0; i < num_batches; ++i) {
        threads.emplace_back([&grid, &guard_start, &batch = batches[i], i, &thread_results]() {
            for (const auto &visited_location: batch) {
                if (is_loop(grid, guard_start, Dir::Up, visited_location)) {
                    thread_results[i].insert(visited_location);
                }
            }
        });
    }

    for (auto &t: threads) {
        t.join();
    }
    size_t total = 0;
    for (const auto &result: thread_results) {
        total += result.size();
    }

    std::println("Part i: {}", total);
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