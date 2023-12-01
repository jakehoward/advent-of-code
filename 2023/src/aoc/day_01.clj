(ns aoc.day-01
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

;; todo - write a macro that prints/returns all intermediate forms of a let

(def input (-> (io/resource "day-1.txt")
               slurp))

(defn sum [xs] (reduce + 0 xs))
(defn str->cs [s] (str/split s #""))

(def num-strs  ["0" "1" "2" "3" "4" "5" "6" "7" "8" "9"])
(def word-strs [:_zero "one" "two" "three" "four" "five" "six" "seven" "eight" "nine"])
(def all-tokens (concat num-strs (rest word-strs)))
(defn token->int [t]
  (max (.indexOf num-strs t) (.indexOf word-strs t)))

(comment (map #((juxt identity token->int) %) all-tokens))


(defn line->num-tokens [line]
  (->> all-tokens
       (map (fn [token] [token (str/index-of line token)]));; no - can happen twice, need all idxs
       (remove (fn [[t idx]] (nil? idx)))
       (sort-by second)
       (map first)))

(defn line->tokens [line]
  (loop [tokens []
         s      line]
    (if (empty? s)
      tokens
      (let [found (->> all-tokens
                       (map (fn [token] [token (str/index-of s token)]))
                       (filter (fn [[t idx]] (= 0 idx)))
                       ffirst)]
        (if found
          (recur (conj tokens found) (.substring s 1))
          (recur tokens (.substring s 1)))))))


(comment
  (->> (line->tokens "sdfoneight1sevenine19")
       (map #((juxt identity token->int) %))))

(defn pt2 [input]
  (let [lines       (str/split-lines input)
        lnums       (map line->tokens lines)
        fst-lst     (map (fn [nums] ((juxt first last) nums)) lnums)
        nums-2-sumz (map (fn [[a b]] (str (token->int a) (token->int b))) fst-lst)
        ans         (sum (map #(Integer/parseInt %) nums-2-sumz))
        ;; ans nums-2-sumz
        ;; ans fst-lst
        ;; ans lnums
        ]
    ans))

(defn run-test-data []
  (let [input "two1nine
eightwothree
abcone2threexyz
xtwone3four
4nineeightseven2
zoneight234
7pqrstsixteen"]
    (pt2 input)))

(comment
  (run-test-data)
  (pt2 input) ;; 54518
  )




;;; ---------------------------------------------------
;;; ---------------------------------------------------
;;; ---------------------------------------------------


(comment(defn figure-it-out-no-overlaps [line]
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
                  (recur tokens (.substring s 1))))))))


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

(comment
  (pt1 input)

  (let [input "1abc2
pqr3stu8vwx
a1b2c3d4e5f
treb7uchet"]
    (pt1 input)))
