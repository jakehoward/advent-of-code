(ns org.jakehoward.aoc.core
  (:require [clojure.java.io :as io]))

(defn get-input [day]
  (-> (format "inputs/days/%s.txt" day)
      io/resource
      slurp
      clojure.string/trim))

(defn lines [txt]
  (clojure.string/split txt #"\n"))

;; (take 3 day-3-data)

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

  ;; Day 3: https://adventofcode.com/2021/day/3
  (def day-3-data
    (->> (get-input 3)
         lines
         (map #(clojure.string/split % #""))))

  (defn colify [rows]
    (apply map vector rows))

  (def columns (colify day-3-data))

  (defn most-common [xs]
    (->> xs
         (group-by identity)
         (map (fn [[value items]] [value (count items)]))
         (sort-by second)
         reverse
         ffirst))

  (defn least-common [xs]
    (->> xs
         (group-by identity)
         (map (fn [[value items]] [value (count items)]))
         (sort-by second)
         ffirst))

  ;; part 1 => 4,006,064
  (def gamma (clojure.string/join (map most-common columns)))
  (def epsilon (clojure.string/join (map least-common columns)))
  (* (Integer/parseInt gamma 2)
     (Integer/parseInt epsilon 2))

  ;; part 2 => 5941884
  (defn get-row-idxs-least [column]
    (let [num-1s (count (filter #(= "1" %) column))
          num-0s (count (filter #(= "0" %) column))
          its-a-tie (= num-1s num-0s)
          get-idxs-for (if (or its-a-tie (= "0" (least-common column)))
                         "0"
                         "1")]
      (keep-indexed (fn [idx v] (when (= v get-idxs-for) idx)) column)))

  (defn get-row-idxs-most [column]
    (let [num-1s (count (filter #(= "1" %) column))
          num-0s (count (filter #(= "0" %) column))
          its-a-tie (= num-1s num-0s)
          get-idxs-for (if (or its-a-tie (= "1" (most-common column)))
                         "1"
                         "0")]
      (keep-indexed (fn [idx v] (when (= v get-idxs-for) idx)) column)))

  (defn get-remaining-rows [rows col-idx mode]
    (let [columns (colify rows)
          column (nth columns col-idx)
          get-row-idxs-algo (condp = mode
                              :least get-row-idxs-least
                              :most get-row-idxs-most)]
      (vals (select-keys (vec rows) (get-row-idxs-algo column)))))

  (defn find-rating [mode]
    (loop [rows day-3-data
           col-idx 0]
      (if (= 1 (count rows))
        (clojure.string/join (first rows))
        (let [remaining-rows (get-remaining-rows rows col-idx mode)]
          (recur remaining-rows (inc col-idx))))))

  (defn binary-string->int [s]
    (Integer/parseInt s 2))

  (get-remaining-rows [["0" "1"] ["0" "1"]] 1 :most)

  (def day3-pt2-ans
    (*
     (binary-string->int (find-rating :least))
     (binary-string->int (find-rating :most))))

  (println day3-pt2-ans))
