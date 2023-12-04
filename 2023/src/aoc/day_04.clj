(ns aoc.day-04
  (:require [aoc.utils :as u]
            [clojure.string :as str]
            [clojure.set :as set]))

;; Assumption: num only appears once in either winning or my nums

;; winning | your nums
(def example
  (str/triml
   "
Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11"))

(def input (u/get-input 4))

(defn num-winning-nums [card]
  (count (set/intersection (:winning card) (:mine card))))

(defn parse-card [card]
  (let [[card-id-s nums-s] (str/split card #":\s+")
        card-id            (-> card-id-s str/trim (str/split #"\s+") second u/parse-int)
        [win-s my-s]       (-> nums-s (str/split #"\s+\|\s+"))
        parse-nums         (fn [num-s] (->> (str/split (str/trim num-s) #"\s+")
                                            (map u/parse-int)
                                            set))
        [win-ns my-ns]     [(parse-nums win-s) (parse-nums my-s)]]
    {:id card-id :winning win-ns :mine my-ns}))

(comment (parse-card "Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53"))

(defn get-score [card]
  (let [overlap-size (count (set/intersection (:winning card) (:mine card)))]
    (if (>= overlap-size 1)
      (.pow (bigdec 2) (dec overlap-size))
      0)))

(defn pt1 [input]
  (let [cards  (map parse-card (str/split-lines input))
        scores (map get-score cards)
        ans    (u/sum scores)]
    ans))

;; make this recursive so can memoize
;; and build a lookup of every card it's
;; seen along the way
(defn get-total-for-one-card [card-by-id id]
  (let [curr-card (card-by-id id)]
    (apply + 1 (map (partial get-total-for-one-card card-by-id)
                    (range (inc (:id curr-card))
                           (inc (+ (:id curr-card) (:num-winning curr-card))))))))

(defn get-total-num-cards [card-by-id]
  (let [card-ids  (keys card-by-id)
        get-total (memoize (partial get-total-for-one-card card-by-id))]
    (u/sum (map get-total card-ids))))


(defn pt2 [input]
  (let [cards           (map parse-card (str/split-lines input))
        cards-with-nw   (map #(assoc % :num-winning (num-winning-nums %)) cards)
        card-by-id      (reduce #(assoc %1 (:id %2) %2) {} cards-with-nw)
        total-num-cards (get-total-num-cards card-by-id)
        ans             total-num-cards]
    ans))

(comment
  (time (pt2 input));; 9881048
  (pt2 example)
  (pt1 example)
  (pt1 input) ;; 21919M
  )


