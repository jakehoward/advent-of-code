(ns org.jakehoward.aoc.days.day-4
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as string]
            [clojure.set :as set]))

(defn process-row [row]
  (let [[elf-one elf-two]     (string/split row #",")
        [[e1s e1e] [e2s e2e]] (map #(string/split % #"-") [elf-one elf-two])
        [[e1s e1e] [e2s e2e]] (map #(map (fn [n] (Integer/parseInt n)) %) [[e1s e1e] [e2s e2e]])]
    [[e1s e1e] [e2s e2e]]))


(def input (->> (utils/get-input 4)
                (utils/lines)
                (map process-row)))


(defn fully-contained [[[as ae] [bs be]]]
  (cond
    (= as bs)  (or (<= ae be) (<= be ae))
    (> as bs) (<= ae be)
    (> bs as) (<= be ae)
    :else      false))


(defn play []
  (->> input
       (filter fully-contained)
       count))

(comment
  (play) ;; => 528
  )
