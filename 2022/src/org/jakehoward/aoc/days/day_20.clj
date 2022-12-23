(ns org.jakehoward.aoc.days.day-20
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as str]))

(defn parse-input [input]
  (map #(Integer/parseInt %) (utils/lines input)))


(defn unmix-one [current-states curr-idx]
  ;; find out where the current index currently lives
  ;; move it by that many...move effected items
  ;; 0 1 2 3
  ;; a b c d  (=> c + 2)
  ;; a b d c (or) c a b d
  ;; a c b d

  ;; 0 1 2 3
  ;; a b c d  (=> b - 2)
  ;; b a c d (or) a c d b
  ;; a c b d

  ;; 0 1 2 3
  ;; a b c d  (=> c - 2)
  ;; a c b d
  ;; c a b d
  (let [len         (count current-states)
        [n pos]     (get current-states curr-idx)
        abs-n       (Math/abs n)
        ;;          disregard loops that net out to zero movement
        n           (if (> abs-n len) (* (/ n abs-n) (mod abs-n (dec len))) n)
        new-pos     (+ pos n)
        ;;          handle wrapping around the list
        it-wraps-p  (> new-pos len)
        new-pos     (if it-wraps-p (rem new-pos (dec len)) new-pos)
        it-wraps-n  (< new-pos 0)
        new-pos     (if it-wraps-n (+ (dec len) new-pos) new-pos)]
    (->> current-states
         (map (fn [[idx [an apos]]]
                (cond (= curr-idx idx)
                      [idx [an new-pos]]

                      (and (pos? n) it-wraps-p)
                      [idx [an (if (and (< apos pos) (>= apos new-pos))
                                 (inc apos)
                                 apos)]]

                      (pos? n)
                      [idx [an (if (and (> apos pos) (<= apos new-pos))
                                 (dec apos)
                                 apos)]]

                      (and (neg? n) it-wraps-n)
                      [idx [an (if (and (<= apos new-pos) (> apos pos))
                                 (dec apos)
                                 apos)]]

                      (neg? n)
                      [idx [an (if (and (>= apos new-pos) (< apos pos))
                                 (inc apos)
                                 apos)]]

                      (= 0 n)
                      [idx [an apos]]

                      :else
                      (throw (Exception. "wtf - pos? neg? 0 is surely complete?")))))
         (into {}))))

(defn check [current-states]
  (let [unique-positions
        (= (count (set (map second (map second current-states))))
           (count (map second (map second current-states))))]
    (when (not unique-positions)
      (throw (Exception. "Positions are not unique")))))

(defn unmix [nums]
  (let [num-nums (count nums)]
    (loop [current-states (into {} (map-indexed (fn [idx n] [idx [n idx]]) nums))
           curr-idx       0]

      (comment
        (println (str/join ", " (map first (sort-by second (vals current-states)))))
        (check current-states))

      (if (< curr-idx num-nums)
        (recur (unmix-one current-states curr-idx) (inc curr-idx))
        (map first (sort-by second (vals current-states)))))))

(defn unmix-2 [initial-current-states]
  (let [num-nums (count initial-current-states)]
    (loop [current-states initial-current-states
           curr-idx       0]

      (comment
        (println (str/join ", " (map first (sort-by second (vals current-states)))))
        (check current-states))

      (if (< curr-idx num-nums)
        (recur (unmix-one current-states curr-idx) (inc curr-idx))
        (into {} current-states)))))

(defn solve [nums]
  (+
   (nth nums (mod (+ 1000 (.indexOf nums 0)) (count nums)))
   (nth nums (mod (+ 2000 (.indexOf nums 0)) (count nums)))
   (nth nums (mod (+ 3000 (.indexOf nums 0)) (count nums)))
   ))

(defn part-1 [input]
  (let [nums      (parse-input input)
        unmixed (unmix nums)
        ;; _        (println "um:" unmixed)
        ans       (solve unmixed)]
    ans))

(defn part-2 [input]
  (let [nums         (parse-input input)
        dkey         811589153
        actual-nums  (map #(* dkey %) nums)
        initial      (into {} (map-indexed (fn [idx n] [idx [n idx]]) nums))
        all-unmixed  (iterate unmix-2 initial)
        unmixed      (take 1 (drop 9 all-unmixed))
        _            (println "um:" unmixed (map first (sort-by second (vals unmixed))))
        ans          (solve (map first (sort-by second (vals unmixed))))]
    ans))

(comment
  (println "\n\n")
  (time (part-1 example-input))
  (time (part-1 (utils/get-input "20"))) ;; => 5904

  (time (part-2 example-input))
  (time (part-2 (utils/get-input "20")))

  (let [
        ;; nums '(1, 2, -3, 4, 0, 3, -2)
        nums '(-2, 1, 2, -3, 4, 0, 3)
        ]
    [(+
      (nth nums (mod (+ 1000 (.indexOf nums 0)) (count nums)))
      (nth nums (mod (+ 2000 (.indexOf nums 0)) (count nums)))
      (nth nums (mod (+ 3000 (.indexOf nums 0)) (count nums))))
     ])

  (def example-input (->
                      "
1
2
-3
3
-2
0
4"
                      (str/trim)))
  ;;
  )
