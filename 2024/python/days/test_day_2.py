import pytest
from days.day_2 import one_missing_perms

class TestDay2:
    def test_missing_perms(self):
        assert (one_missing_perms([1,2,3]) == [[2, 3], [1, 3], [1, 2]])