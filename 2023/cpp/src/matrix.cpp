#include "matrix.h"
#include <string>
#include <iostream>

void test_matrix() {
    Matrix<std::string, long> basic = Matrix<std::string, long>({"a", "b", "c", "d", "e", "f"}, 3, 2, {false});
    my_assert(basic.inBounds(-1, 0) == false, "01");
    my_assert(basic.inBounds(0, -1) == false, "02");
    my_assert(basic.inBounds(0, 0), "1");
    my_assert(basic.inBounds(2, 1), "2");
    my_assert(basic.inBounds(3, 2) == false, "3");
    my_assert(basic.inBounds(3, 1) == false, "4");
    my_assert(basic.at(0, 0) == "a", "5");
    my_assert(basic.at(2, 1) == "f", "6");

    Matrix<std::string, long> repeatX = Matrix<std::string, long>({"a", "b", "c", "d", "e", "f"}, 3, 2, {true});
    std::cerr << repeatX << std::endl;
    my_assert(repeatX.inBounds(1000000000, 1), "FAIL: Any X should be in bounds");
    my_assert(repeatX.inBounds(1000000000, -1) == false, "FAIL: Y should still be bounds tested");
    my_assert(repeatX.inBounds(1000000000, 2) == false, "FAIL: Y should still be bounds tested (2)");
    my_assert(repeatX.at(0, 0) == "a", "Fail: expected (0,0) to work");
    my_assert(repeatX.at(3, 0) == "a", "FAIL: expected x to wrap around(1): " + repeatX.at(3, 0));
    my_assert(repeatX.at(5, 0) == "c", "FAIL: expected x to wrap around(2): " + repeatX.at(3, 0));
    my_assert(repeatX.at(6, 0) == "a", "FAIL: expected x to wrap around twice: " + repeatX.at(3, 0));

    // throws
    try {
        Matrix<std::string, long> empty = Matrix<std::string, long>({}, 0, 0, {false});
        my_assert(false, "Expected empty matrix to refuse to be initialised");
    } catch (...) {

    }

    try {
        Matrix<std::string, long> mis_sized = Matrix<std::string, long>({"a", "b", "c"}, 2, 1, {false});
        my_assert(false, "Expected mis-sized matrix to refuse to be initialised");
    } catch (...) {

    }
}