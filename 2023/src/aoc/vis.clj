(ns aoc.vis
  (:require [hiccup2.core :as h]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(defn viz-js [svgs]
  (-> "viz.js"
      io/resource
      slurp
      (str/replace "{{svgs}}" (str/join "," (map #(str "`" % "`") svgs)))))

(def largest-dimension-px 1000)
(def cell-size 10)
(def half-cell-size (long (/ cell-size 2)))

(defn get-x-size [grid]
  (count (first grid)))

(defn get-y-size [grid]
  (count grid))

(defn calc-width-height [grid]
  (let [[x-size y-size] ((juxt get-x-size get-y-size) grid)
        [width height]  (if (> x-size y-size)
                          [largest-dimension-px
                           (* largest-dimension-px (/ y-size x-size))]
                          [(* largest-dimension-px (/ x-size y-size))
                           largest-dimension-px])]
    {:width (long (Math/ceil width)) :height (long (Math/ceil height))}))

(defn cell->svg-elem [{:keys [color shape]} x y]
  (cond (= :rect shape)
        [:rect {:x x :y y :width cell-size :height cell-size :fill color}]

        (= :circle shape)
        [:circle {:cx (+ half-cell-size x) :cy (+ half-cell-size y) :r half-cell-size :fill color}]

        :else (throw (Exception. (str "Unsupported shape: " shape)))))

(defn grid->svg [grid cell-fn]
  (let [{:keys [width height]} (calc-width-height grid)
        [viewport-x viewport-y] [(* cell-size (get-x-size grid)) (* cell-size (get-y-size grid))]
        root [:svg
              {:width width :height height :viewbox (format "0 0 %s %s" viewport-x viewport-y)}]
        cells (for [y (range (get-y-size grid))
                    x (range (get-x-size grid))]
                (cell->svg-elem (cell-fn grid [y x]) (* cell-size x) (* cell-size y)))]
    (into root cells)))

(defn viz-page [svg]
  [:html
   [:body
    [:div {:style {:display :flex :flex-direction :column :gap 10}}
     [:div {:style {:display :flex :flex-direction :row :gap 10}}
      [:h1 "Grid view"]]
     svg]]])

(defn viz-multi-page [svgs]
  [:html
   [:body
    [:script {} (h/raw (viz-js svgs))]
    [:div {:style {:display :flex :flex-direction :column :gap "10px"}}
     [:div {:style {:display :flex :flex-direction :row :align-items :center :gap "10px"}}
      [:h1 "Grid view"]
      [:button {:onclick "play()" :style {:height  "20px"}} "Play"]
      [:button {:onclick "pause()" :style {:height  "20px"}} "Pause"]
      [:button {:onclick "resetToStart()" :style {:height  "20px"}} "<--"]
      [:button {:onclick "onLeft100()" :style {:height  "20px"}} "<<<"]
      [:button {:onclick "onLeft10()" :style {:height  "20px"}} "<<"]
      [:button {:onclick "onLeft()" :style {:height  "20px"}} "<"]
      [:button {:onclick "onRight()" :style {:height "20px"}} ">"]
      [:button {:onclick "onRight10()" :style {:height "20px"}} ">>"]
      [:button {:onclick "onRight100()" :style {:height "20px"}} ">>>"]
      [:span {:id "buttonLabel"}]]
     [:div {:id "svgContainer"}]]
    [:script {} "init()"]]])

(defn grid->html [grid cell-fn]
  (-> grid
      (grid->svg cell-fn)
      viz-page
      h/html))

(defn grids->html [grids cell-fn]
  (->> grids
       (mapv #(h/html (grid->svg % cell-fn)))
       viz-multi-page
       h/html))

(comment

  (let [grids (mapcat identity (repeat 100
                                       [[(vec (flatten (repeat 10 [1 0 1])))
                                         (vec (flatten (repeat 10 [0 1 0])))
                                         (vec (flatten (repeat 10 [1 0 1])))
                                         (vec (flatten (repeat 10 [1 0 1])))
                                         (vec (flatten (repeat 10 [0 1 0])))
                                         (vec (flatten (repeat 10 [1 0 1])))
                                         (vec (flatten (repeat 10 [1 0 1])))
                                         (vec (flatten (repeat 10 [0 1 0])))
                                         (vec (flatten (repeat 10 [1 0 1])))]
                                        [(vec (flatten (repeat 10 [0 1 0])))
                                         (vec (flatten (repeat 10 [1 0 1])))
                                         (vec (flatten (repeat 10 [0 1 0])))
                                         (vec (flatten (repeat 10 [0 1 0])))
                                         (vec (flatten (repeat 10 [1 0 1])))
                                         (vec (flatten (repeat 10 [0 1 0])))
                                         (vec (flatten (repeat 10 [0 1 0])))
                                         (vec (flatten (repeat 10 [1 0 1])))
                                         (vec (flatten (repeat 10 [0 1 0])))]]))
        cell-fn (fn [g yx] (if (= 1 (get-in g yx))
                             {:shape :rect :color "red"}
                             {:shape :circle :color "green"}))]
    (->> (grids->html grids cell-fn)
         (spit "viz.html")))

  (let [grid [[nil nil nil] [nil nil nil]]
        grid [[nil nil] [nil nil] [nil nil]]
        grid [[nil nil] [nil nil]]]
    (get-x-size grid)
    (get-y-size grid)
    ((juxt get-x-size get-y-size) grid)
    (calc-width-height grid)))
