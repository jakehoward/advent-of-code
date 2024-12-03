#include <string>
#include <string_view>
#include <charconv>

#include <utils/misc.hpp>

std::string example_2(R"(xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5)))");

bool can_parse_to_int(std::string_view sv) {
    int result;
    auto [ptr, ec] = std::from_chars(sv.data(), sv.data() + sv.size(), result);
    return ec == std::errc() && ptr == sv.data() + sv.size();
}

long to_long(std::string_view sv) {
    return std::strtol(sv.data(), nullptr, 10);
}

void part_ii(const std::string &input) {
    size_t pos = 0;
    bool on = true;
    std::string_view view(input);
    auto len = input.length();
    long ans = 0;
    while (pos < len) {
        if (view.substr(pos, 4) == "do()") {
            on = true;
            pos += 4;
        } else if (view.substr(pos, 7) == "don't()") {
            on = false;
            pos += 7;
        } else if (on) {
            if (view.substr(pos).starts_with("mul(")) {
                pos += 4;
                auto next_comma_idx = view.substr(pos).find(',');
                auto next_paren_idx = view.substr(pos).find(')');
                auto mul_len = next_paren_idx;
                if (next_comma_idx != std::string_view::npos && next_paren_idx != std::string_view::npos &&
                    next_comma_idx >= 1 &&
                    next_paren_idx > next_comma_idx && 3 <= mul_len && mul_len <= 7) {
                    auto first = view.substr(pos, next_comma_idx);
                    auto second = view.substr(pos + next_comma_idx + 1, next_paren_idx - next_comma_idx - 1);
                    if (can_parse_to_int(first) && can_parse_to_int(second)) {
                        ans += to_long(first) * to_long(second);
                        pos += mul_len;
                    }
                }
            } else {
                ++pos;
            }
        } else {
            ++pos;
        }
    }
    std::println("Part ii: {}", ans);
}

int main() {
    auto input = Utils::read_input(3);

    Utils::with_timer("Part ii (example)", []() {
        part_ii(example_2);
    });

    Utils::with_timer("Part ii (input)", [&input]() {
        part_ii(input);
    });
}