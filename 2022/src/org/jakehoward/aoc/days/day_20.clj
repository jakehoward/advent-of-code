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

(defn faster-unmix-one [current-states curr-idx]
  (let [len         (count current-states)
        [n pos]     (get current-states curr-idx)
        abs-n       (Math/abs n)
        ;;          disregard loops that net out to zero movement
        n           (if (> abs-n len) (do (println "foo") (* (/ n abs-n) (mod abs-n (dec len)))) n)
        new-pos     (+ pos n)
        ;;          handle wrapping around the list
        it-wraps-p  (> new-pos len)
        new-pos     (if it-wraps-p (rem new-pos (dec len)) new-pos)
        it-wraps-n  (< new-pos 0)
        new-pos     (if it-wraps-n (+ (dec len) new-pos) new-pos)]
    (-> (reduce (fn [cs [idx [an apos]]]
                  (cond (= curr-idx idx)
                        (assoc! cs idx [an new-pos])

                        (and (pos? n) it-wraps-p)
                        (assoc! cs idx [an (if (and (< apos pos) (>= apos new-pos))
                                             (inc apos)
                                             apos)])

                        (pos? n)
                        (assoc! cs idx [an (if (and (> apos pos) (<= apos new-pos))
                                             (dec apos)
                                             apos)])

                        (and (neg? n) it-wraps-n)
                        (assoc! cs idx [an (if (and (<= apos new-pos) (> apos pos))
                                             (dec apos)
                                             apos)])

                        (neg? n)
                        (assoc! cs idx [an (if (and (>= apos new-pos) (< apos pos))
                                             (inc apos)
                                             apos)])
                        (= 0 n)
                        (assoc! cs idx [an apos])

                        :else
                        (throw (Exception. "wtf - pos? neg? 0 is surely complete?"))))
                (transient current-states)
                current-states)
        persistent!)))

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
        ;; (recur (unmix-one current-states curr-idx) (inc curr-idx))
        (recur (faster-unmix-one current-states curr-idx) (inc curr-idx))
        (map first (sort-by second (vals current-states)))))))

(defn unmix-2 [initial-current-states]
  (let [num-nums (count initial-current-states)]
    (loop [current-states initial-current-states
           curr-idx       0]

      (comment (println (str/join ", " (map first (sort-by second (vals current-states))))))
      (comment (check current-states))

      (if (< curr-idx num-nums)
        (recur (faster-unmix-one current-states curr-idx) (inc curr-idx))
        current-states))))

(defn solve [nums]
  (let [n1 (nth nums (mod (+ 1000 (.indexOf nums 0)) (count nums)))
        n2 (nth nums (mod (+ 2000 (.indexOf nums 0)) (count nums)))
        n3 (nth nums (mod (+ 3000 (.indexOf nums 0)) (count nums)))]
    (println "n1:" n1 "n2:" n2 "n3:" n3)
    (+ n1 n2 n3)))

(defn part-1 [input]
  (let [nums      (parse-input input)
        unmixed   (unmix nums)
        ans       (solve unmixed)]
    ans))

(defn part-1-v2 [input]
  (let [nums      (parse-input input)
        initial   (into {} (map-indexed (fn [idx n] [idx [n idx]]) nums))
        unmixed   (unmix-2 initial)
        um-nums   (map first (sort-by second (vals unmixed)))
        ans       (solve um-nums)]
    ans))

(defn part-2 [input]
  (let [nums         (parse-input input)
        dkey         811589153
        ;; dkey         1
        actual-nums  (map #(* dkey %) nums)
        initial      (into {} (map-indexed (fn [idx n] [idx [n idx]]) actual-nums))

        ;; todo:
        ;; unmixed-1     (first (iterate unmix-2 initial))
        ;; unmixed-2     (unmix-2 initial)
        ;; _             (println "eq:" (= unmixed-1 unmixed-2))

        num-rnds      10
        unmixed       (loop [state initial rnd 0]
                        (println "state:" (map first (sort-by second (vals state)))
                                 "rnd:" rnd)
                        (if (< rnd num-rnds)
                          (recur (unmix-2 state) (inc rnd))
                          state))
        ;; unmixed       (first (take num-rnds (iterate unmix-2 initial)))
        ;; _            (println "um:" unmixed (map first (sort-by second (vals unmixed))))
        ans          (solve (map first (sort-by second (vals unmixed))))]
    ans))

(comment
  (println "\n\n")
  (time (part-2 example-input))
  (time (part-2 (utils/get-input "20")))

  (time (part-1 example-input)) ;; => 3
  (time (part-1-v2 example-input))

  ;; => 5904 "Elapsed time: 12,299.403868 msecs"
  ;;         "Elapsed time: 4159.379517 msecs" with transients
  (time (part-1 (utils/get-input "20")))

  ;; => 1623178306  ("Elapsed time: 7.149748 msecs")
  ;; => -1623178306 ???
  (time (part-2 example-input))

  ;; => -982834464283 (wrong) ("Elapsed time: 153,222.828098 msecs")
  ;; => same                  "Elapsed time:   73,825.334121 msecs" with transients
  ;; => used +ve 982834464283 => too low
  (time (part-2 (utils/get-input "20")));; => 11928


  ;; debug
  (time (part-1-v2 (utils/get-input "20")))

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
