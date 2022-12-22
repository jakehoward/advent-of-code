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

(defn get-num-2 [monkey-lookup monkey-name]
  (let [monkey (get monkey-lookup monkey-name)
        number (:num monkey)]
    (cond
      (and
       (nil? number)
       (nil? (:op monkey)))     nil
      number                    number
      :else                     (let [a (get-num-2 monkey-lookup (:arg1 monkey))
                                      b (get-num-2 monkey-lookup (:arg2 monkey))]
                                  (if (or (nil? a) (nil? b))
                                    nil
                                    ((:op monkey) a b))))))

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

(defn part-2-analysis [input]
  (let [monkeys       (parse-input input)
        monkey-lookup (-> (build-lookup monkeys :name)
                          (assoc "humn" {:name "humn" :num nil}))
        root          (get monkey-lookup "root")
        arg1          (:arg1 root)
        arg2          (:arg2 root)
        _             (println "arg1" arg1 "arg2" arg2)
        arg1-val      (get-num-2 monkey-lookup arg1)
        arg2-val      (get-num-2 monkey-lookup arg2)
        root-num-chk  (get-num-2 (assoc monkey-lookup "humn" {:name "humn" :num 1186}) "root")]
    (println "root-sanity-check:" root-num-chk)
    (println "arg1:" arg1 "val:" arg1-val)
    (println "arg2:" arg2 "val:" arg2-val)
    (println "Whichever of arg1 or arg2 has nil value will need its equation solved.")
    (println "(+ x (* y humn) ... ) = non-nil arg value")
    [root-num-chk arg1-val arg2-val]))

(defn build-equation [monkey-lookup eqn-arg variable]
  (let [monkey  (get monkey-lookup eqn-arg)
        number  (or (:num monkey) (get-num-2 monkey-lookup (:name monkey)))]
    (cond
      (= eqn-arg variable) eqn-arg
      number               number
      :else                {:op (:op monkey)
                            :arg1 (build-equation monkey-lookup (:arg1 monkey) variable)
                            :arg2 (build-equation monkey-lookup (:arg2 monkey) variable)})))

(defn build-equation-2 [monkey-lookup eqn-arg variable]
  (let [monkey  (get monkey-lookup eqn-arg)
        number  (or (:num monkey) (get-num-2 monkey-lookup (:name monkey)))]
    (cond
      (= eqn-arg variable) eqn-arg
      (number? eqn-arg)    eqn-arg
      number               number
      :else                {:op (:op monkey)
                            :arg1 (build-equation monkey-lookup (:arg1 monkey) variable)
                            :arg2 (build-equation monkey-lookup (:arg2 monkey) variable)})))

(def counter-operations {(resolve '+) (resolve '-)
                         (resolve '-) (resolve '+)
                         (resolve '/) (resolve '*)
                         (resolve '*) (resolve '/)})

(defn solve-eqn [eqn known-val variable]
  (let [arg1 (:arg1 eqn)
        arg2 (:arg2 eqn)
        c-op (get counter-operations (:op eqn))]
    (do (println "\n" "Eqn:" eqn "arg1:" arg1 "arg2:" arg2 "known-val:" known-val))
    (cond
      ;; what if both numbers? => shouldn't happen in this problem...
      (= variable
         arg1)       (do (println "eqn:" eqn "known" known-val) (c-op known-val arg2))
      (= variable
         arg2)       (do (println "eqn:" eqn) (c-op known-val arg1))
      (number? arg1) (solve-eqn (:arg2 eqn) (c-op known-val arg1) variable)
      (number? arg2) (solve-eqn (:arg1 eqn) (c-op known-val arg2) variable)
      :else
      (throw (Exception. (str "Hmm...solving for eqn: "
                              eqn
                              " arg1: "
                              (if (nil? arg1) "nil" arg1)
                              " arg2: "
                              (if (nil? arg2) "nil" arg2)
                              " failed"))))))

(defn part-2 [input]
  (let [monkeys       (parse-input input)
        monkey-lookup (-> (build-lookup monkeys :name)
                          (assoc "humn" {:name "humn" :num nil}))
        root          (get monkey-lookup "root")
        arg1          (:arg1 root)
        arg2          (:arg2 root)
        arg1-val      (get-num-2 monkey-lookup arg1)
        arg2-val      (get-num-2 monkey-lookup arg2)
        eqn-arg       (if (nil? arg1-val) arg1 arg2)
        eqn           (build-equation-2 monkey-lookup eqn-arg "humn")
        known-val     (if (nil? arg1-val) arg2-val arg1-val)
        ans           (solve-eqn eqn known-val "humn")]
    ans))

(comment
  (part-2 example-input) ;; => 301
  ;; your answer is too high
  (time (part-2 (utils/get-input "21"))) ;; => 1886675955731489/324

  (/ 107810054617577N 9)

  (part-2-analysis example-input)
  (time (part-2-analysis (utils/get-input "21")))

  (part-1 example-input) ;; => 152
  (time (part-1 (utils/get-input "21"))) ;; => 194058098264286
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
