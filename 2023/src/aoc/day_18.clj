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
       (mapv (fn [l] (let [[d n color] (str/split l #"\s+")
                           direction   (case d "R" [0 1] "L" [0 -1] "D" [1 0] "U" [-1 0])
                           dir-name    (case d "R" :right "L" :left "D" :down "U" :up)
                           n           (u/parse-int n)]
                       {:direction   direction
                        :dir-name    dir-name
                        :length      n
                        :translation (mapv #(* n %) direction)
                        :color       (str/replace (str/replace color "(" "") ")" "")})))))

(def curr-dir->turn->next-dir {:right {:left :up    :right :down}
                               :left  {:left :down  :right :up}
                               :up    {:left :left  :right :right}
                               :down  {:left :right :right :left}})

;;    R6
;; #######
;; #.....#
;; ###...# D5
;; ..#...#
;; ..#...#
;; ###.### L2
;; #...#..
;; ##..###
;; .#....#
;; .######
(def turn->corner-type {[:right :down]  :outside
                        [:right :up]    :inside
                        [:left  :down]  :inside
                        [:left  :up]    :outside
                        [:up    :right] :outside
                        [:up    :left]  :inside
                        [:down  :right] :inside
                        [:down  :left]  :outside})

(defn trace-polygon-left [instructions]
  (loop [points    [[0 0] (mapv + [0 0] (-> instructions first :direction))]
         add-1     false
         rem-instr instructions]
    (if (= 1 (count rem-instr))
      (conj points (first points))
      (let [last-point (last points)
            this-instr (first rem-instr)
            next-instr (first (rest rem-instr))
            turn-type  (turn->corner-type [(:dir-name this-instr) (:dir-name next-instr)])

            new-point  (->> (mapv + last-point (:translation this-instr))
                            (mapv + (if add-1 (:direction this-instr) [0 0]))
                            (mapv + (if (= turn-type :inside)
                                      (mapv #(* -1 %) (:direction this-instr))
                                      [0 0])))]
        (recur (conj points new-point)
               (= :outside turn-type)
               (rest rem-instr))))))

;; https://en.wikipedia.org/wiki/Shoelace_formula
(defn guass-area [all-points]
  (let [points     (if (= (first all-points) (last all-points)) (butlast all-points) all-points)
        point-pairs (->> (partition 2 1 (cycle points))
                         (take (count points)))
        dets        (mapv (fn [[[c a] [d b] :as x]] (- (* a d) (* b c))) point-pairs)]
    (-> dets
        u/sum
        (* 1/2)
        clojure.core/abs)))

(comment
  (guass-area [[6 1] [1 3] [2 7] [4 4] [5 8]])
  (guass-area [[1 6] [3 1] [7 2] [4 4] [8 5]]))

(defn pt1 [input]
  (let [instructions      (parse-input input)
        invalid-turns     #{[:right :right] [:left :left] [:up :up] [:down :down]
                            [:right :left] [:left :right] [:up :down] [:down :up]}
        _                 (as-> (partition 2 1 (map :dir-name instructions)) $
                            (every? #(not (contains? invalid-turns %)) $)
                            (assert $ "Instructions contain invalid turns"))
        left-points       (trace-polygon-left instructions)
        left-ans          (guass-area left-points)]
    left-ans))

(defn pt2 [input]
  (let [parsed (parse-input input)
        ans    parsed]
    ans))

(comment
  (pt1 example) ;; [62N 49/2]
  (pt1 input) ;; [39189N 39189N]  ;;[39189N 35514N] ;; 39189N (too low)
  (pt2 example)
  (pt2 input)
;
)
