function getSvgContainer() { return document.getElementById("svgContainer"); }
function getButtonLabel() { return document.getElementById("buttonLabel"); }

let currentSvg = 0;
const svgs = [
  {{svgs}}
];

function getCurrent() {
  return currentSvg;
}

function numSvgs() {
  return svgs.length;
}

function setButtonLabel() {
  getButtonLabel().innerHTML = `${getCurrent() + 1} of ${numSvgs()}`;
}

function updateSvgContainer() {
  getSvgContainer().innerHTML = svgs[currentSvg];
  setButtonLabel();
}

function onRight() {
  currentSvg = Math.min(svgs.length - 1, currentSvg + 1);
  updateSvgContainer();
}

function onRight10() {
  currentSvg = Math.min(svgs.length - 1, currentSvg + 10);
  updateSvgContainer();
}

function onRight100() {
  currentSvg = Math.min(svgs.length - 1, currentSvg + 100);
  updateSvgContainer();
}

function onLeft() {
  currentSvg = Math.max(0, currentSvg - 1);
  updateSvgContainer();
}

function onLeft10() {
  currentSvg = Math.max(0, currentSvg - 10);
  updateSvgContainer();
}

function onLeft100() {
  currentSvg = Math.max(0, currentSvg - 100);
  updateSvgContainer();
}

function init() {
  updateSvgContainer();
}

function resetToStart() {
  currentSvg = 0;
  updateSvgContainer();
}

let interval = null;
function play() {
  // want roughly 10 second end to end
  // with a min gap to stop laptop setting fire
  const gapMs = Math.max(100, Math.ceil(10000 / numSvgs()));
  clearInterval(interval);
  if (currentSvg === numSvgs() - 1) {
    currentSvg = 0;
    updateSvgContainer();
  }

  interval = setInterval(() => {
    if (currentSvg < numSvgs() - 1) {
      onRight();
    } else {
      clearInterval(interval);
    }
  }, gapMs);
}

function pause() {
  clearInterval(interval);
}
