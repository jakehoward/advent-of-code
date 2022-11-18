(ns org.jakehoward.aoc.core
  (:require [clojure.java.io :as io]))

(defn get-input [day]
  (-> (format "inputs/days/%s.txt" day)
      io/resource
      slurp
      clojure.string/trim))

(defn lines [txt]
  (clojure.string/split txt #"\n"))

(comment
  ;; Day 1: https://adventofcode.com/2021/day/1
  (def day-1-data
    (->> (get-input 1)
         lines
         (map #(Integer/parseInt %))))

  (defn count-increasing [nums]
    (->> nums
       (partition 2 1)
       (map (fn [[a b]] (< a b)))
       (filter identity)
       count))

  ;; part one => 1759
  (-> day-1-data
      count-increasing)

  ;; part 2 => 1805
  (->> day-1-data
       (partition 3 1)
       (map (partial apply +))
       count-increasing))
