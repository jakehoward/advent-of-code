(ns org.jakehoward.aoc.days.day-5
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as string]))

(defrecord Instruction [num-crates from to])

(defn parse-instruction [ins-str]
  ;; "move 8 from 3 to 2"
  (let [[_ num-crates _ from _ to] (string/split ins-str #"\s+")]
    (->Instruction (Integer/parseInt num-crates) from to)))

(def instructions (->> (-> (utils/get-input 5)
                           (string/split #"\n\n")
                           second
                           (string/split #"\n"))
                       (map parse-instruction)))

(defn get-column-at-idx [rows idx]
  (->> rows
       (map #(nth % idx))
       (filter #(not= % \space))
       vec))

(defn parse-crates [crate-str]
  (let [rows                (string/split crate-str #"\n")
        data-rows           (butlast rows)
        reversed-data-rows  (reverse data-rows)
        idx-row             (last rows)]
    ;; Lean on fact things are nicely lined up.
    ;; If there's a non-space char in idx-row
    ;; then we can get the crate from the rows
    (reduce (fn [crates [idx char]]
              (if-not (= \space char)
                (assoc crates (str char) (get-column-at-idx reversed-data-rows idx))
                crates))
            {}
            (map-indexed vector idx-row))))

(def crates (-> (utils/get-input 5)
                (string/split #"\n\n")
                first
                parse-crates))

(defn process-instruction [crates {:keys [num-crates from to]} grab-fn]
  (let [from-crate-stack  (crates from)
        crates-to-move    (take num-crates (reverse from-crate-stack)) ;; partition?
        remaining-crates  (reverse (drop num-crates (reverse from-crate-stack)))
        to-crate-stack    (crates to)
        updated-to-stack  (concat to-crate-stack (grab-fn crates-to-move))]
    (-> crates
        (assoc from remaining-crates)
        (assoc to   updated-to-stack))))

(defn play [grab-fn]
  (let [final-state     (reduce #(process-instruction %1 %2 grab-fn) crates instructions)
        top-boxes       (->> final-state
                             (map (fn [[idx crates]] [idx crates]))
                             (sort-by first)
                             (map second)
                             (map last)
                             (string/join ""))]
    top-boxes))

(comment
  crates
  (play identity) ;; => "WHTLRMZRC"
  (play reverse) ;; =>  "GMPMLWNMG"
  )

