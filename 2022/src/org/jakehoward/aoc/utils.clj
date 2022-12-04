(ns org.jakehoward.aoc.utils
  (:require [clojure.java.io :as io]))

(defn get-input [day]
  (-> (format "inputs/days/%s.txt" day)
      io/resource
      slurp
      clojure.string/trim))

(defn lines [txt]
  (clojure.string/split txt #"\n"))

(defn chars [lines]
  (map #(clojure.string/split % #"") lines))

(defn range-incl [start end]
  (cond
    (= start end) (range start end)
    (< start end) (range start (inc end))
    (> start end) (reverse (range end (inc start)))))
