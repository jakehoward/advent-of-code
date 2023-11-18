(ns org.jakehoward.aoc.day-9
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as string]))

(def raw-rows (-> (utils/get-input 9)
                  (string/split #"\n")))

(def data (->> raw-rows
               (map #(string/split % #""))
               (map #(map (fn [n] (Integer/parseInt n)) %))))

(defprotocol GridItem
  (neighbour-coords [this]))

(defrecord Cell [x y value]
  GridItem
  (neighbour-coords [this]
    (for [nx [(inc x) x (dec x)]
          ny [(inc y) y (dec y)]
          :when (not (and (= nx x) (= ny y)))]
      [nx ny])))

;; x x x
;; x O x
;; x x x
(defn neighbours [x y]
  [[(dec x) y]
   [(inc x) y]
   [x (dec y)]
   [x (inc y)]])

(comment
  (for [col (range (count (first data)))
        row (range (count data))]
    (let [value            (nth (nth data row) col)
          _                (println value)
          neighbour-coords (neighbours col row)
          neighbour-vals   (map (fn [[col row]] (nth (nth data row) col)) neighbour-coords)]
      (when (= value
               (apply min (conj value neighbour-vals)))
        value)))

  (.neighbour-coords (->Cell 1 1 :any))
  (count (.neighbour-coords (->Cell 1 1 :any))))

