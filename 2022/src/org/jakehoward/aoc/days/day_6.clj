(ns org.jakehoward.aoc.days.day-6
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as string]))

(def input (utils/get-input 6))

(comment
  ;; pt1 => 1723
  (->>  (partition 4 1 input)
        (take-while #(< (count (set %)) 4))
        count
        (+ 4))

  ;; pt2 => 3708
  (->>  (partition 14 1 input)
        (take-while #(< (count (set %)) 14))
        count
        (+ 14))
  )
