(ns org.jakehoward.aoc.days.day-13
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as str]))

(defn parse-input [input]
  (->> (str/split input #"\n\n")
       (map #(str/split % #"\n"))
       (mapv (fn [chunk] (mapv clojure.edn/read-string chunk)))))

(defn is-correct-int? [l r]
  (cond (= l r) nil
        (< l r) true
        (> l r) false))

(defn is-correct? [l r]
  (try
    (comment (println "l:" l "r:" r))
    (cond (and (sequential? l) (sequential? r))
          (cond (and (empty? l) (empty? r)) nil
                (and (empty? l) (seq r))    true
                (and (empty? r) (seq l))    false
                :else                       (if-some [ans (is-correct? (first l) (first r))]
                                              ans
                                              (is-correct? (rest l) (rest r))))

          (and (sequential? l) (int? r))
          (if-some [ans (is-correct? l [r])]
            ans
            ;; (is-correct? (rest l) (rest r))
            false
            )

          (and (sequential? r) (int? l))
          (if-some [ans (is-correct? [l] r)]
            ans
            ;; (is-correct? (rest l) (rest r))
            false
            )

          (and (int? l) (int? r))
          (is-correct-int? l r)

          :else (throw (Exception. (str "Missing case, l: " l " r: " r))))
    (catch Exception e (println "l:" l "r:" r (.getMessage e)))))

(defn is-correct-comp [l r]
  (if (is-correct? l r) -1 1))

(comment
  (is-correct? [1] [2])

  (is-correct? [1,1,3,1,1]
               [1,1,5,1,1])

  (is-correct? [] [])

  (is-correct? [[1],[2,3,4]]
               [[1],4])

  (is-correct? [9]
             [[8,7,6]])

  (is-correct? [[4,4],4,4]
             [[4,4],4,4,4])

  (is-correct? [7,7,7,7]
               [7,7,7])

  (is-correct? []
             [3])

  (is-correct? [[[]]]
             [[]])

  (is-correct? [1,[2,[3,[4,[5,6,7]]]],8,9]
               [1,[2,[3,[4,[5,6,0]]]],8,9])
  ;; 
  )

(defn get-correct-packet-idxs [all-packet-pairs]
  (loop [idx            1
         correct-idxs   []
         packet-pairs   all-packet-pairs]
    (if-let [[left right] (first packet-pairs)]
      (if (is-correct? left right)
        (recur (inc idx) (conj correct-idxs idx) (rest packet-pairs))
        (recur (inc idx) correct-idxs (rest packet-pairs)))
      correct-idxs)))

(defn part-1 [input]
  (let [packet-pairs  (parse-input input)
        correct-idxs  (get-correct-packet-idxs packet-pairs)]
    (reduce + correct-idxs)))

(defn part-2 [input]
  (let [packet-pairs   (parse-input input)
        divider-1      [[2]]
        divider-2      [[6]]
        all-packets    (concat [divider-1 divider-2] (reduce concat packet-pairs))
        sorted-packets (sort is-correct-comp all-packets)]

    (*
     (inc (.indexOf sorted-packets divider-1))
     (inc (.indexOf sorted-packets divider-2)))))

(comment
  (part-1 example-input)
  (part-1 (utils/get-input "13")) ;; => 5684

  (part-2 example-input)
  (part-2 (utils/get-input "13")) ;; => 22932

  (def example-input (->
                      "
[1,1,3,1,1]
[1,1,5,1,1]

[[1],[2,3,4]]
[[1],4]

[9]
[[8,7,6]]

[[4,4],4,4]
[[4,4],4,4,4]

[7,7,7,7]
[7,7,7]

[]
[3]

[[[]]]
[[]]

[1,[2,[3,[4,[5,6,7]]]],8,9]
[1,[2,[3,[4,[5,6,0]]]],8,9]"
                      (str/trim)))
  ;;
  )
