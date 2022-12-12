(ns org.jakehoward.aoc.days.day-12
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as str]))

(defn parse-input [input]
  (let [letter-grid   (->> (utils/lines input)
                           (map #(str/split % #"")))]
    (vec letter-grid)))

(defn get-letter-yx [letter-grid letter]
  ;; todo: get
  (let [start-yx (->> letter-grid
                      (map-indexed (fn [y row] [y (.indexOf row letter)]))
                      (filter #(> (second %) -1))
                      first)]
    start-yx))

(defn get-start-yx [letter-grid]
  (get-letter-yx letter-grid "S"))

(defn get-end-yx [letter-grid]
  (get-letter-yx letter-grid "E"))


;; h(...) in the literature
;; estimate cost from point to end
(defn day12-heuristic [step-cost-est [end-y end-x] y x]
  (* step-cost-est
     (- (+ end-y end-x) y x 2)))

(defn part-1 [input]
  (let [letter-grid  (parse-input input)
        start-yx     (get-start-yx letter-grid)
        end-yx       (get-end-yx letter-grid)
        ans end-yx]
    ans))

(def example-input
  (->
   "
Sabqponm
abcryxxl
accszExk
acctuvwj
abdefghi"
   (str/trim)))

(comment
  (part-1 example-input)
  (part-1 (utils/get-input "12"))

  ;;
  )

