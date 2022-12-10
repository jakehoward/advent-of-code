(ns org.jakehoward.aoc.days.day-9-ui
  (:require
   [org.jakehoward.aoc.days.day-9 :refer [get-all-positions example-data]]
   [io.github.humbleui.canvas :as canvas]
   [io.github.humbleui.core :as core]
   [io.github.humbleui.paint :as paint]
   [io.github.humbleui.ui :as ui]
   [io.github.humbleui.window :as window]
   [nrepl.cmdline :as nrepl])
  (:import
   [io.github.humbleui.skija Canvas Color PaintMode Path]))

(defonce *window
  (atom nil))

(defn fill-cell [canvas x y paint opacity]
  (.setAlpha paint opacity)
  (canvas/draw-rect canvas (core/rect-xywh x y 1 1) paint))

(def paint-knot
  (paint/stroke 0xFF000000 0.1))

(defn render-knot [canvas x y]
  (fill-cell canvas x y (paint/fill 0) 255))

(defn paint [ctx canvas size]
  (let [size  {:width 1000 :height 1000}
        field (min (:width size) (:height size))
        dim   25
        scale (/ field dim)]

    ;; center canvas
    (canvas/translate canvas
                      (-> (:width size) (- field) (/ 2))
                      (-> (:height size) (- field) (/ 2)))

    ;; scale to fit full width/height but keep square aspect ratio
    (canvas/scale canvas scale scale)

    ; erase background
    (with-open [bg (paint/fill 0xFFFFFFFF)]
      (canvas/draw-rect canvas (core/rect-xywh 0 0 dim dim) bg))

    ;; (doseq [x (range dim)
            ;; y (range dim)]
    ;; (render-knot canvas x y))
    (let [x 1
          y 1]
      (render-knot canvas x (- (dec dim) y)))

    ;; schedule redraw on next vsync
    (window/request-frame (:window ctx))))

(def app
  (ui/default-theme
   (ui/center
    (ui/canvas
     {:on-paint paint}))))

(defn -main [& args]
  (ui/start-app!
   (reset! *window
           (ui/window
            {:title "Ropey dynamcics"}
            #'app)))
  (apply nrepl/-main args))
