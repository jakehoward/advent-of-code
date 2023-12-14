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
                                        (partition x-size)
                                        (map vec)
                                        vec))

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

;;     |---- cycle ---|
;; s---x----------- see-x

(defn num-cycles-until-repeat [matrix]
  (loop [current    matrix
         idx-seen   []
         num-cycles 0]
    (let [matches (filter #(= current (second %)) idx-seen)
          match-idx   (ffirst matches)]

      (cond (= 1 (count matches))
            {:cycle-starts match-idx :cycle-length (dec (- num-cycles match-idx))}

            (> num-cycles 1000)
            (throw (Exception. "Max iterations exceeded for num-cycles-unitl-repeat"))

            :else
            (recur (cycle-boulders current)
                   (conj idx-seen [num-cycles current])
                   (inc num-cycles))))))

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
        {:keys [cycle-starts cycle-length]} (num-cycles-until-repeat parsed)
        _        (println "Cycles unitl repeat, starts:" cycle-starts "length:" cycle-length )
        extra-cycles        (rem (- 1000000000 cycle-starts) cycle-length)
        final-matrix        (->> (iterate cycle-boulders parsed)
                                 (take (+ 1 cycle-starts extra-cycles))
                                 last)
        y-size (count parsed)
        scores (->> final-matrix
                    u/cols
                    (map score-col))]
    ;; {:final-matrix final-matrix :scores scores :score (u/sum (flatten scores))}
    (u/sum scores)))

(defn debug [input cycles]
  (let [parsed              (parse-input input)
        final-matrices      (->> (iterate cycle-boulders parsed)
                                 (take 24))
        scores              (->> final-matrices
                                 (map (fn [m] (->> m
                                                   u/cols
                                                   (map score-col)
                                                   u/sum))))]
    scores))

;;       x
;; S 1 2 3 4 5 6 7 8 9 10 11

(comment
  (time (pt2 example))
  (time (debug example 0))

  (time (pt2 input)) ;; 96333 (too high) ;; 96345 (too high)

  (take 3 (iterate inc 1))

  (rem 1000000000 102)

  (time (pt1 example)) ;; 136
  (time (pt1 input)) ;; 107430
  ;; (/ (* 7 1e9) (* 1000 60 60 24))
  
  (pt2 input)
;
)
