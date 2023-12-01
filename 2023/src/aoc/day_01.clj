(ns aoc.day-01
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input (-> (io/resource "day-1.txt")
               slurp))

(defn sum [xs] (reduce + 0 xs))
(defn str->cs [s] (str/split s #""))

(defn pt1 [input]
  (let [lines     (str/split-lines input)
        int-strs  #{"1" "2" "3" "4" "5" "6" "7" "8" "9" "0"}
        first-int (fn [line] (->> line str->cs (drop-while #(not (contains? int-strs %))) first))
        last-int  (fn [line] (->> line str->cs reverse (drop-while #(not (contains? int-strs %))) first))
        cal-vals  (->> lines
                       (map #((juxt first-int last-int) %))
                       (map #(Integer/parseInt (str/join %))))
        ans       (sum cal-vals)]
    ans))

(def num-strs  ["0" "1"   "2"   "3"     "4"    "5"    "6"   "7"     "8"     "9"])
(def num-words [    "one" "two" "three" "four" "five" "six" "seven" "eight" "nine"])

(defn figure-it-out [line]
  (->> (concat num-strs num-words)
       (map (fn [s] [s (str/index-of line s)]))
       (filter (fn [[s idx]] idx))
       (sort-by second)
       (map first)))
(comment
  (figure-it-out "sevenine")
  (figure-it-out "7sevenine")
  (figure-it-out ""))

(defn figure-it-out-no-overlaps [line]
  (loop [tokens []
         s      line]
    ;; if index-of any of the tokens is zero add to found and remove from s
    (if (empty? s)
      tokens

      (let [found
            (->> (concat num-strs num-words)
                 (map (fn [token] [token (str/index-of s token)]))
                 (filter (fn [[t idx]] (and idx (zero? idx))))
                 first)]
        (if found
          (recur (conj tokens (first found)) (if (>= (count s) (count (first found)))
                                               (.substring s (count (first found)))
                                               ""))
          (recur tokens (.substring s 1)))))))

(comment
  (figure-it-out-no-overlaps "sevenine")
  (figure-it-out "7sevenine")
  (figure-it-out ""))


(comment
  (.indexOf num-strs "0")
  (let [l "xadfone3azdfive9sadfzero"]
    (figure-it-out l)))

(defn pt2 [input]
  (let [lines       (str/split-lines input)
        lnums       (map figure-it-out lines)
        fst-lst     (map (fn [nums] ((juxt first last) nums)) lnums)
        nums-2-sumz (map (fn [[a b]] (str (max (.indexOf num-strs a)
                                               (inc (.indexOf num-words a)))
                                          (max (.indexOf num-strs b)
                                               (inc (.indexOf num-words b))))) fst-lst)
        ans (sum (map #(Integer/parseInt %) nums-2-sumz))
        ans nums-2-sumz
        ;; ans fst-lst
        ;; ans lnums
        ]
    ans))

(comment
  (count (pt2 input));; 35739 (too low) ;; 54498 ;; 54498 ;; 54498 (too low)

  (let [input "two1nine
eightwothree
abcone2threexyz
xtwone3four
4nineeightseven2
zoneight234
7pqrstsixteen"]
    (pt2 input))
  ;; (29 83 13 24 42 14 76) ;; ("29" "83" "13" "24" "42" "14" "76")

  (pt1 input)
  (let [input "1abc2
pqr3stu8vwx
a1b2c3d4e5f
treb7uchet"]
    (pt1 input)))
