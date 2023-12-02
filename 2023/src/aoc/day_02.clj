(ns aoc.day-02
  (:require [aoc.utils :as u]
            [clojure.string :as str]))

(def example
  "Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green")

(def input (u/get-input 2))

(def max-rgb {"red"   12
              "green" 13
              "blue"  14})

(defn possible-draw? [rgb]
  (every? (fn [[col n]] (>= (get max-rgb col) n)) rgb))

(comment
  (possible-draw? {"green" 13
              "red" 0
              "blue" 0}))

(defn parse-draw [draw-str]
  (let [draws (-> draw-str (str/split #", "))
        ans   (->> draws
                   (map (fn [d] (str/split d #" ")))
                   (map (fn [[n col]] {col (u/parse-int n)})))]
    (reduce merge {"red" 0 "green" 0 "blue" 0} ans)))

(defn parse-game [line]
  (let [game-id (-> line (str/split #": ") first (str/split #" ") second)
        draws   (map parse-draw (-> line (str/split #": ") second (str/split #"; ")))]
    {:id (u/parse-int game-id)
     :draws draws}))

(defn pt1 [input]
  (let [lines      (str/split-lines input)
        games      (map parse-game lines)
        poss-games (filter (fn [g] (every? possible-draw? (:draws g))) games)
        poss-ids   (map :id poss-games)
        ans        (u/sum poss-ids)]
    ans))

(defn add-max-draw [game]
  (let [draws     (:draws game)
        max-draws (reduce (partial merge-with max) draws)]
    (assoc game :max-draws max-draws)))

(defn pt2 [input]
  (let [lines      (str/split-lines input)
        games      (map parse-game lines)
        games      (map add-max-draw games)
        powers     (map #(reduce * 1 (-> % :max-draws vals)) games)
        ans        (u/sum powers)]
    ans))



(comment
  (pt2 input);; 72596
  (pt1 input)
  
  (type (-> (str/split-lines example) first (str/split #": ") second (str/split #"; ")))
  (map parse-game (str/split-lines example))
  ;
  )
