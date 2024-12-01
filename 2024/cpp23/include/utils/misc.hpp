#pragma once

#include <ranges>
#include <vector>
#include <string>
#include <sstream>
#include <string_view>
#include <regex>
#include <print>
#include <fstream>
#include <format>
#include <exception>
#include <functional>

#include <cassert>
#define ASSERT(condition, message) do { \
    if (!(condition)) { \
        std::cerr << "Assertion failed: " << message << std::endl; \
        assert(condition); \
    } \
} while (0)

namespace Utils {
    std::string read_input(int day_number) {
        auto filename = std::format("../input/{}.txt", day_number);
        std::ifstream file(filename, std::ios::binary | std::ios::ate);
        if (!file) {
            throw std::runtime_error("Unable to open file: " + filename);
        }

        auto size = file.tellg();
        file.seekg(0, std::ios::beg);

        std::string input;
        input.resize(size);

        if (!file.read(input.data(), size)) {
            throw std::runtime_error("Error reading file: " + filename);
        }

        return input;
    }

    /**
     * Split input into lines. The input MUST OUTLIVE THE USE OF THE LINES!!
     * @param input
     * @return A vector of views into the original string, where each view is a line
     */
    std::vector<std::string_view> view_lines(std::string_view input) {
        return input
               | std::ranges::views::split('\n')
               | std::ranges::views::transform([](auto &&rng) {
            return std::string_view(rng.begin(), rng.end());
        })
               | std::ranges::to<std::vector<std::string_view>>();
    }

//    std::vector<std::string> split_lines(const std::string &input) {
//        return input | std::ranges::views::split('\n') | std::ranges::to<std::vector<std::string>>();
//    }

    std::vector<std::string> split_lines(const std::string& str) {
        auto result = std::vector<std::string>{};
        auto ss = std::stringstream{str};

        for (std::string line; std::getline(ss, line, '\n');) {
            result.push_back(line);
        }

        return result;
    }

    std::vector<std::string> split_regex(const std::string &input, const std::string &pattern) {
        std::vector<std::string> tokens;
        std::regex re(pattern);

        std::sregex_token_iterator it(input.begin(), input.end(), re, -1);
        std::sregex_token_iterator end;

        while (it != end) {
            tokens.push_back(*it++);
        }

        return tokens;
    }

    /**
     * Splits string on whitespace, not including newlines
     * @param input string
     *
     * @return vector of strings
     */
    std::vector<std::string> split_whitespace(const std::string &input) {
        std::vector<std::string> tokens{};
        std::stringstream ss{input};
        std::string t;
        while(ss >> t) {
            tokens.push_back(t);
        }

        return tokens;
    }

    void print_lines(const std::vector<std::string_view> &lines) {
        for (const auto &line: lines) {
            std::println("{}", line);
        }
    }

    void with_timer(const std::string &name, std::function<void()> f) {
        auto start = std::chrono::high_resolution_clock::now();
        f();
        auto end = std::chrono::high_resolution_clock::now();
        auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count();
        auto suffix = "ms";
        if (duration < 5) {
            duration = std::chrono::duration_cast<std::chrono::microseconds>(end - start).count();
            suffix = "microseconds";
        }
        std::println("{} took {} {}", name, duration, suffix);
    }

    void with_timer(std::function<void()> f) {
        with_timer("", f);
    }

}