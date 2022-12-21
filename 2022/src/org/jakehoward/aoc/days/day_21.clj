(ns org.jakehoward.aoc.days.day-21
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as str]))

(defn parse-line [line]
  (let [tokens                (str/split line #"\s+")
        monkey                (-> (first tokens)
                                  (str/split #":")
                                  first)
        [_ arg-or-num op arg] tokens]
    (if op
      {:name monkey :arg1 arg-or-num :op (resolve (symbol op)) :arg2 arg}
      {:name monkey :num (Integer/parseInt arg-or-num)})))

(defn parse-input [input]
  (->> (utils/lines input)
       (map parse-line)))

(defn get-num [monkey-lookup monkey-name]
  (let [monkey (get monkey-lookup monkey-name)
        number (:num monkey)]
    (if number
      number
      ((:op monkey)
       (get-num monkey-lookup (:arg1 monkey))
       (get-num monkey-lookup (:arg2 monkey))))))

(defn build-lookup [items lookup-key]
  (loop [xs items
         lu {}]
    (if-let [x (first xs)]
      (recur (rest xs) (assoc lu (get x lookup-key) x))
      lu)))

(defn part-1 [input]
  (let [monkeys       (parse-input input)
        monkey-lookup (build-lookup monkeys :name)
        root          (get-num monkey-lookup "root")]
    root))

(comment
  (part-1 example-input)
  (part-1 (utils/get-input "21")) ;; => 194058098264286
  (def example-input (->
                      "
root: pppw + sjmn
dbpl: 5
cczh: sllz + lgvd
zczc: 2
ptdq: humn - dvpt
dvpt: 3
lfqf: 4
humn: 5
ljgn: 2
sjmn: drzm * dbpl
sllz: 4
pppw: cczh / lfqf
lgvd: ljgn * ptdq
drzm: hmdt - zczc
hmdt: 32"
                      (str/trim)))
  ;;
  )
