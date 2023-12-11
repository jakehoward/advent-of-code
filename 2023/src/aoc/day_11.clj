(ns aoc.day-11
  (:require [aoc.utils :as u]
            [clojure.string :as str]))

(defn unique-pairs [xs]
  (->> (for [a xs b xs :when (not= a b)]
              (set [a b]))
            set
            (map vec)))
(comment (unique-pairs [:a :b :c]))

(def example (str/trim "
...#......
.......#..
#.........
..........
......#...
.#........
.........#
..........
.......#..
#...#....."))

(def input (u/get-input 11))

(defn will-expand? [space-slice]
  (every? #(= "." %) space-slice))

(defn parse-input [input]
  (u/input->matrix input))

(defn shortest-path [[[ay ax] [by bx]]]
  (+ (apply - (sort > [ay by])) (apply - (sort > [ax bx]))))

(comment (shortest-path [[9 4] [5 1]])
         (shortest-path [[0 4] [11 5]])
         ;; todo (sort (comp - compare) ...)
         )

(defn pt1 [input]
  (let [space      (parse-input input)
        rows       space
        cols       (u/cols space)
        row-ex     (mapv will-expand? rows)
        col-ex     (mapv will-expand? cols)
        galaxy-yxs (for [y (range (count space))
                         x (range (count (first space)))
                         :when (= "#" (get-in space [y x]))]
                     (let [num-expanded-before-x (->> col-ex (take x) (filter identity) count)
                           num-expanded-before-y (->> row-ex (take y) (filter identity) count)]
                       [(+ num-expanded-before-y y) (+ num-expanded-before-x x)]))
        galaxy-pairs   (unique-pairs galaxy-yxs)
        shortest-paths (map shortest-path galaxy-pairs)
        ans     {:row-expansions row-ex
                 :col-expansions col-ex
                 :galaxy-yxs galaxy-yxs
                 ;; :pairs (unique-pairs galaxy-yxs)
                 :shortest-paths shortest-paths}]
    (u/sum (:shortest-paths ans))))

(defn pt2 [input]
  (let [space      (parse-input input)
        rows       space
        cols       (u/cols space)
        row-ex     (mapv will-expand? rows)
        col-ex     (mapv will-expand? cols)
        galaxy-yxs (for [y (range (count space))
                         x (range (count (first space)))
                         :when (= "#" (get-in space [y x]))]
                     (let [num-expanded-before-x (->> col-ex (take x) (filter identity) count)
                           num-expanded-before-y (->> row-ex (take y) (filter identity) count)]
                       [(+ (* 999999N num-expanded-before-y) y) (+ (* 999999N num-expanded-before-x) x)]))
        galaxy-pairs   (unique-pairs galaxy-yxs)
        shortest-paths (map shortest-path galaxy-pairs)
        ans     {:row-expansions row-ex
                 :col-expansions col-ex
                 :galaxy-yxs galaxy-yxs
                 ;; :pairs (unique-pairs galaxy-yxs)
                 :shortest-paths shortest-paths}]
    (u/sum (:shortest-paths ans))))

(comment
  (pt1 example)
  (pt1 input)
  (pt2 example)
  (pt2 input) 512240933238N  ;; 512241445470 (too high) ;; 271457245470N ???
;
)
