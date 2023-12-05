(ns aoc.day-{{ns-n}}
  (:require [aoc.utils :as u]
            [clojure.string :as str]))

(def example (str/trim ""))
(def input (u/get-input {{n}}))

(defn parse-input [input])

(defn pt1 [input]
  (let [parsed (parse-input input)
        ans    parsed]
    ans))

(defn pt2 [input]
  (let [parsed (parse-input input)
        ans    parsed]
    ans))

(comment
  (pt1 example)
  (pt1 input)
  (pt2 example)
  (pt2 input)
;
)
