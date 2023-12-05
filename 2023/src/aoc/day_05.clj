(ns aoc.day-05
  (:require [aoc.utils :as u]
            [clojure.string :as str]))

(def example (str/trim "
seeds: 79 14 55 13

seed-to-soil map:
50 98 2
52 50 48

soil-to-fertilizer map:
0 15 37
37 52 2
39 0 15

fertilizer-to-water map:
49 53 8
0 11 42
42 0 7
57 7 4

water-to-light map:
88 18 7
18 25 70

light-to-temperature map:
45 77 23
81 45 19
68 64 13

temperature-to-humidity map:
0 69 1
1 0 69

humidity-to-location map:
60 56 37
56 93 4"))

(def input (u/get-input 5))

(defrecord Range [start end])
(defn overlaps? [r1 r2]
  (and (< (:start r1) (:end r2))
       (> (:end r1) (:start r2))
       (< (:start r2) (:end r1))
       (> (:end r2) (:start r1))))

(defn split-ranges [base-range r-to-split]
  (if (overlaps? base-range r-to-split)
    (let [before (if (< (:start r-to-split) (:start base-range))
                   (->Range (:start r-to-split) (:start base-range))
                   nil)
          middle (->Range (max (:start base-range) (:start r-to-split))
                          (min (:end base-range) (:end r-to-split)))
          after  (if (> (:end r-to-split) (:end base-range))
                   (->Range (:end base-range) (:end r-to-split))
                   nil)
          overlaps   middle
          new-ranges (filter identity [before after])]
      {:overlaps overlaps :new-ranges new-ranges})
    {:overlaps nil :new-ranges [r-to-split]}))

(comment
  (split-ranges (->Range 10 20) (->Range 15 17))
  (split-ranges (->Range 10 20) (->Range 5 7))
  (split-ranges (->Range 10 20) (->Range 25 27))
  (split-ranges (->Range 10 20) (->Range 5 27))
  (split-ranges (->Range 10 20) (->Range 5 15))
  (split-ranges (->Range 10 20) (->Range 15 27)))


(defn parse-mapping [mapping]
  (let [lines         (str/split-lines mapping)
        m-name        (-> lines first (str/split #"\s+") first)
        dest-src-lens (->> (rest lines)
                           (map #(str/split % #"\s+"))
                           (map #(map u/parse-int %))
                           (map (fn [[dest src len]]
                                  (let [src->dest (fn [s] (if (<= src s (+ src len))
                                                            (+ dest (- s src))
                                                            nil))]
                                    {:dest dest
                                     :src src
                                     :len len
                                     :src-range (->Range src (+ src len))
                                     :dest-range (->Range dest (+ dest len))
                                     :src->dest src->dest}))))]
    {:name m-name :dsls dest-src-lens}))

(defn apply-mapping-to-range [mapping range]
  (loop [ranges           [range]
         attempted-ranges []
         mapped-ranges    []
         mappings         (:dsls mapping)]
    ;; (println "ranges:" ranges "attempted:" attempted-ranges "mapped:" mapped-ranges "mappings:" (count mappings))
    (if (empty? mappings)
      (into [] (concat ranges attempted-ranges mapped-ranges))
      (if (empty? ranges)
        (recur attempted-ranges [] mapped-ranges (rest mappings))
        (let [m                             (first mappings)
              r                             (first ranges)
              ;; _ (println m)
              ;; _ (println (:src-range m))
              {:keys [overlaps new-ranges]} (split-ranges (:src-range m) r)
              mapped-range                  (if overlaps
                                              (->Range ((:src->dest m) (:start overlaps))
                                                       ((:src->dest m) (:end overlaps)))
                                              nil)
              new-attempted-ranges          (into attempted-ranges new-ranges)
              new-mapped-ranges             (if mapped-range
                                              (conj mapped-ranges mapped-range)
                                              mapped-ranges)]
          (recur (rest ranges) new-attempted-ranges new-mapped-ranges mappings))))))

(defn seed-range->location-ranges [seed-range all-mappings]
  (loop [ranges   [seed-range]
         mappings all-mappings]
    ;; (println "ranges:" ranges)
    (if (empty? mappings)
      ranges
      (recur (mapcat (fn [r] (apply-mapping-to-range (first mappings) r)) ranges)
             (rest mappings)))))

(defn follow-the-trail-ranges [data]
  (let [{:keys [seed-ranges mappings]} data]
    (mapcat #(seed-range->location-ranges % mappings) seed-ranges)))

(defn parse-input [input]
  (let [chunks   (str/split input #"\n\n")

        seeds    (as-> (first chunks) $
                   (str/split $ #":")
                   (second $)
                   (str/trim $)
                   (str/split $ #"\s+")
                   (map u/parse-int $))
        mappings (map parse-mapping (rest chunks))]
    {:seeds seeds
     :seed-ranges (map (fn [[start len]] (->Range start (+ start len))) (partition 2 seeds))
     :mappings mappings}))

(comment
  (let [{:keys [seed-ranges mappings]} (parse-input example)]
    (apply-mapping-to-range (first mappings) (first seed-ranges)))

  (follow-the-trail-ranges (parse-input example))
  ;
  )

(defn next-loc [mapping loc]
  (let [answers (mapv #((:src->dest %) loc) (:dsls mapping))]
    (reduce (fn [ans candidate]
              ;; use reduced ?
              ;; (println "ans:" ans "candidate:" candidate)
              (if ans ans candidate))
            (conj answers loc))))

(defn follow-the-trail [data kw]
  (->> data
       kw
       (map (fn [seed]
              (reduce (fn [loc mapping]
                        (next-loc mapping loc))
                      seed
                      (:mappings data))))))

(comment
  (parse-input example)
  ;
  )

(defn pt1 [input]
  (let [parsed (parse-input input)
        ans    (follow-the-trail parsed :seeds)]
    (apply min ans)))

(defn pt2 [input]
  (let [parsed (parse-input input)
        ans    (follow-the-trail-ranges parsed)]
    (:start (first (sort-by :start ans)))))

(comment
  (pt1 example)
  (pt1 input)   ;; 309796150
  (pt2 example)
  ;; (count (:seeds-pt2 (parse-input input))) ;; never finishes
  (time (pt2 input)) ;; 50716416
;
  )


(comment
  (overlaps? (->Range 0 10) (->Range 11 15))
  (overlaps? (->Range 0 10) (->Range 10 15))
  (overlaps? (->Range 0 10) (->Range 9 15))
  (overlaps? (->Range 0 10) (->Range 5 7))
  (overlaps? (->Range 11 15) (->Range 0 10))
  (overlaps? (->Range 10 15) (->Range 0 10))
  (overlaps? (->Range 9 15)  (->Range 0 10))
  (overlaps?  (->Range 5 7)  (->Range 0 10))
  (overlaps?  (->Range 0 -5)  (->Range 0 10))
  (overlaps? (->Range 0 1) (->Range 0 1))
  )
