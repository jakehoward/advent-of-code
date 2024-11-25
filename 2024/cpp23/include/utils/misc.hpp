#pragma once

#include <ranges>
#include <vector>
#include <string>
#include <string_view>
#include <regex>
#include <print>
#include <fstream>
#include <format>
#include <exception>
#include <functional>

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
                   return std::string_view(rng.begin(), rng.end()); })
               | std::ranges::to<std::vector<std::string_view>>();
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

    void print_lines(const std::vector<std::string_view> &lines) {
        for(const auto &line: lines) {
            std::println("{}", line);
        }
    }

    void with_timer(const std::string &name, std::function<void()> f) {
        auto start = std::chrono::high_resolution_clock::now();
        f();
        auto end = std::chrono::high_resolution_clock::now();
        auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count();
        auto suffix = "ms";
        if (duration == 0) {
            duration = std::chrono::duration_cast<std::chrono::microseconds>(end - start).count();
            suffix = "microseconds";
        }
        std::println("{} took {} {}", name, duration, suffix);
    }

    void with_timer(std::function<void()> f) {
        with_timer("", f);
    }

}