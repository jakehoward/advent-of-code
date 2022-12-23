(ns org.jakehoward.aoc.days.day-20-pt2
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as str]))

(defn parse-input [input]
  (map #(Integer/parseInt %) (utils/lines input)))

(defn faster-unmix-one [current-states curr-idx]
  (let [len         (count current-states)
        [n pos]     (get current-states curr-idx)
        ;; abs-n       (Math/abs n) ;; doesn't work...so weird...JVM/Clojure bug
        abs-n       (if (neg? n) (* -1 n) n)
        ;;          disregard loops that net out to zero
        ;;          0 1 2 3
        ;;          a b c d (b => 4)
        ;;          a c b d (1)
        ;;          a c d b (2)
        ;;          a b c d (3)
        ;;          a c b d (4)
        ;; _           (print "n:" n "abs-n:" abs-n "er...:" (Math/abs n) "post-n xx:" nil)
        ;; _           (if (not= 0 n)
                      ;; (println " (/ n abs-n):" (/ n abs-n) "(rem abs-n):" (rem abs-n (dec len)))
        ;; (println "\n"))
        post-n      (if (>= abs-n (dec len)) (* (/ n abs-n) (rem abs-n (dec len))) n)
        n           post-n
        new-pos     (+ pos n)
        ;;          handle wrapping around the list
        it-wraps-p  (>= new-pos len)
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

(comment
  (Math/abs -2434767459)
  (Math/abs 2434767459)
  (do
    (println "\n\n")
    (time (part-2 example-input)))
  ;;
  )

(defn check [current-states]
  (let [unique-positions
        (= (count (set (map second (map second current-states))))
           (count (map second (map second current-states))))]
    (when (not unique-positions)
      (throw (Exception. "Positions are not unique")))))

(defn unmix-2 [initial-current-states]
  (let [num-nums (count initial-current-states)]
    (loop [current-states initial-current-states
           curr-idx       0]

      (comment (println "\n" (map second (sort-by second (vals current-states)))))
      (comment (println (str/join ", " (map first (sort-by second (vals current-states))))))
      (do (check current-states))

      (if (< curr-idx num-nums)
        (recur (faster-unmix-one current-states curr-idx) (inc curr-idx))
        current-states))))

(defn solve [nums]
  (let [n1 (nth nums (mod (+ 1000 (.indexOf nums 0)) (count nums)))
        n2 (nth nums (mod (+ 2000 (.indexOf nums 0)) (count nums)))
        n3 (nth nums (mod (+ 3000 (.indexOf nums 0)) (count nums)))]
    (comment (println "n1:" n1 "n2:" n2 "n3:" n3))
    (+ n1 n2 n3)))

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

        num-rnds      10
        ;; num-rnds      1
        unmixed       (loop [state initial rnd 0]

                        (comment ;; example input only!
                          (println)
                          (println "state:" (map first (sort-by second (vals state))))
                          (println "idxs:"  (map second (sort-by second (vals state))))
                          (println "rnd:" rnd))

                        (if (< rnd num-rnds)
                          (recur (unmix-2 state) (inc rnd))
                          state))
        ans          (solve (map first (sort-by second (vals unmixed))))]
    ans))

(comment
  (println "\n\n")
  (time (part-2 example-input));; => 1623178306 (3.4ms)
  (time (part-2 (utils/get-input "20")));; => 8332585833851 (257 seconds)

  (time (part-1-v2 example-input))

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
