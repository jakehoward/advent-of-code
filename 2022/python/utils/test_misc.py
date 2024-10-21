import pytest

from utils.misc import partition


class TestPartition:
    def test_empty(self):
        assert (partition(2, 1, []) == [])

    def test_one(self):
        assert (partition(2, 1, [1]) == [])

    def test_two(self):
        assert (partition(2, 1, [1, 2]) == [[1, 2]])

    def test_three(self):
        assert (partition(2, 1, [1, 2, 3]) == [[1, 2], [2, 3]])

    def test_many(self):
        assert (partition(2, 1, [1, 2, 3, 4, 5]) == [[1, 2], [2, 3], [3, 4], [4, 5]])

    def test_chunk_size_three(self):
        assert (partition(3, 1, []) == [])
        assert (partition(3, 1, [1]) == [])
        assert (partition(3, 1, [1, 2]) == [])
        assert (partition(3, 1, [1, 2, 3]) == [[1, 2, 3]])
        assert (partition(3, 1, [1, 2, 3, 4, 5]) == [[1, 2, 3], [2, 3, 4], [3, 4, 5]])

    def test_step_size_two(self):
        assert (partition(2, 2, []) == [])
        assert (partition(2, 2, [1, 2]) == [[1, 2]])
        assert (partition(2, 2, [1, 2, 3]) == [[1, 2]])
        assert (partition(2, 2, [1, 2, 3, 4]) == [[1, 2], [3, 4]])
        assert (partition(2, 2, [1, 2, 3, 4, 5]) == [[1, 2], [3, 4]])
