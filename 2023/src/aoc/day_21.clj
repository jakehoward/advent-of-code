(ns aoc.day-21
  (:require [aoc.utils :as u]
            [clojure.string :as str]
            [clojure.set :as set]))

(def example (str/trim "
...........
.....###.#.
.###.##..#.
..#.#...#..
....#.#....
.##..S####.
.##..#...#.
.......##..
.##.#.####.
.##..##.##.
..........."))
(def input (u/get-input 21))

(defn parse-input [input]
  (u/input->matrix input))

(defn get-start [matrix]
  (->> (for [y (range (u/y-size matrix))
             x (range (u/x-size matrix))
             :when (= "S" (get-in matrix [y x]))]
         [y x])
       first))

(defn solve-pt1 [matrix start-yx total-num-steps]
  (let [get-valid-nbrs (memoize (fn [yx]
                                  (->> (u/get-neighbours-coords-yx matrix yx {:diagonals false})
                                       (filterv (fn [yx] (and (not= "#" (get-in matrix yx))))))))]
    (loop [positions  [start-yx]
           num-steps  total-num-steps]
      (if (= 0 num-steps)
        (count (set positions))
        (let [nbr-yxs (->> positions
                           (mapv get-valid-nbrs)
                           (apply concat)
                           vec)]
          (recur (vec (set nbr-yxs))
                 (dec num-steps)))))))

(defn pt1 [input num-steps]
  (let [matrix (parse-input input)
        s-yx   (get-start matrix)
        ans    (solve-pt1 matrix s-yx num-steps)]
    ans))

(defn pt2 [input num-steps]
  (let [matrix (parse-input input)
        s-yx   (get-start matrix)
        ans    (solve-pt1 matrix s-yx num-steps)]
    ans))

(comment
  (time (pt2 example 6))
  (time (pt2 example 10)) ;; 50

  ;; (pt2 input 26501365)

  (time (pt1 example 6))
  (time (pt1 input 64)) ;; 3615

  (let [m (-> (str/trim "
...........
.....###.#.
.###.##.O#.
.O#O#O.O#..
O.O.#.#.O..
.##O.O####.
.##.O#O..#.
.O.O.O.##..
.##.#.####.
.##O.##.##.
...........")
              (u/input->matrix))]
    (sort (for [y (range (u/y-size m))
               x (range (u/x-size m))
               :when (= "O" (get-in m [y x]))]
            [y x])))
;
)
