
## Installation
```
brew install py3cairo ffmpeg
brew install pango pkg-config scipy
brew install --cask mactex-no-gui # Optional, needed for TeX
```

```
pip3 install manim
```

## Running

```
manim -pql getting_started.py CreateCircle
PYTHONPATH=$PYTHONPATH:$(pwd) manim -pql animations/explain_day_4.py DayFour
```