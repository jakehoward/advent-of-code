(ns org.jakehoward.aoc.utils
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn get-input [day]
  (-> (format "inputs/days/%s.txt" day)
      io/resource
      slurp
      clojure.string/trim))

(defn lines [txt]
  (clojure.string/split txt #"\n"))

(defn chars [lines]
  (map #(clojure.string/split % #"") lines))

(defn cols [rows]
  (apply map vector rows))

(defn range-incl [start end]
  (cond
    (= start end) (range start end)
    (< start end) (range start (inc end))
    (> start end) (reverse (range end (inc start)))))

(defn parse-int [s] (Integer/parseInt s))

(def a->z (->> (range 97 (+ 97 26))
               (map char)))

(def A->Z (map #(Character/toUpperCase %) a->z))

;; ========
;; A*
;; --
;; Copied and adapted from Joy of Clojure
;; ========

(defn min-by [f coll]
  (when (seq coll)
    (reduce (fn [min other]
              (if (> (f min) (f other))
                other
                min))
            coll)))

(defn neighbours [x-size y-size yx]
  (let [deltas [[-1 0] [1 0] [0 -1] [0 1]]]
    (->> deltas
         (map #(vec (map + yx %)))
         (filter (fn [[new-y new-x]] (and (< -1 new-y y-size) (< -1 new-x x-size)))))))

(defn joy-heuristic [step-cost-est size [y x]]
  (* step-cost-est
     (- (+ size size) y x 2)))

;; h(...) in the literature
(defn jake-heuristic [step-cost-est [end-y end-x] [y x]]
  (* step-cost-est
     (+ (Math/abs (- end-y y)) (Math/abs (- end-x x)))))

;; (joy-heuristic 1 4 [4 4])
;; (jake-heuristic 1 [4 1] [4 2])
;; (neighbours 5 5 [4 2])

;; g(...) in the literature
(defn path-cost [node-cost cheapest-nbr step-cost]
  (+ node-cost step-cost (* step-cost (count (:xys cheapest-nbr [])))
     (or (:cost cheapest-nbr) 0)))

;; f(...) in the literature
(defn total-cost [estimate-cost-fn newcost step-cost-est end-yx yx]
  (+ newcost
     (estimate-cost-fn step-cost-est end-yx yx)))

(defn a-star [start-yx end-yx step-est cell-costs get-neighbours estimate-cost-fn]
  (let [x-size (count (first cell-costs))
        y-size (count cell-costs)]
    (loop [steps 0
           routes (vec (repeat y-size (vec (repeat x-size nil))))
           work-todo (sorted-set [0 start-yx])]
      (if (empty? work-todo)
        [(get-in routes end-yx) :steps steps]
        (let [[_ yx :as work-item]  (first work-todo)
              rest-work-todo        (disj work-todo work-item)
              nbr-yxs               (get-neighbours x-size y-size yx)
              cheapest-nbr          (min-by :cost (keep #(get-in routes %) nbr-yxs))
              newcost               (path-cost (get-in cell-costs yx) cheapest-nbr step-est)
              oldcost               (:cost (get-in routes yx))
              route-entry           {:cost newcost :yxs (conj (:yxs cheapest-nbr []) yx)}]
          (cond (and oldcost (>= newcost oldcost))
                (recur (inc steps) routes rest-work-todo)

                (= end-yx yx)
                (recur (inc steps)
                       (assoc-in routes yx route-entry)
                       (sorted-set))

                :else
                (recur (inc steps)
                       (assoc-in routes yx route-entry)
                       (into rest-work-todo
                             (map (fn [w]
                                    [(total-cost estimate-cost-fn newcost step-est end-yx w) w])
                                  nbr-yxs)))))))))

;; Usage example
(comment
  (def z-world
    [[1 1 1 1 1]
     [999 999 999 999 1]
     [1 1 1 1 1]
     [1 999 999 999 999]
     [1 1 1 1 1]])

  (def asym-z-world
    [[1 1 1 1 1 1 1 1 1]
     [999 999 999 999 1 1 1 1 1]
     [1 1 1 1 1 1 1 1 1]
     [1 1 1 1 1 999 999 999 999]
     [1 1 1 1 1 1 1 1 1]])

  (def spiral-world
    [[1   1   1   1   1]
     [999 999 999 999 1]
     [1   1   1   999 1]
     [1   999 999 999 1]
     [1   1   1   1   1]])

  (defn lpad [n char s]
    (str (str/join (repeat (- n (count (str s))) char)) s))

  (defn rpad [n char s]
    (str s (str/join (repeat (- n (count (str s))) char))))

  (defn visualise-route [world route]
    (let [route-set (set route)
          max-len (apply max (mapcat (fn [row] (map (comp count str) row)) world))]
      (->> (for [y (range (count world)) x (range (count (first world)))]
             (let [str-cell (get-in world [y x])]
               (if (route-set [y x])
                 (lpad (inc max-len) " " (str "*" str-cell))
                 (lpad (inc max-len) " " (str "" str-cell)))))
           (partition (count (first world)))
           (map #(str/join " " %))
           (str/join "\n"))))

  (defn viz-a-star [start-yx end-yx step-est world get-neighbours estimate-cost]
    (let [[result _ iterations]
          (a-star start-yx end-yx step-est world get-neighbours estimate-cost)]

      (println "Iterations:" iterations)
      (println "Cost:" (:cost result))
      (println "Steps:" (count (:yxs result)))
      (println "Result:" result)
      (println
       (visualise-route world (:yxs result)))))

  (let [step-est       1
        get-neighbours neighbours
        estimate-cost  jake-heuristic
        world          spiral-world
        start-yx       [0 0]
        end-yx         [2 2]]
    (viz-a-star
     start-yx end-yx step-est world get-neighbours estimate-cost))

;;
  )

;; ======
;; A* END
;; ======
