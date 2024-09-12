#include <string>
#include <iostream>
#include "src/days/index.h"
#include "utils.h"
#include <chrono>

int main(int argc, char *argv[]) {
    if (argc > 2) {
        auto mode = Utils::parseMode(std::string(argv[2]));
        auto start = std::chrono::high_resolution_clock::now();

        if (std::string(argv[1]) == "1") {
            Days::run1(mode);
        } else {
            throw std::runtime_error("Cannot find function for: " + std::string(argv[1]));
        }

        auto end = std::chrono::high_resolution_clock::now();
        auto diff = std::chrono::duration_cast<std::chrono::microseconds>(end - start);
        std::cout << "Time taken: " << diff.count() << " µs" << std::endl;
    } else {
        std::cerr << "Usuage: ./aoc <day-to-run: 1-25> <mode: example|input>" << std::endl;
    }
    return 0;
}
