(ns aoc.day-13
  (:require [aoc.utils :as u]
            [clojure.string :as str]))

(def example (str/trim "
#.##..##.
..#.##.#.
##......#
##......#
..#.##.#.
..##..##.
#.#.##.#.

#...##..#
#....#..#
..##..###
#####.##.
#####.##.
..##..###
#....#..#"))

(def input (u/get-input 13))

(defn parse-input [input]
  (->> (str/split input #"\n\n")
       (map u/input->matrix)))


(defn has-partner? [matrix [clamp-left clamp-right] [y x]]
  (if (>= x clamp-right)
    (let [distance-to-clamp (- x clamp-right)
             other-x (- clamp-left distance-to-clamp)]
      (= (get-in matrix [y x]) (get-in matrix [y other-x])))
    (let [distance-to-clamp (- clamp-left x)
             other-x (+ clamp-right distance-to-clamp)]
      (= (get-in matrix [y x]) (get-in matrix [y other-x])))))

;; 0 1 2 [3 4] 5 6 => (7)
;; 0 1 2 [3 4] 5 6 7 => (8)
(defn is-reflection? [col-clamp matrix]
  ;; assumes always in col orientation
  ;; Transpose if you want rows, up to caller to get that
  (let [x-size (count (first matrix))
        [clamp-left clamp-right] col-clamp
        smaller-partition (if (< clamp-right (/ x-size 2)) :left :right)
        coords-to-match (filter (fn [[y x]] (>= x clamp-right)) (u/matrix-coords-yx matrix))]
    (every? #(has-partner? matrix col-clamp %) coords-to-match)))

(comment
  (is-reflection? [4 5] (first (parse-input example)))
  )

(defn find-reflection [matrix]
  (let [x-size (count (first matrix))
        y-size (count matrix)
        col-clamps (partition 2 1 (range 0 x-size))
        row-coords (partition 2 1 (range 0 y-size))]
    {:col-reflections (map (fn [clamp] {:col-clamp clamp
                                        :r? (is-reflection? clamp matrix)})
                           col-clamps)}
    (map (fn [clamp] {:col-clamp clamp
                      :r? (is-reflection? clamp matrix)})
         col-clamps)))

(comment
  (let [parsed (parse-input example)
        first-m (first parsed)
        reflections (find-reflection first-m)]
    (->> reflections
         (filter (fn [{:keys [r?]}] r?))))) ;; bug - destructure doesn't cause lazy eval??

(defn pt1 [input]
  (let [parsed (parse-input input)
        ans    (map find-reflection parsed)]
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
