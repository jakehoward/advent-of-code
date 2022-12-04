(ns org.jakehoward.aoc.days.day-4
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as string]
            [clojure.set :as set]))

(defn process-row [row]
  (let [[elf-one elf-two]     (string/split row #",")
        [[e1s e1e] [e2s e2e]] (map #(string/split % #"-") [elf-one elf-two])
        [[e1s e1e] [e2s e2e]] (map #(map (fn [n] (Integer/parseInt n)) %) [[e1s e1e] [e2s e2e]])]
    (map (fn [[s e]] (utils/range-incl s e)) [[e1s e1e] [e2s e2e]])))

(def input (->> (utils/get-input 4)
                (utils/lines)
                (map process-row)))

(defn fully-contained [a b]
  (= (count (set/intersection (set a) (set b)))
     (min (count a) (count b))))

(defn play []
  (->> input
       (filter (fn [[elf-one-zone elf-two-zone]] (fully-contained elf-one-zone elf-two-zone)))
       count))

(comment
  (let [[a b] (nth input 7)]
    ;; [a b]
    (fully-contained a b)
    )

  (let [
        ;; raw "3-3,7-31"
        ;; raw "96-96,9-96"
        ;; raw "1-2,2-97"
        raw (last (utils/lines (utils/get-input 4)))
        [a b] (process-row raw)]
    (fully-contained a b))

  (fully-contains [] [:a])
  (last (utils/lines (utils/get-input 4)))
  (play) ;; => 564
  )
