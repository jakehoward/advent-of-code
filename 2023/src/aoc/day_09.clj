(ns aoc.day-09
  (:require [aoc.utils :as u]
            [clojure.string :as str]))

(def example (str/trim "
0 3 6 9 12 15
1 3 6 10 15 21
10 13 16 21 30 45"))

(def input (u/get-input 9))

(defn parse-input [input]
  (->> (str/split-lines input)
       (mapv #(str/split (str/trim %) #"\s+"))
       (mapv #(mapv u/parse-int %))))

(defn triangle [nums]
  (loop [curr-nums nums
         diffs     []
         steps     0]
    (if (or (> steps 100) (every? #(= 0 %) curr-nums))
      diffs
      (recur (mapv (fn [[a b]] (- b a)) (partition 2 1 curr-nums))
             (conj diffs curr-nums)
             (inc steps)))))

;; [[10 13 16 21 30 45] [3 3 5 9 15] [0 2 4 6] [2 2 2]]
(defn- next-num [nums]
  (let [tri (triangle nums)]
    (reduce (fn [acc diffs] (+ acc (last diffs)))
            (last (last tri))
            (reverse (butlast tri)))))

(defn- prev-num [nums]
  (let [tri (triangle nums)]
    (reduce (fn [acc diffs] (- (first diffs) acc))
            (first (last tri))
            (reverse (butlast tri)))))

(defn pt1 [input]
  (let [parsed (parse-input input)
        ans    (map next-num parsed)]
    (u/sum ans)))

(comment
  (pt1 example)
  (pt1 input) ;; 1584748274
;
)

(defn pt2 [input]
  (let [parsed (parse-input input)
        ans    (map prev-num parsed)]
    (u/sum ans)))

(comment
  (pt1 example)
  (pt1 input)
  (pt2 example)
  (pt2 input) 1026
;
)
