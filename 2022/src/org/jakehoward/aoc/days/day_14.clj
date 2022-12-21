(ns org.jakehoward.aoc.days.day-14
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as str]))

(defn parse-input [input]
  input)

(defn part-1 [input]
  (let [xxx (parse-input input)
        ans xxx]
    ans))

(comment
  (part-1 example-input)
  (part-1 (utils/get-input "14"))
  (def example-input (->
                      "
498,4 -> 498,6 -> 496,6
503,4 -> 502,4 -> 502,9 -> 494,9"
                      (str/trim)))
  ;;
  )
