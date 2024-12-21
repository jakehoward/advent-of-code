import pytest
from days.day_21 import complexity, code_pad_to_robot, arrow_pad_to_robot

class TestDay21:
    def test_complexity(self):
        assert (10 == complexity('002A', '^v<>A'))

    def test_code_pad_mapping(self):
        assert ('<P>P' == ''.join(code_pad_to_robot('0A')))

    def test_arrow_pad_mapping(self):
        assert ('v<<P>>^PvP^P' == ''.join(arrow_pad_to_robot('<P>P')))

    def test_code_to_arrow_mapping(self):
        assert ('<<vP>>^PvP^P' == ''.join(arrow_pad_to_robot(code_pad_to_robot('0A'))))
        assert ('<<vPP>P>^PvPP<^P>P<vP>^P<P>P' == ''.join(arrow_pad_to_robot(arrow_pad_to_robot(code_pad_to_robot('0A')))))


# 869A: v<<P>>^PPPv<P<P>>^PvPP^<P>Pv<P^>Pv<<P>>^P<Pv>P^Pv<<P>>^PvP^Pv<P<P>>^PPP<Pv>P^P (78)
# 869A: v<<P>>^PPPv<P<P>>^PvPP^<P>Pv<P^>Pv<<P>>^P<Pv>P^Pv<<P>>^PvP^Pv<P<P>>^PPP<Pv>P^P (78)

# 180A: v<<P>>^Pv<P<P>>^PPvPP^<P>Pv<P^>P<Pv<P>>^PPvP^Pv<P<P>>^PPP<Pv>P^Pv<P^>P<P>P (74)
# 180A: v<<P>>^Pv<P<P>>^PPvPP^<P>Pv<P^>P<Pv<P>>^PPvP^Pv<P<P>>^PPP<Pv>P^Pv<P^>P<P>P (74)

# 596A: v<<P>>^PPv<P<P>>^PvPP^<P>Pv<P^>P<Pv<P>>^PvP^Pv<P<P>>^P<Pv>P^Pv<P<P>>^PP<Pv>P^P (78)
# 596A: v<<P>>^PPv<P<P>>^PvPP^<P>Pv<P^>P<Pv<P>>^PvP^Pv<P<P>>^P<Pv>P^Pv<P<P>>^PP<Pv>P^P (78)

# 965A: v<<P>>^PPPvP^Pv<P<P>>^P<Pv>P^Pv<P<PP>>^PvPP^<P>Pv<P^>Pv<<P>>^PP<Pv>P^P (70)
# 965A: v<<P>>^PPPvP^Pv<P<P>>^P<Pv>P^Pv<P<PP>>^PvPP^<P>Pv<P^>Pv<<P>>^PP<Pv>P^P (70)

# 973A: v<<P>>^PPPvP^Pv<P<PP>>^PPvPP^<P>Pv<P^>PPv<<P>>^PP<Pv>P^Pv<P<P>>^P<Pv>P^P (72)
# 973A: v<<P>>^PPPvP^Pv<P<PP>>^PPvPP^<P>Pv<P^>PPv<<P>>^PP<Pv>P^Pv<P<P>>^P<Pv>P^P (72)


## Arrow:
# def wont_panic(button, movements):
#     if button == up and movements[:1] == [left]:
#         return False
#     if button == press and movements[:2] == [left, left]:
#         return False
#     if button == left and movements[:1] == [up]:
#         return False
#     return True


## Code:
# def wont_panic(button, movements):
#     if button == 'A' and movements[:2] == [left, left]:
#         return False
#     if button == '0' and movements[:1] == [left]:
#         return False
#     if button == '1' and movements[:1] == [down]:
#         return False
#     return True

# if len(movements) > 0 and movements[-1]:
#     last_movement = movements[-1]
#     if last_movement in [left, right] and wont_panic(current_button, horizontal_movements):
#         movements += horizontal_movements
#         movements += vertical_movements
#         movements += [press]
#         current_button = button
#         continue
#     elif last_movement in [up, down] and wont_panic(current_button, vertical_movements):
#         movements += vertical_movements
#         movements += horizontal_movements
#         movements += [press]
#         current_button = button
#         continue
