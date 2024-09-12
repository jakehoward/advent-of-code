#include <string>
#include <iostream>
#include "src/days/index.h"

int main(int argc, char *argv[]) {
    if (argc > 1) {
        if (std::string(argv[1]) == "1") {
            Days::run1();
        } else {
            throw std::runtime_error("Cannot find function for: " + std::string(argv[1]));
        }
    } else {
        std::cerr << "Usuage: ./aoc <day-to-run>" << std::endl;
    }
    return 0;
}
