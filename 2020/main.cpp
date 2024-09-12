#include <string>
#include <iostream>
#include "src/days/index.h"
#include "utils.h"
#include "matrix.h"
#include <chrono>

int main(int argc, char *argv[]) {
    if (argc > 3) {
        auto part = std::stoi(std::string(argv[2]));
        auto mode = Utils::parseMode(std::string(argv[3]));
        auto start = std::chrono::high_resolution_clock::now();

        if (std::string(argv[1]) == "1") {
            Days::run1(mode, part);
        } else if (std::string(argv[1]) == "2") {
            Days::run2(mode, part);
        } else if (std::string(argv[1]) == "3") {
            Days::run3(mode, part);
        } else {
            throw std::runtime_error("Cannot find function for: " + std::string(argv[1]));
        }

        auto end = std::chrono::high_resolution_clock::now();
        auto diff = std::chrono::duration_cast<std::chrono::microseconds>(end - start);
        std::cout << "Time taken: " << diff.count() << " µs" << std::endl;
    } else if (argc == 2 && std::string(argv[1]) == "test") {
        test_matrix();
    } else {
        std::cerr << "Usuage: ./aoc <day-to-run: 1-25> <part: 1|2> <mode: example|input>" << std::endl;
    }
    return 0;
}
