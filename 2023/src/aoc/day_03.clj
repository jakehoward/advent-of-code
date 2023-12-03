(ns aoc.day-03
  (:require [aoc.utils :as utils]
            [clojure.string :as str]
            [aoc.utils :as u]
            [clojure.set :as set]))

(def example "467..114..
...*......
..35..633.
......#...
617*......
.....+.58.
..592.....
......755.
...$.*....
.664.598..")

(def input (utils/get-input 3))

(defrecord Num [n yx-coords])
(defrecord Sym [text yx-coord])

(defn is-sym-text? [s]
  (and (not (contains? utils/int-strings s))
       (not= "." s)))

(defn num-collector->Number [items]
  (let [n   (u/parse-int (str/join (map first items)))
        yxs (map second items)]
    (->Num n yxs)))

(comment
  (num-collector->Number [[4 [0 0]] [6 [0 1]] [7 [0 2]]]))

(defn parse-row [row row-y]
  (loop [rem           row
         idx           0
         num-collector []
         tokens        []]
    (comment (println "rem:" rem, "coll:" num-collector "tokens:" tokens))

    (if (empty? rem)
      (if (seq num-collector)
        (conj tokens (num-collector->Number num-collector))
        tokens)

      (let [entry        (first rem)
            entry-is-int (contains? utils/int-strings entry)
            next-nc      (if entry-is-int
                           (conj num-collector [entry [row-y idx]])
                           [])

            next-tokens  tokens
            next-tokens  (if (and (seq num-collector) (not entry-is-int))
                           (conj next-tokens (num-collector->Number num-collector))
                           next-tokens)
            next-tokens  (if (is-sym-text? entry)
                           (conj next-tokens (->Sym entry [row-y idx]))
                           next-tokens)]

        (recur (rest rem) (inc idx) next-nc next-tokens)))))

(defn num->neighbour-yx [matrix num]
  (->> num
       :yx-coords
       (mapv #(utils/get-neighbours-coords-yx matrix %))
       (apply concat)
       set))

(comment
  (let [m [["1" "2" "."]
           ["." "." "."]]
        n (->Num 12 [[0 0] [0 1]])]
    (num->neighbour-yx m n)))

(defn pt1 [input]
  (let [matrix       (utils/input->matrix input)
        tokens       (mapcat parse-row matrix (range))
        type->tokens (group-by type tokens)
        sym-yxs      (->> (get type->tokens aoc.day_03.Sym)
                          (mapv :yx-coord)
                          set)
        num-with-sym (->> (get type->tokens aoc.day_03.Num)
                          (filter #(seq (set/intersection (num->neighbour-yx matrix %)
                                                          sym-yxs)))
                          (map :n))
        ans  (utils/sum num-with-sym)]
    ans))

(defn pt2 [input]
  (let [matrix       (utils/input->matrix input)
        tokens       (mapcat parse-row matrix (range))
        type->tokens (group-by type tokens)
        gear-yxs     (->> (get type->tokens aoc.day_03.Sym)
                          (filterv #(= "*" (:text %)))
                          (mapv :yx-coord)
                          set)
        ans          (->> gear-yxs
                          (map (fn [gyx] (->> (get type->tokens aoc.day_03.Num)
                                              (filter
                                               #(seq (set/intersection (num->neighbour-yx matrix %)
                                                                       #{gyx}))))))
                          (filter (fn [ns] (= 2 (count ns))))
                          (map #(map :n %))
                          (map #(apply * %))
                          utils/sum)
        ;; ans nums-i-like
        ;; ans gear-yxs
        ]
    ans))

(comment
  (time (pt2 input));; 87263515
  (-> (pt2 example)
      (clojure.pprint/pprint))

  (-> (pt1 example)
      (clojure.pprint/pprint))

  (time (pt1 input))

  (let [line  "467..114.."
        linet (utils/split-line line)]
    (parse-row linet 0))

  (map parse-row (map utils/split-line (str/split-lines example)) (range))

  (mapcat (fn [item row-idx] [item row-idx]) [[:a :b] [:c :d]] (range))
  ;
  )
