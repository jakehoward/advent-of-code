(ns org.jakehoward.aoc.days.day-11-pt2
  (:require [org.jakehoward.aoc.utils :as utils :refer [parse-int]]
            [clojure.string :as str]
            [clojure.pprint :as pprint]))

(defrecord Monkey [id items worry-fn monkey-fn div-by])

(defn op->fn [op-str]
  (let [[raw-arg op raw-arg2] (str/split op-str #" ")
        op-var                (resolve (symbol op))
        arg                   (if (= "old" raw-arg) raw-arg (parse-int raw-arg))
        arg2                  (if (= "old" raw-arg2) raw-arg2 (parse-int raw-arg2))]
    (cond (and (= "old" arg)
               (= "old" arg2))
          (fn [old] (op-var old old))

          (= "old" arg)
          (fn [old] (op-var old arg2))

          (= "old" arg2)
          (fn [old] (op-var arg old))

          :else (throw (Exception. (str "Can't create fn for a1: " arg " a2: " arg2))))))

(defn parse-monkey [monkey]
  (let [lines   (utils/lines monkey)
        id      (-> (nth lines 0) (str/split #"Monkey ") second (str/split #":") first parse-int)
        items   (->> (-> (nth lines 1) (str/split #"Starting items:") second (str/split #", "))
                     (map #(str/trim %))
                     (map parse-int)
                     (map bigint)
                     vec)
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
        ;; ans      ["id:" id "items:" items "op:" op "test op 5:" (op 5) "div by:" div-by
                  ;; "true-m:" true-m "false-m:" false-m]
        ]
    (->Monkey id items op next-m div-by)))

(defn parse-input
  ([input]
   (let [monkey-strs (str/split input #"\n\n")
         monkeys     (map parse-monkey monkey-strs)]
     monkeys)))

(defn build-monkey-lookup [monkeys]
  (->> monkeys
       (reduce (fn [lookup monkey] (assoc lookup (:id monkey) monkey))
               {})))

(defn worry-score [all-num-insp]
  (reduce * (take 2 (reverse (sort all-num-insp)))))

(defn play-game
  "Iterate through num-rounds processing items
  returning the number of items processed by each monkey"
  [monkey-lookup num-rounds]
  (let [reduce-factor (apply * (map :div-by (vals monkey-lookup)))
        min-worry     (apply min (map :div-by (vals monkey-lookup)))
        in-items      (transient
                       (into {} (map (fn [[id m]] [id (:items m)]) (sort monkey-lookup))))
        in-num-insp   (transient
                       (into {} (map (fn [[id _]] [id 0]) (sort monkey-lookup))))
        num-monkeys   (count (keys monkey-lookup))]
    (loop [rem-rounds  num-rounds
           items       in-items
           num-insp    in-num-insp]
      (when (= 0 (mod rem-rounds 250))
        (println "Remaining rounds:" rem-rounds))
      (if (> rem-rounds 0)
        (let [[updated-items updated-num-insp]
              (loop [monkey-id        0
                     updated-items    items
                     updated-num-insp num-insp]
                (if (< monkey-id num-monkeys)
                  ;; logic goes here...(transducers to speed it up?)
                  (let [monkey            (monkey-lookup monkey-id)
                        all-items         (updated-items monkey-id)
                        worry-to-monkey   (map
                                           (fn [item]
                                             (let [worry  (apply (:worry-fn monkey)  [item])
                                                   ;; worry  (if (> (- worry reduce-factor)
                                                                 ;; min-worry)
                                                            ;; (- worry reduce-factor)
                                                   ;; worry)
                                                   worry (->> (iterate #(- % reduce-factor)
                                                                       worry)
                                                              (drop-while #(> % min-worry))
                                                              first
                                                              (+ reduce-factor))
                                                   next-m (apply (:monkey-fn monkey) [worry])]
                                               [worry next-m]))
                                           all-items)
                        u-updated-items    (reduce (fn [ui [w m]]
                                                     (assoc! ui m (conj (ui m) w)))
                                                   updated-items
                                                   worry-to-monkey)
                        u-updated-items    (assoc! u-updated-items monkey-id [])
                        u-updated-num-insp (assoc! updated-num-insp monkey-id
                                                   (+ (count all-items)
                                                      (updated-num-insp monkey-id)))]
                    (recur (inc monkey-id) u-updated-items u-updated-num-insp))
                  [updated-items updated-num-insp]))]
          (recur (dec rem-rounds) updated-items updated-num-insp))

        (do
          (persistent! num-insp)
          ;; items
          )))))

(defn part-2 [input]
  (let [monkeys        (parse-input input)
        monkey-lookup  (build-monkey-lookup monkeys)
        all-num-insp   (play-game monkey-lookup 10000)
        score          (worry-score (vals all-num-insp))
        ]
    all-num-insp
    score
    ))

(comment
  ;; ans: 2713310158
  ;; ans-650: 11373744
  (time (part-2 example-input)) ;; => 2713310158
  (time (part-2 (utils/get-input "11")))

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
