(ns aoc.day-14
  (:require [aoc.utils :as u]
            [clojure.string :as str]))

(defn get-idxs-of [coll item]
  (->> coll
       (map-indexed vector)
       (filter (fn [[idx item]] (= "#" item)))
       (map first)))

(def example (str/trim "
O....#....
O.OO#....#
.....##...
OO.#O....O
.O.....O#.
O.#..O.#.#
..O..#O..O
.......O..
#....###..
#OO..#...."))
(def input (u/get-input 14))

(defn parse-input [input]
  (u/input->matrix input))

(defn roll-boulders [square-idxs boulder-idxs]
  (loop [barrier -1])
  [0 1 2 3])

(defn roll-boulders [xs]
  (loop [items xs
         idx   0
         barrier -1
         boulder-idxs []]
    (if (empty? items)
      boulder-idxs
      (let [item (first items)]
        (recur (rest items)
               (inc idx)
               (if (= "#" item) idx barrier)
               (if (= "O" item)
                 (conj boulder-idxs (inc (max barrier (or (last boulder-idxs) -1))))
                 boulder-idxs))))))

(defn cycle-boulders [matrix]
  (let [y-size (count matrix)
        x-size (count (first matrix))
        col-boulder-idxs->yxs    (fn [idxs]
                                   (->> idxs
                                       (mapcat (fn [x ys] (map (fn [y] [y x]) ys)) (range))
                                       set))
        row-boulder-idxs->yxs    (fn [idxs]
                                   (->> idxs
                                       (mapcat (fn [y xs] (map (fn [x] [y x]) xs)) (range))
                                       set))
        update-matrix            (fn [boulder-yxs]
                                   (->> (for [y (range y-size)
                                              x (range x-size)]
                                          (cond (contains? boulder-yxs [y x])
                                                "O"
                                                (= "#" (get-in matrix [y x]))
                                                "#"
                                                :else "."))
                                        (partition x-size)))

        matrix-after-north       (->> (u/cols matrix)
                                      (map roll-boulders)
                                      col-boulder-idxs->yxs
                                      update-matrix)

        matrix-after-west (->> matrix-after-north
                                      (map roll-boulders)
                                      row-boulder-idxs->yxs
                                      update-matrix)

        matrix-after-south  (->> matrix-after-west
                                 u/cols
                                 (map reverse)
                                 (map roll-boulders)
                                 (map (fn [reversed-col] (map #(- (dec y-size) %) reversed-col)))
                                 col-boulder-idxs->yxs
                                 update-matrix)

        matrix-after-east   (->> matrix-after-south
                                 (map reverse)
                                 (map roll-boulders)
                                 (map (fn [reversed-row] (map #(- (dec x-size) %) reversed-row)))
                                 row-boulder-idxs->yxs
                                 update-matrix)]
    matrix-after-east))

(defn pt1 [input]
  (let [parsed (parse-input input)
        cols   (u/cols parsed)
        all-rolled-boulder-idxs (map roll-boulders cols)
        col-count (count (first cols))
        ans (mapcat (fn [rbi] (map #(- col-count %) rbi)) all-rolled-boulder-idxs)]
    (u/sum ans)))

(defn num-cycles-until-repeat [matrix]
  (loop [current matrix
         seen    #{}
         steps   0]
    (cond (contains? seen current)
          steps

          (> steps 100)
          (throw (Exception. "Max iterations exceeded for num-cycles-unitl-repeat"))

          :else
          (recur (cycle-boulders current)
                 (conj seen current)
                 (inc steps)))))

(defn score-col [col]
  (let [boulder-idxs (->> col
                          (map-indexed vector)
                          (filter (fn [[idx item]] (= item "O")))
                          (map first))]
    (->> boulder-idxs
         (map #(- (count col) %))
         u/sum)))

(defn pt2 [input]
  (let [parsed              (parse-input input)
        cycles-until-repeat (num-cycles-until-repeat parsed)
        extra-cycles        (rem 1000000000 cycles-until-repeat)
        final-matrix        (->> (iterate cycle-boulders parsed)
                                 (take (+ 1 cycles-until-repeat extra-cycles))
                                 last)
        y-size (count parsed)
        ans  (->> final-matrix
                  u/cols
                  (map score-col)
                  u/sum)]
    ans))

(comment
  (time (pt2 example))
  (time (pt2 input))

  (take 3 (iterate inc 1))

  (rem 1000000000 3)

  (time (pt1 example))
  (time (pt1 input)) ;; 107430
  ;; (/ (* 7 1e9) (* 1000 60 60 24))
  
  (pt2 input)
;
)
