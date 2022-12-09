(ns org.jakehoward.aoc.days.day-8
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as string]))

(defn input->matrix [raw-input]
  (->> raw-input
       (utils/lines)
       (utils/chars)
       (map (fn [line] (map #(Integer/parseInt %) line)))))

(defn above [col row-idx]
  (take row-idx col))

(defn below [col row-idx]
  (drop (inc row-idx) col))

(defn right [row col-idx]
  (below row col-idx))

(defn left [row col-idx]
  (above row col-idx))

(defn is-visible? [row-idx col-idx row-lookup col-lookup]
  (let [col         (col-lookup col-idx)
        row         (row-lookup row-idx)
        tree        (nth row col-idx)
        trees-above (above col row-idx)
        trees-below (below col row-idx)
        trees-left  (left  row col-idx)
        trees-right (right row col-idx)
        ans (boolean
             (or (= 0 row-idx)
                 (= 0 col-idx)
                 (= (dec (count row)) row-idx)
                 (= (dec (count col)) col-idx)
                 (every? #(< % tree) trees-above)
                 (every? #(< % tree) trees-below)
                 (every? #(< % tree) trees-left)
                 (every? #(< % tree) trees-right)))]
    (comment
      (println "\n\n")
      (println "row:" row)
      (println "col:" col)
      (println "tree:" tree)
      (println "row-idx:" row-idx)
      (println "col-idx:" col-idx)
      (println "trees-above:" trees-above)
      (println "trees-below:" trees-below)
      (println "trees-left:" trees-left)
      (println "trees-right:" trees-right)
      (println "ans:" ans))

    ans))

(defn scenic-score [row-idx col-idx row-lookup col-lookup]
  (let [col         (col-lookup col-idx)
        row         (row-lookup row-idx)
        tree        (nth row col-idx)
        can-see     (fn [trees] (let [visible-trees (take-while #(> tree %) trees)
                                      next-tree     (first (drop (count visible-trees) trees))]
                                  (if next-tree
                                    (conj (vec visible-trees) next-tree)
                                    visible-trees)))
        trees-above (can-see (reverse (above col row-idx)))
        trees-below (can-see (below col row-idx))
        trees-left  (can-see (reverse (left  row col-idx)))
        trees-right (can-see (right row col-idx))
        ans         (* (count trees-above)
                       (count trees-below)
                       (count trees-left)
                       (count trees-right))]
    (comment
      (println "\n\n")
      (println "row:" row)
      (println "col:" col)
      (println "tree:" tree)
      (println "row-idx:" row-idx)
      (println "col-idx:" col-idx)
      (println "trees-above:" trees-above)
      (println "trees-below:" trees-below)
      (println "trees-left:" trees-left)
      (println "trees-right:" trees-right)
      (println "ans:" ans))

    ans))

(defn get-visible-coords [input]
  (let [matrix     (input->matrix input)
        row-lookup (into {} (map-indexed vector matrix))
        cols       (utils/cols matrix)
        col-lookup (into {} (map-indexed vector cols))
        col-range  (range (count (first cols)))
        row-range  (range (count (first matrix)))]
    (for [col-idx col-range
          row-idx row-range
          :when (is-visible? row-idx col-idx row-lookup col-lookup)]
      [row-idx col-idx])))

(defn get-scenic-scores [input]
  (let [matrix     (input->matrix input)
        row-lookup (into {} (map-indexed vector matrix))
        cols       (utils/cols matrix)
        col-lookup (into {} (map-indexed vector cols))
        col-range  (range (count (first cols)))
        row-range  (range (count (first matrix)))]
    (for [col-idx col-range
          row-idx row-range]
      (scenic-score row-idx col-idx row-lookup col-lookup))))

(defn part-1 [input]
  (count (get-visible-coords input)))

(defn part-2 [input]
  (apply max (get-scenic-scores input)))

(comment
  (part-1 example-input)
  (part-2 example-input)
  (part-1 (utils/get-input 8));; => 1823
  (part-2 (utils/get-input 8));; => 211680
  (into {} (map-indexed vector (utils/cols (input->matrix example-input))))

  (let [matrix     (input->matrix example-input)
        row-lookup (into {} (map-indexed vector matrix))
        cols       (utils/cols matrix)
        col-lookup (into {} (map-indexed vector cols))
        ;; col-range  (range (count (first cols)))
        ;; row-range  (range (count (first matrix)))
        row-idx     3
        col-idx     3]
    (scenic-score row-idx col-idx row-lookup col-lookup)
    ;; (is-visible? row-idx col-idx row-lookup col-lookup)
    )

  (let [matrix (input->matrix example-input)]
    (for [col-idx (range (count (first (utils/cols matrix))))
          row-idx (range (count (first matrix)))]
      [row-idx col-idx]))

  (def example-input "30373
25512
65332
33549
35390")
  (def example-input-2 "30373
25512
33549
35390"))
