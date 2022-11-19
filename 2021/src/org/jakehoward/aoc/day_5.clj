(ns org.jakehoward.aoc.day-5
  (:require [org.jakehoward.aoc.utils :as utils]
            [org.jakehoward.aoc.day-4 :refer [Printable]]
            [clojure.string :as string]))

(def data (->> (utils/get-input 5)
               utils/lines))

(defn range-incl [start end]
  (cond
    (= start end) (range start end)
    (< start end) (range start (inc end))
    (> start end) (reverse (range end (inc start)))))

(defprotocol DiscreteLine
  (points [this]))

(defrecord Point [x y]
  Printable
  (asString [this] (str (format "(%s,%s)" x y))))

(defrecord Line [start end]
  Printable
  (asString [this] (str (.asString start) " -> " (.asString end)))
  DiscreteLine
  (points [this] (cond
                   (= (.x start) (.x end))
                   (mapv #(->Point (.x start) %)
                         (range-incl (.y start) (.y end)))

                   (= (.y start) (.y end))
                   (mapv #(->Point % (.y start))
                         (range-incl (.x start) (.x end)))

                   :else
                   []
                   )))

(def lines (->> data
                (mapv (fn [input] (string/split input #" -> ")))
                (mapv (fn [[start end]] [(string/split start #",")
                                         (string/split end #",")]))
                (mapv (fn [[start end]] [(mapv #(Integer/parseInt %) start)
                                         (mapv #(Integer/parseInt %) end)]))
                (mapv (fn [[[x1 y1] [x2 y2]]] (->Line (->Point x1 y1)
                                                      (->Point x2 y2))))))

;; ======
;; ======
(defn soln []
  (let [all-points (mapcat #(.points %) lines)
        grouped    (group-by identity all-points)
        counts     (map (fn [[p ps]] [p (count ps)]) grouped)
        over-2     (filter (fn [[p number]] (>= number 2)) counts)]
    (count over-2)))

(comment
  (soln) ;; => 6225

  (.asString (last lines))

  (->> (->Line (->Point 1 4)
               (->Point 10 13))
       .points
       (map #(.asString %)))
  )
