from manim import *

class CreateCircle(Scene):
    def construct(self):
        circle = Circle()  # create a circle
        circle.set_fill(PINK, opacity=0.5)  # set the color and transparency
        self.play(Create(circle))  # show the circle on screen

class TableExample(Scene):
    def construct(self):
        vals = [[1, 0, 5, 10],
             [0, 0, 5, 10],
             [2, 2, 7, 12],
             [4, 4, 9, 14]]
        t0 = MathTable(
            vals,
            include_outer_lines=True)
        # self.add(t0)
        # self.play(Create(t0))
        t1 = MathTable([[v for vs in vals for v in vs]], include_outer_lines=True)
        t1.scale(0.5)
        self.wait()
        # self.camera.rescale_to_fit(t1.s...) ???
        # self.add(t1)
        self.play(Create(t1))

        circle = Circle()  # create a circle
        circle.set_fill(RED, opacity=0.5)  # set the color and transparency
        self.play(Create(circle))  # show the circle on screen
        self.wait()
