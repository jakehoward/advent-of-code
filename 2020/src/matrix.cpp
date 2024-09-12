#include "matrix.h"
#include <string>
#include <iostream>

template<typename Type, typename IndexType>
Type Matrix<Type, IndexType>::at(IndexType const x, IndexType const y) const {
    if (inBounds(x, y)) {
        return data.at((x_size * y) + x);
    }
    if (x >= x_size && config.repeatX) {
        return data.at((x_size * y) + (x % x_size));
    }

    std::string x_size_desc = config.repeatX ? "inf" : std::to_string(x_size);
    std::string err =
            std::string("Attempted to access matrix[") + x_size_desc + "," + std::to_string(y_size) + "] at: (" +
            std::to_string(x) + "," + std::to_string(y) + ")";
    throw std::runtime_error(err);
}

template<typename Type, typename IndexType>
bool Matrix<Type, IndexType>::inBounds(IndexType const x, IndexType const y) const {
    if (config.repeatX) {
        return y < y_size;
    }
    return (x < x_size) && (y < y_size);
};

void test_matrix() {
    Matrix<std::string, long> basic = Matrix<std::string, long>({"a", "b", "c", "d", "e", "f"}, 3, 2, {false});
    my_assert(basic.inBounds(0, 0), "1");
    my_assert(basic.inBounds(2, 1), "2");
    my_assert(basic.inBounds(3, 2) == false, "3");
    my_assert(basic.inBounds(3, 1) == false, "4");
    my_assert(basic.at(0, 0) == "a", "5");
    my_assert(basic.at(2, 1) == "f", "6");

    Matrix<std::string, long> repeatX = Matrix<std::string, long>({"a", "b", "c", "d", "e", "f"}, 3, 2, {true});
    std::cerr << repeatX << std::endl;
    my_assert(repeatX.inBounds(1000000000, 1), "FAIL: Any X should be in bounds");
    my_assert(repeatX.at(0, 0) == "a", "");

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