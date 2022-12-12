(ns org.jakehoward.aoc.days.day-n
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
  (part-1 (utils/get-input "< put day here>"))
  (def example-input (->
                      ""
                      (str/trim)))
  ;;
  )
