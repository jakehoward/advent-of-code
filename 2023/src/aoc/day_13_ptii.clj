(ns aoc.day-13ii
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


(defn has-partner-col? [matrix [clamp-min clamp-max] [y x]]
  (if (>= x clamp-max)
    (let [distance-to-clamp (- x clamp-max)
             other-x (- clamp-min distance-to-clamp)]
      (= (get-in matrix [y x]) (get-in matrix [y other-x])))
    (let [distance-to-clamp (- clamp-min x)
             other-x (+ clamp-max distance-to-clamp)]
      (= (get-in matrix [y x]) (get-in matrix [y other-x])))))

(defn has-partner-row? [matrix [clamp-min clamp-max] [y x]]
  (if (>= y clamp-max)
    (let [distance-to-clamp (- y clamp-max)
             other-y (- clamp-min distance-to-clamp)]
      (= (get-in matrix [y x]) (get-in matrix [other-y x])))
    (let [distance-to-clamp (- clamp-min y)
             other-y (+ clamp-max distance-to-clamp)]
      (= (get-in matrix [y x]) (get-in matrix [other-y x])))))

;; 0 1 2 [3 4] 5 6 => (7)
;; 0 1 2 [3 4] 5 6 7 => (8)
(defn is-reflection-col? [col-clamp matrix]
  (let [x-size (count (first matrix))
        [clamp-left clamp-right] col-clamp
        smaller-partition (if (< clamp-right (/ x-size 2)) :left :right)
        coords-to-match (->> (u/matrix-coords-yx matrix)
                             (filter (fn [[y x]] (if (= smaller-partition :left)
                                                   (<= x clamp-left)
                                                   (>= x clamp-right)))))]
    (= 1 (count (filter #(not (has-partner-col? matrix col-clamp %)) coords-to-match)))))

(defn is-reflection-row? [row-clamp matrix]
  (let [y-size (count matrix)
        [clamp-top clamp-bottom] row-clamp
        smaller-partition (if (< clamp-bottom (/ y-size 2)) :top :bottom)
        coords-to-match (->> (u/matrix-coords-yx matrix)
                             (filter (fn [[y x]] (if (= smaller-partition :top)
                                                   (<= y clamp-top)
                                                   (>= y clamp-bottom)))))]
    (= 1 (count (filter #(not (has-partner-row? matrix row-clamp %)) coords-to-match)))))

(comment
  (is-reflection-col? [4 5] (first (parse-input example)))
  (is-reflection-row? [0 1] (first (parse-input example)))

  ;; pt2
  (is-reflection-row? [2 3] (first (parse-input example)))
  (is-reflection-col? [4 5] (first (parse-input example)))

  (is-reflection-row? [0 1] (second (parse-input example)))
  (is-reflection-row? [3 4] (second (parse-input example))))

(defn find-reflection-old [matrix]
  (let [x-size (count (first matrix))
        y-size (count matrix)
        col-clamps (partition 2 1 (range 0 x-size))
        row-clamps (partition 2 1 (range 0 y-size))]
    {:cols (->> col-clamps
                (map (fn [clamp] {:col-clamp clamp
                                  :r? (is-reflection-col? clamp matrix)}))
                (filter (fn [{:keys [r?]}] r?)))
     :rows (->> row-clamps
                (map (fn [clamp] {:row-clamp clamp
                                  :r? (is-reflection-row? clamp matrix)}))
                (filter (fn [{:keys [r?]}] r?)))}))

(defn find-reflection [matrix]
  (let [x-size (count (first matrix))
        y-size (count matrix)
        col-clamps (partition 2 1 (range 0 x-size))
        row-clamps (partition 2 1 (range 0 y-size))
        col-reflection (->> col-clamps
                             (map (fn [clamp] {:clamp clamp
                                               :r? (is-reflection-col? clamp matrix)}))
                             (filter (fn [{:keys [r?]}] r?))
                             (map :clamp)
                             (first))
        row-reflection (->> row-clamps
                             (map (fn [clamp] {:clamp clamp
                                               :r? (is-reflection-row? clamp matrix)}))
                             (filter (fn [{:keys [r?]}] r?))
                             (map :clamp)
                             (first))]
    [(if (nil? col-reflection) 0 (second col-reflection))
     (if (nil? row-reflection) 0 (* 100 (second row-reflection)))] ))

(defn pt2 [input]
  (let [parsed (parse-input input)
        reflection-summaries    (mapcat find-reflection parsed)
        ans (u/sum reflection-summaries)]
    ans))

;; add up the number of columns
;; to the left of each vertical line
;; of reflection; to that, also add
;; 100 multiplied by the number of rows
;; above each horizontal line of reflection.

(comment
  (pt2 example)
  (pt2 input) ;; 33183
;
)
