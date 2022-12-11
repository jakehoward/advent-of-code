(ns org.jakehoward.aoc.days.day-11
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as str]))

(defrecord Monkey [id items worry-on-inspection-fn next-monkey num-inspections])

(defn op->fn [op-str]
  (let [[arg op arg2] (str/split op-str #" ")
        op-var        (resolve (symbol op))]
    (fn [old]
      (op-var
       (if (= "old" arg) old (Integer/parseInt arg))
       (if (= "old" arg2) old (Integer/parseInt arg2))))))

(defn parse-int [s] (Integer/parseInt s))

(defn parse-monkey [monkey]
  (let [lines   (utils/lines monkey)
        id      (-> (nth lines 0) (str/split #"Monkey ") second (str/split #":") first parse-int)
        items   (->> (-> (nth lines 1) (str/split #"Starting items:") second (str/split #", "))
                     (map #(str/trim %))
                     (map parse-int))
        op      (-> (nth lines 2) (str/split #"Operation: new = ") second op->fn)
        div-by  (-> (nth lines 3)
                    (str/split #"Test: divisible by ")
                    second
                    str/trim
                    parse-int)
        true-m  (-> (nth lines 4)
                    (str/split #"If true: throw to monkey ")
                    second
                    str/trim
                    parse-int)
        false-m  (-> (nth lines 5)
                     (str/split #"If false: throw to monkey ")
                     second
                     str/trim
                     parse-int)
        next-m   (fn [worry-level] (if (= 0 (mod worry-level div-by)) true-m false-m))
        num-insp 0
        ;; ans      ["id:" id "items:" items "op:" op "test op 5:" (op 5) "div by:" div-by
                  ;; "true-m:" true-m "false-m:" false-m]
        ]
    (->Monkey id items op next-m num-insp)))

((.next_monkey (parse-monkey example-input)) 47)

(defn parse-input [input]
  (let [monkey-strs (str/split input #"\n\n")
        monkeys     (map parse-monkey monkey-strs)]
    monkeys))

(defn part-1 [input]
  (let [xxx (parse-input input)
        ans xxx]
    ans))

(comment
  (part-1 example-input)
  (part-1 (utils/get-input "11"))

  (def example-input
    (-> "
Monkey 0:
  Starting items: 79, 98
  Operation: new = old * 19
  Test: divisible by 23
    If true: throw to monkey 2
    If false: throw to monkey 3

Monkey 1:
  Starting items: 54, 65, 75, 74
  Operation: new = old + 6
  Test: divisible by 19
    If true: throw to monkey 2
    If false: throw to monkey 0

Monkey 2:
  Starting items: 79, 60, 97
  Operation: new = old * old
  Test: divisible by 13
    If true: throw to monkey 1
    If false: throw to monkey 3

Monkey 3:
  Starting items: 74
  Operation: new = old + 3
  Test: divisible by 17
    If true: throw to monkey 0
    If false: throw to monkey 1"
        (str/triml)))
;;
  )
