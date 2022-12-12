(ns org.jakehoward.aoc.days.day-12
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as str]))

(defn parse-input [input]
  (let [letter-grid   (->> (utils/lines input)
                           (map #(str/split % #"")))]
    (vec letter-grid)))

(defn get-letter-yx [letter-grid letter]
  ;; todo: get
  (let [start-yx (->> letter-grid
                      (map-indexed (fn [y row] [y (.indexOf row letter)]))
                      (filter #(> (second %) -1))
                      first)]
    start-yx))

(defn get-start-yx [letter-grid]
  (get-letter-yx letter-grid "S"))

(defn get-end-yx [letter-grid]
  (get-letter-yx letter-grid "E"))

(defn cell->num [cell]
  (cond (= "S" cell) 0
        (= "E" cell) 25
        :else (.indexOf utils/a->z (first (char-array cell)))))

(defn build-number-grid [letter-grid]
  (->> letter-grid
       (mapv (fn [row] (mapv cell->num row)))))

;; h(...) in the literature
;; estimate cost from point to end
(defn day12-heuristic [step-est [end-y end-x] [y x]]
  (* step-est
     (+ (Math/abs (- end-y y)) (Math/abs (- end-x x)))))

(defn make-get-neighbours [number-grid]
  (fn [x-size y-size yx]
    (let [deltas [[-1 0] [1 0] [0 -1] [0 1]]
          curr   (get-in number-grid yx)]
      (->> deltas
           (map #(vec (map + yx %)))
           (filter (fn [[new-y new-x]]
                     (and
                      (< -1 new-y y-size)
                      (< -1 new-x x-size)
                      (< (- (get-in number-grid [new-y new-x]) curr) 2))))))))

(defn make-get-reverse-neighbours [number-grid]
  (fn [x-size y-size yx]
    (let [deltas [[-1 0] [1 0] [0 -1] [0 1]]
          curr   (get-in number-grid yx)]
      (->> deltas
           (map #(vec (map + yx %)))
           (filter (fn [[new-y new-x]]
                     (and
                      (< -1 new-y y-size)
                      (< -1 new-x x-size)
                      (< (- curr (get-in number-grid [new-y new-x])) 2))))))))

(defn build-cell-costs [grid]
  (let [y-size (count grid)
        x-size (count (first grid))]
    (vec (repeat y-size (vec (repeat x-size 1))))))

(defn part-1 [input]
  (let [letter-grid     (parse-input input)
        start-yx        (get-start-yx letter-grid)
        end-yx          (get-end-yx letter-grid)
        number-grid     (build-number-grid letter-grid)
        get-neighbours  (make-get-neighbours number-grid)
        get-rev-nbr     (make-get-reverse-neighbours number-grid)
        step-cost       100
        cell-costs      (build-cell-costs number-grid)
        [result _ its]  (utils/a-star-d12 start-yx
                                          end-yx
                                          step-cost
                                          cell-costs
                                          get-neighbours
                                          get-rev-nbr
                                          day12-heuristic)
        _               (println "s-yx:" start-yx "e-yx:" end-yx "its:" its)]
    (dec (count (:yxs result)))
    ;; (:yxs result)
    ))

(def example-input
  (->
   "
Sabqponm
abcryxxl
accszExk
acctuvwj
abdefghi"
   (str/trim)))

(comment
  (time (part-1 example-input))
  ;; 123 (too low) ;; => 440
  (time (part-1 (utils/get-input "12")))

  (let [letter-grid     (parse-input (utils/get-input 12))
        start-yx        (get-start-yx letter-grid)
        end-yx          (get-end-yx letter-grid)
        number-grid     (build-number-grid letter-grid)
        get-neighbours  (make-get-neighbours number-grid)]
    (get-neighbours (count (first number-grid)) (count number-grid) start-yx))

  (let [grid   [[9 0 9]
                [2 0 1]
                [9 -3 9]]
        get-n  (make-get-neighbours grid)
        ans    (get-n 3 3 [1 1])]
    ans)
  ;;
  )

