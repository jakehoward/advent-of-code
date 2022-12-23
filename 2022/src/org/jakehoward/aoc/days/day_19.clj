(ns org.jakehoward.aoc.days.day-19
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
  (part-1 (utils/get-input "19"))
  (def example-input (->
                      "
Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.

Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian."
                      (str/trim)))
  ;;
  )
