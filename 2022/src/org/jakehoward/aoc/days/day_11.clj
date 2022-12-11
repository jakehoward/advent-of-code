(ns org.jakehoward.aoc.days.day-11
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as str]))

(defprotocol MonkeyAround
  (items-report [this])
  (process-item [this])
  (add-item [this item]))

(defrecord Monkey [id items worry-on-inspection-fn next-monkey-fn num-inspections worry-div-by]
  MonkeyAround
  (items-report [this] (str "Monkey " id ": " (str/join ", " items)))
  (add-item [this item] (update this :items #(conj (vec %) item)))
  (process-item [this]
    (if-let [item (first items)]
      (let [worry-score (bigint (/ (worry-on-inspection-fn item) worry-div-by))
            next-monkey (next-monkey-fn worry-score)]
        {:updated-monkey (-> this
                             (update :items #(-> % rest vec))
                             (update :num-inspections inc))
         :item-to {:id next-monkey :worry-score worry-score}})
      {:updated-monkey this :item-to nil})))

(defn op->fn [op-str]
  (let [[arg op arg2] (str/split op-str #" ")
        op-var        (resolve (symbol op))]
    (fn [old]
      (op-var
       (if (= "old" arg) old (Integer/parseInt arg))
       (if (= "old" arg2) old (Integer/parseInt arg2))))))

(defn parse-int [s] (Integer/parseInt s))

(defn parse-monkey [worry-div-by monkey]
  (let [lines   (utils/lines monkey)
        id      (-> (nth lines 0) (str/split #"Monkey ") second (str/split #":") first parse-int)
        items   (->> (-> (nth lines 1) (str/split #"Starting items:") second (str/split #", "))
                     (map #(str/trim %))
                     (map parse-int)
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
        num-insp 0
        ;; ans      ["id:" id "items:" items "op:" op "test op 5:" (op 5) "div by:" div-by
                  ;; "true-m:" true-m "false-m:" false-m]
        ]
    (->Monkey id items op next-m num-insp worry-div-by)))

(defn parse-input
  ([input] (parse-input input 3))
  ([input worry-div-by]
   (let [monkey-strs (str/split input #"\n\n")
         monkeys     (map (partial parse-monkey worry-div-by) monkey-strs)]
     monkeys)))

(defn- process-all-items [initial-monkey initial-monkey-lookup]
  (loop [monkey        initial-monkey
         monkey-lookup initial-monkey-lookup]
    (if-let [item (first (:items monkey))]

      (let [result        (.process-item monkey)
            to-monkey-id  (:id (:item-to result))
            updated-item  (:worry-score (:item-to result))]
        (recur
         (:updated-monkey result)
         (-> monkey-lookup
             (assoc (:id monkey) (:updated-monkey result))
             (assoc to-monkey-id (.add-item (monkey-lookup to-monkey-id) updated-item)))))
      monkey-lookup)))

(defn play-game [initial-monkey-lookup num-rounds]
  (loop [remaining-rounds    num-rounds
         monkey-lookup       initial-monkey-lookup
         monkeys-to-process  (sort (keys initial-monkey-lookup))]
    (if (> remaining-rounds 0)
      (if-let [monkey-id (first monkeys-to-process)]
        (let [updated-monkey-lookup (process-all-items (monkey-lookup monkey-id) monkey-lookup)]
          (recur remaining-rounds updated-monkey-lookup (rest monkeys-to-process)))
        ;; set the monkey list back to original and go for next round
        (recur (dec remaining-rounds) monkey-lookup (sort (keys initial-monkey-lookup))))
      monkey-lookup)))

(defn build-monkey-lookup [monkeys]
  (->> monkeys
       (reduce (fn [lookup monkey] (assoc lookup (:id monkey) monkey))
               {})))

(defn part-1 [input]
  (let [monkeys        (parse-input input)
        monkey-lookup  (build-monkey-lookup monkeys)
        monkeys-after  (play-game monkey-lookup 20)
        num-insp       (map :num-inspections (vals monkeys-after))]
    monkeys-after
    (reduce * (take 2 (reverse (sort num-insp))))))

(defn part-2 [input]
  (let [monkeys        (parse-input input 1)
        monkey-lookup  (build-monkey-lookup monkeys)
        monkeys-after  (play-game monkey-lookup 10)
        num-insp       (map :num-inspections (vals monkeys-after))
        ]
    (reduce * (take 2 (reverse (sort num-insp))))
    ))

(defn print-items-report [monkeys]
  (do (println "\n\n")
      (doseq [report-str (->> monkeys (sort-by :id) (map items-report))]
        (println report-str))))

(comment
  (part-1 example-input)
  (part-1 (utils/get-input "11")) ;; => 54752

  (time (part-2 example-input))
  (time (part-2 (utils/get-input "11")))

  (let [monkeys       (parse-input example-input)
        monkey-lookup (build-monkey-lookup monkeys)
        first-monkey  (monkey-lookup 0)
        u-lookup      (process-all-items first-monkey monkey-lookup)
        u-lookup      (process-all-items (monkey-lookup 1) u-lookup)
        u-lookup      (process-all-items (monkey-lookup 2) u-lookup)
        u-lookup      (process-all-items (monkey-lookup 3) u-lookup)]
    (print-items-report (vals u-lookup)))

  ;; =====
  ;; =====
  ;; =====
  (print-items-report (parse-input example-input))

  (-> (parse-input example-input)
      (nth 0)
      process-item
      ;; :updated-monkey
      ;; process-item
      )
  (let [monkeys       (parse-input example-input)
        monkey-lookup (build-monkey-lookup monkeys)
        monkey        (monkey-lookup 2)
        u-lookup      (process-all-items monkey monkey-lookup)]
    (-> monkey
        .process-item))

  (let [m2 (parse-monkey (-> (str/split example-input #"\n\n")
                             (nth 2)))]
    ((:next-monkey-fn m2) 2080)
    (.process-item m2)
    )

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
