#include <string>
#include <iostream>
#include "src/days/index.h"
#include "utils.h"

int main(int argc, char *argv[]) {
    if (argc > 2) {
        auto mode = Utils::parseMode(std::string(argv[2]));
        if (std::string(argv[1]) == "1") {
            Days::run1(mode);
        } else {
            throw std::runtime_error("Cannot find function for: " + std::string(argv[1]));
        }
    } else {
        std::cerr << "Usuage: ./aoc <day-to-run: 1-25> <mode: example|input>" << std::endl;
    }
    return 0;
}
