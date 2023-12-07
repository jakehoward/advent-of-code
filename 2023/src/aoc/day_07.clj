(ns aoc.day-07
  (:require [aoc.utils :as u]
            [clojure.string :as str]
            [clojure.set :as set]
            [clojure.algo.generic.functor :refer [fmap]]))

(def example (str/trim "
32T3K 765
T55J5 684
KK677 28
KTJJT 220
QQQJA 483"))
(def input (u/get-input 7))

(def card->num (merge (reduce #(assoc %1 (str %2) %2) {} (range 1 10))
                      (reduce #(assoc %1 (first (str %2)) %2) {} (range 1 10))
                      {"T" 10 "J" 11 "Q" 12 "K" 13 "A" 14}
                      {\T 10 \J 11 \Q 12 \K 13 \A 14}))

(def num->card-str (fmap str (reduce-kv (fn [acc k v] (assoc acc v k)) {} card->num)))
(def card->num-pt2 (merge card->num {\J 1 "J" 1}))

(def hand-order
  [:five
   :four
   :full-house
   :three
   :two-pair
   :pair
   :high])

(defn hand-sorter [card->num]
  (fn [a b]
    (cond (= (:strength a) (:strength b))
          (let [first-diff (-> (drop-while (fn [[ac bc]] (= ac bc))
                                           (map vector
                                                (map card->num (:hand a))
                                                (map card->num (:hand b))))
                               first)]
            (cond (nil? first-diff)        0
                  (> (first first-diff)
                     (second first-diff)) -1
                  :else                    1))

          (> (:strength a) (:strength b))
          -1

          :else
          1)))

(defn sort-hands-s->w
  ([hands]
   (sort-hands-s->w card->num hands))
  ([card->num-to-use hands]
   (sort (hand-sorter card->num-to-use) hands)))

(defn hand-strength [{:keys [type]}] (- (count hand-order) (.indexOf hand-order type)))

(defn hand-type [{:keys [hand]}]
  (let [cards       (str/split hand #"")
        card-counts (->> (group-by identity cards) (map second) (map count) sort reverse)]
    (condp = card-counts
      [5]       :five
      [4 1]     :four
      [3 2]     :full-house
      [3 1 1]   :three
      [2 2 1]   :two-pair
      [2 1 1 1] :pair
      :high)))

(defn resolve-jokers [hand]
  (let [cards (str/split hand #"")]
    (cond
      (every? #(= "J" %) cards) (str/join (repeat (count cards) "A"))
      (every? #(not= "J" %) cards) (str/join cards)

      :else
      (let [cards                   (str/split hand #"")
            [jokers non-jokers]     (->> cards
                                         (group-by #(get {\J :joker "J" :joker} % :not-joker))
                                         ((juxt :joker :not-joker)))
            non-joker-counts        (group-by
                                     (fn [card] (count (filter #(= % card) non-jokers)))
                                     non-jokers)
            max-count               (apply max (keys non-joker-counts))
            best-card-to-copy-num   (->> max-count
                                         (get non-joker-counts)
                                         (map card->num-pt2)
                                         (apply max))
            best-card-to-copy       (num->card-str best-card-to-copy-num)

            resolved
            (->> cards
             (map (fn [c] (if (#{\J "J"} c) best-card-to-copy c)))
             (str/join ""))]

        (comment
          (when (< (rand) 0.1)
            (println hand "->" resolved ":"
                     (hand-type {:hand hand}) "->" (hand-type {:hand resolved}))))

        resolved))))

(defn parse-input [input]
  (->> (str/split-lines input)
       (map (fn [l] (let [[h b] (str/split l #"\s+")] {:hand h :bid (u/parse-int b)})))))

(defn pt1 [input]
  (let [hands (->> (parse-input input)
                   (map #(assoc % :type (hand-type %)))
                   (map #(assoc % :strength (hand-strength %))))
        sorted-hands (reverse (sort-hands-s->w card->num-pt2 hands))
        scores       (map (fn [h r] (* r (:bid h))) sorted-hands (drop 1 (range)))
        ans          (u/sum scores)]
    ans))

(defn pt2 [input]
  (let [hands (->> (parse-input input)
                   (map #(assoc % :type (hand-type (update % :hand resolve-jokers))))
                   (map #(assoc % :strength (hand-strength %))))
        sorted-hands (reverse (sort-hands-s->w card->num-pt2 hands))
        scores       (map (fn [h r] (* r (:bid h))) sorted-hands (drop 1 (range)))
        ans          (u/sum scores)]
    ans))

(comment
  (pt1 example)
  (pt1 input)
  (pt2 example)
  (pt2 input)
;
  )

(comment
  (let [hands ["AAAAA" "AAAAQ" "AAAKK" "AAAKJ" "AAKKJ" "AAKJT" "AT978"]]
    (map hand-type hands))

  (parse-input example)

  (resolve-jokers "2JJJT")

  (sort (hand-sorter card->num)
        [{:hand "" :strength 5} {:hand "" :strength 4} {:hand "" :strength 5}])
  (sort (hand-sorter card->num)
        [{:hand "A" :strength 5} {:hand "" :strength 4} {:hand "1" :strength 5}])
  (sort (hand-sorter card->num)
        [{:hand "AAAA1" :strength 5} {:hand "" :strength 4} {:hand "AAAA2" :strength 5}]))
