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
        } else if (std::string(argv[1]) == "4") {
            Days::run4(mode, part);
        } else if (std::string(argv[1]) == "5") {
            Days::run5(mode, part);
        } else if (std::string(argv[1]) == "6") {
            Days::run6(mode, part);
        } else if (std::string(argv[1]) == "7") {
            Days::run7(mode, part);
        } else if (std::string(argv[1]) == "8") {
            Days::run8(mode, part);
        } else if (std::string(argv[1]) == "9") {
            Days::run9(mode, part);
        } else if (std::string(argv[1]) == "10") {
            Days::run10(mode, part);
        } else if (std::string(argv[1]) == "11") {
            Days::run11(mode, part);
        } else if (std::string(argv[1]) == "12") {
            Days::run12(mode, part);
        } else if (std::string(argv[1]) == "13") {
            Days::run13(mode, part);
        } else if (std::string(argv[1]) == "14") {
            Days::run14(mode, part);
        } else if (std::string(argv[1]) == "15") {
            Days::run15(mode, part);
        } else if (std::string(argv[1]) == "16") {
            Days::run16(mode, part);
        } else if (std::string(argv[1]) == "17") {
            Days::run17(mode, part);
        } else if (std::string(argv[1]) == "18") {
            Days::run18(mode, part);
        } else if (std::string(argv[1]) == "19") {
            Days::run19(mode, part);
        } else if (std::string(argv[1]) == "20") {
            Days::run20(mode, part);
        } else if (std::string(argv[1]) == "21") {
            Days::run21(mode, part);
        } else if (std::string(argv[1]) == "22") {
            Days::run22(mode, part);
        } else if (std::string(argv[1]) == "23") {
            Days::run23(mode, part);
        } else if (std::string(argv[1]) == "24") {
            Days::run24(mode, part);
        } else if (std::string(argv[1]) == "25") {
            Days::run25(mode, part);
        } else {
            throw std::runtime_error("Cannot find function for: " + std::string(argv[1]));
        }

        auto end = std::chrono::high_resolution_clock::now();
        auto diff = std::chrono::duration_cast<std::chrono::microseconds>(end - start);
        auto diffMs = std::chrono::duration_cast<std::chrono::milliseconds>(end - start);
        std::cout << "Time taken: " << diff.count() << " µs" << " (" << diffMs.count() << " ms)" << std::endl;
    } else if (argc == 2 && std::string(argv[1]) == "test") {
        test_matrix();
    } else {
        std::cerr << "Usuage: ./aoc <day-to-run: 1-25> <part: 1|2> <mode: example|input>" << std::endl;
    }
    return 0;
}
