(ns org.jakehoward.aoc.core
  (:require [clojure.java.io :as io]))

(defn get-input [day]
  (-> (format "inputs/days/%s.txt" day)
      io/resource
      slurp
      clojure.string/trim))

(defn lines [txt]
  (clojure.string/split txt #"\n"))

(comment
  ;; Day 1: https://adventofcode.com/2021/day/1
  (def day-1-data
    (->> (get-input 1)
         lines
         (map #(Integer/parseInt %))))

  (defn count-increasing [nums]
    (->> nums
       (partition 2 1)
       (map (fn [[a b]] (< a b)))
       (filter identity)
       count))

  ;; part one => 1759
  (-> day-1-data
      count-increasing)

  ;; part 2 => 1805
  (->> day-1-data
       (partition 3 1)
       (map (partial apply +))
       count-increasing)


  ;; Day 2: https://adventofcode.com/2021/day/2
  (def day-2-data
    (->> (get-input 2)
         lines
         (map #(clojure.string/split % #" "))
         (map (fn [[operation num]] [operation (Integer/parseInt num)]))))

  (def operations {"forward" +
                   "down" +
                   "up" -})

  ;; part 1 => 1,660,158
  (*
   (->> day-2-data
        (filter (fn [[op]] (= "forward" op)))
        (map second)
        (reduce +))
   (->> day-2-data
        (filter (fn [[op]] (not= "forward" op)))
        (reduce (fn [acc [op num]] (apply (operations op) [acc num])) 0)))

  ;; part 2 => 1,604,592,846
  (->> day-2-data
   (reduce (fn [acc [op num]]
             (condp = op
               "forward" (-> acc
                             (update :horizontal #(+ % num))
                             (update :depth #(+ % (* (:aim acc) num))))
               "up" (update acc :aim #(- % num))
               "down" (update acc :aim #(+ % num))))
           {:aim 0 :depth 0 :horizontal 0})
   vector
   (apply (fn [{:keys [depth horizontal]}] (* depth horizontal))))
  )
