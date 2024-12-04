#include <string>
#include <utils/misc.hpp>

std::string example_2(R"(.M.S......
..A..MSMS.
.M.S.MAA..
..A.ASMSM.
.M.S.M....
..........
S.S.S.S.S.
.A.A.A.A..
M.M.M.M.M.
..........)");

void part_i(const std::string &input) {
    std::println("Part i: {}", "Not implemented");
}


void part_ii(const std::string &input) {
//    std::string_view grid(input);
    std::array<char, 5> mas_1{'M', 'S', 'A', 'M', 'S'};
    std::array<char, 5> mas_2{'M', 'M', 'A', 'S', 'S'};
    std::array<char, 5> mas_3{'S', 'M', 'A', 'S', 'M'};
    std::array<char, 5> mas_4{'S', 'S', 'A', 'M', 'M'};
    std::array<std::array<char, 5>, 4> mases_to_check{mas_1, mas_2, mas_3, mas_4};

    size_t block_len = input.find('\n') + 1;
    std::array<size_t, 5> idxs_to_check{0, 2, block_len + 1, 2 * block_len, 2 * block_len + 2};
    int answer = 0;
    while (idxs_to_check[4] < input.length()) {
        for (const auto &mas: mases_to_check) {
            if (mas[0] == input[idxs_to_check[0]] &&
                mas[1] == input[idxs_to_check[1]] &&
                mas[2] == input[idxs_to_check[2]] &&
                mas[3] == input[idxs_to_check[3]] &&
                mas[4] == input[idxs_to_check[4]]
            ) {
                answer += 1;
            }
        }
        bool go_to_next_line = (idxs_to_check[1] % block_len) == (block_len - 2);
        for (auto &n: idxs_to_check) {
            if (go_to_next_line) {
                n += 2;
            } else {
                n +=1;
            }
        }
    }

    std::println("Part ii: {}", answer);
}

int main() {
    auto input = Utils::read_input(4);
//    Utils::with_timer("Parse input",  [&input](){ parse_input(input); });

//    Utils::with_timer("Part i (example)", []() {
//        part_i(example);
//    });

//    Utils::with_timer("Part i (input)", [&input]() {
//        part_i(input);
//    });

    Utils::with_timer("Part ii (example)", []() {
        part_ii(example_2);
    });

    Utils::with_timer("Part ii (input)", [&input]() {
        part_ii(input);
    });
}