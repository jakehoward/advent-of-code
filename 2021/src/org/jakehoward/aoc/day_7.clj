(ns org.jakehoward.aoc.day-7
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as string]))

(def initial-positions (->> (string/split (utils/get-input 7) #",")
                            (map #(Integer/parseInt %))))

(defn make-summed-ints
  ([] (make-summed-ints 0 0))
  ([n sum]
   (lazy-seq
    (cons (+ n sum) (lazy-seq (make-summed-ints (inc n) (+ n sum)))))))

(def summed-ints (make-summed-ints))


(defn fuel-cost-human [a b]
  (if (> a b)
    (- a b)
    (- b a)))

(defn fuel-cost-crab [a b]
  ;; (nth summed-ints (fuel-cost-human a b)) ;; slower
  (reduce + (range (inc (fuel-cost-human a b)))))

(defn solve [cost-fn]
  (let [min-pos       (apply min initial-positions)
        max-pos       (apply max initial-positions)
        all-positions (range min-pos (inc max-pos))
        all-costs     (for [pos-to-try all-positions]
                        (let [costs      (map #(cost-fn % pos-to-try) initial-positions)
                              total-cost (reduce + costs)]
                          {:pos pos-to-try :cost total-cost}))
        sorted-costs  (sort-by :cost all-costs)]
    (first sorted-costs)))

(comment
  (time (solve fuel-cost-human)) ;; => {:pos 341, :cost 349357}

  ;; "Elapsed time: 12586.938099 msecs"
  (time (solve fuel-cost-crab));; => {:pos 480, :cost 96708205}

  ;; with memoize
  (time (solve (memoize fuel-cost-crab)))

  (time (nth summed-ints 5000))
  (time (reduce + (range (inc 5000)))))
