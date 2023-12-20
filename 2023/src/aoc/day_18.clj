(ns aoc.day-18
  (:require [aoc.utils :as u]
            [clojure.string :as str]))

(def example (str/trim "
R 6 (#70c710)
D 5 (#0dc571)
L 2 (#5713f0)
D 2 (#d2c081)
R 2 (#59c680)
D 2 (#411b91)
L 5 (#8ceee2)
U 2 (#caa173)
L 1 (#1b58a2)
U 2 (#caa171)
R 2 (#7807d2)
U 3 (#a77fa3)
L 2 (#015232)
U 2 (#7a21e3)"))

(def input (u/get-input 18))

(defn parse-input [input]
  (->> (str/split-lines input)
       (map (fn [l] (let [[d n color] (str/split l #"\s+")]
                      {:direction (case d "R" :right "L" :left "D" :down "U" :up)
                       :length    (u/parse-int n)
                       :color     (str/replace (str/replace color "(" "") ")" "")})))))

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
