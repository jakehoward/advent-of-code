(ns org.jakehoward.aoc.days.day-3
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.set :as set]))

(def input (-> (utils/get-input 3)
               (utils/lines)))

(def a->z (->> (range 97 (+ 97 26))
               (map char)))

(def A->Z (map #(Character/toUpperCase %) a->z))

(def points (into {} (map-indexed #(vector %2 (inc %1)) (concat a->z A->Z))))

(defn split-in-half [s]
  (let [half (/ (count s) 2)]
    [(take half s) (drop half s)]))

(defn find-overlap [[first-compartment second-compartment]]
  (set/intersection (set first-compartment) (set second-compartment)))

(defn play []
  (let [compartment-pairs (map split-in-half input)
        matching-items    (->> (map find-overlap compartment-pairs)
                               (map first))
        scores            (map points matching-items)]
    (reduce + scores)))

(defn find-common-item [seqs]
  (-> (apply set/intersection (map set seqs))
      (first)))

(defn play-pt2 []
  (let [groups        (partition 3 input)
        common-items  (map find-common-item groups)
        scores        (map points common-items)]
    (reduce + scores)))

(comment
  (play) ;; => 7980
  (play-pt2) ;; => 2881
  )
