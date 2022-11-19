(ns org.jakehoward.aoc.day-8
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as string]
            [clojure.set :as set]
            [clojure.string :as str]))

(def pt1-data (->> (utils/get-input 8)
                   utils/lines
                   (map #(string/split % #" \| "))
                   (map second)
                   (map #(string/split % #" "))))

(def pt2-data (->> (utils/get-input 8)
                   utils/lines
                   (map #(string/split % #" \| "))
                   (map (fn [inputs] (map #(string/split % #" ") inputs)))))

(defn only [xs]
  (if (> (count xs) 1)
    (throw (Exception. (str "only found " (count xs) " matches in " xs)))
    (first xs)))

(defn num-shared [& ss]
  (count (apply set/intersection ss)))

(defn has-count [items c]
  (let [matches (filter #(= c (count %)) items)]
    (if (> (count matches) 1)
      (throw (Exception. (str "has-count found " (count matches) "matches for count " c)))
      (first matches))))

(defn signal->num [samples]
  (let [sorted-samples (sort-by count samples)
        mapping        {1 (has-count samples 2)
                        7 (has-count samples 3)
                        4 (has-count samples 4)
                        8 (has-count samples 7)}
        mapping         (merge mapping
                               {3 (->> samples
                                       (filter #(= 5 (count %)))
                                       (filter #(and (= 2 (num-shared (set %)
                                                                      (set (get mapping 1))
                                                                      (set (get mapping 4))))))
                                       only)
                                9 (->> samples
                                       (filter #(= 6 (count %)))
                                       (filter #(and (= 4 (num-shared (set %)
                                                                      (set (get mapping 4))))))
                                       only)})
        mapping          (merge mapping
                                {2 (->> samples
                                        (filter #(= 5 (count %)))
                                        (filter #(and (= 4 (num-shared (set %)
                                                                       (set (get mapping 3))))
                                                      (= 2 (num-shared (set %)
                                                                       (set (get mapping 4))))))
                                        only)
                                 5 (->> samples
                                        (filter #(= 5 (count %)))
                                        (filter #(and (= 4 (num-shared (set %)
                                                                       (set (get mapping 3))))
                                                      (= 3 (num-shared (set %)
                                                                       (set (get mapping 4))))))
                                        only)})
        mapping          (merge mapping
                                {6 (->> samples
                                        (filter #(= 6 (count %)))
                                        (filter #(and (= 5 (num-shared (set %)
                                                                       (set (get mapping 5))))
                                                      (not= % (get mapping 9))))
                                        only)})
        mapping          (merge mapping
                                {6 (->> samples
                                        (filter #(= 6 (count %)))
                                        (filter #(and (not= % (get mapping 9))
                                                      (not= % (get mapping 6))))
                                        only)})
        mapping          (merge mapping
                                {0 (->> samples
                                        (filter #(= 6 (count %)))
                                        (filter #(and (not= % (get mapping 9))
                                                      (not= % (get mapping 6))))
                                        only)})]
    (reduce-kv (fn [m k v] (assoc m (set v) k)) {} mapping)))


(defn deduce-nums [[sample nums]]
  (let [mapping (signal->num sample)
        set-nums (map set nums)]
    (map mapping set-nums)))


(defn solve-pt2 []
  (let [nums-segments (map deduce-nums pt2-data)
        nums          (map #(Integer/parseInt (string/join %)) nums-segments)]
    (reduce + nums)))


(comment
  (->> pt1-data
       (map #(map count %))
       flatten
       (filter #{7 4 2 3})
       count) ;; => 392

  (signal->num (first (nth pt2-data 100)))

  ;; => 967206
  (solve-pt2))

