(ns org.jakehoward.aoc.days.day-1
  (:require [org.jakehoward.aoc.utils :as utils]))

(def input (utils/get-input 1))

(defn calories-per-elf []
  (->> (clojure.string/split input #"\n\n")
       (map #(clojure.string/split % #"\n"))
       (map #(map (fn [i] (Integer/parseInt i)) %))
       (map #(reduce + %))))

(defn ans []
  (apply max (calories-per-elf)))

(defn ans-p2 []
  (->> (calories-per-elf)
       (sort)
       (reverse)
       (take 3)
       (reduce +)))

(comment
  (ans) ;; => 72070
  (ans-p2);; => 211805
  )
