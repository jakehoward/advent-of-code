(ns aoc.day-17
  (:require [aoc.utils :as u]
            [aoc.vis :as vis]
            [clojure.core.async :as async]
            [flatland.ordered.set :refer [ordered-set]]
            [clojure.string :as str]))

(def small-example (str/trim "
2413
9215
3215
3214
4322"))

(def example (str/trim "
2413432311323
3215453535623
3255245654254
3446585845452
4546657867536
1438598798454
4457876987766
3637877979653
4654967986887
4564679986453
1224686865563
2546548887735
4322674655533"))

(def example-path (str/trim "
2>>34^>>>1323
32v>>>35v5623
32552456v>>54
3446585845v52
4546657867v>6
14385987984v4
44578769877v6
36378779796v>
465496798688v
456467998645v
12246868655<v
25465488877v5
43226746555v>"))

(def input (u/get-input 17))

(defn parse-input [input]
  (mapv #(mapv u/parse-int %) (u/input->grid input)))

(comment
  (let [ex-path  (u/input->grid example-path)
        ex-input (parse-input example)]
    (->> (for [y (range (u/y-size ex-input))
               x (range (u/x-size ex-input))
               :when (#{">" "^" "<" "v"} (get-in ex-path [y x]))]
           (get-in ex-input [y x]))
         (u/sum))))

(comment
  "
2>>34^>>>1323
32v>>>35v5623
32552456v>>54
3446585845v52
4546657867v>6
14385987984v4
44578769877v6
36378779796v>
465496798688v
456467998645v
12246868655<v
25465488877v5
43226746555v>")

(defn min-by [f coll]
  (when (seq coll)
    (reduce (fn [min other]
              (if (> (f min) (f other))
                other
                min))
            coll)))

(defn get-run-length [path]
  (let [reversed-path (reverse path)
        run-length    (->> reversed-path
                           (take-while #(or (= (first  %) (first  (last path)))
                                            (= (second %) (second (last path)))))
                           count)]
    (if (= nil (nth reversed-path run-length nil))
      run-length
      (dec run-length))))

(comment
  (->> [[[0 0]]
        [[0 0] [0 1]]
        [[0 0] [0 1] [0 2]]
        [[0 0] [0 1] [0 2] [1 2]]
        [[0 0] [0 1] [0 2] [1 2] [2 2] [3 2] [4 2]]]
       (map (juxt identity get-run-length))))

(defn first-comp [a b]
  (let [c (compare (first a) (first b))]
    (if (not= 0 c)
      c
      (compare (vec (second a)) (vec (second b))))))

(def right [0 1])
(def left  [0 -1])
(def up    [-1 0])
(def down  [1 0])

(defn make-vertex [yx direction run]
  (assert (and (vector? yx) (= 2 (count yx))) (str "Invalid yx:" yx))
  (assert (#{left right up down} direction)   (str "Invalid direction:" direction))
  (assert (#{0 1 2 3} run)                    (str "Invalid run:" run))
  {:yx yx :direction direction :run run})

(comment
  (->> (map vector (repeatedly #(rand-int 10)) (take 4 (repeatedly #(make-vertex [0 1] [0 -1] 2))))
       (reduce conj (sorted-set-by first-comp))))

(defn- get-neighbours [cell-costs path-yxs yx]
  (assert (= (last path-yxs) yx) (str "Incorrect path for yx: " yx " path: " path-yxs))
  (let [candidate-nbr-yxs (u/get-nbr-yxs cell-costs yx {:diagonals false})
        prev              (last (butlast path-yxs))
        nbr-yxs           (->> candidate-nbr-yxs
                               (filterv (fn [nyx] (not= prev nyx)))
                               (filterv (fn [nyx] (<= (get-run-length (conj path-yxs nyx)) 3))))]
    (->>  nbr-yxs
          (mapv (fn [nyx]
                  (make-vertex nyx (mapv - nyx yx) (get-run-length (conj path-yxs nyx))))))))
(comment (get-neighbours (parse-input small-example)
                         [[0 0] [0 1] [0 2] [1 2]]
                         [1 2]))

(defn- shortest-path [start-yx end-yx cell-costs]
  (let [max-steps         500000
        start-vertex      (make-vertex start-yx right 0)
        start-path        [start-yx]]

    (loop [vertex->state {start-vertex {:cost 0 :predecessor nil}}
           work-items    (sorted-set-by first-comp [0 start-vertex])
           steps         0]

      (when (> steps max-steps) (throw (Exception. (str "Max step count reached: " steps))))

      ;; I think we have work through the entire priority queue because
      ;; the end-yx's we find are conceptually on different graphs, so need to
      ;; search to all possible end-yxs and get the minimum
      ;; usual break criterion: get to end-yx <- because know shortest path at that point
      (if (empty? work-items)
        (into {} (filterv (fn [[vertex]] (= (:yx vertex) end-yx)) vertex->state))

        (let [[cost vertex :as work-item] (first work-items)
              {:keys [yx direction run]} vertex
              path-to-vertex       (->> (iterate (fn [v] (:predecessor (get vertex->state v)))
                                                 vertex)
                                        (take 4)
                                        (keep :yx)
                                        reverse
                                        vec)
              nbr-vertices         #break (get-neighbours cell-costs path-to-vertex yx)
              curr-new-cost-pairs  (fn [nbr-v]
                                     (let [current-cost (-> (get vertex->state nbr-v) :cost)
                                           new-cost     (+ (get-in cell-costs (:yx nbr-v))
                                                           cost)]
                                       [current-cost new-cost]))
              relax-vertex         (fn [v->state nbr-v]
                                     (let [[current-cost new-cost] (curr-new-cost-pairs nbr-v)]
                                       (if (or (nil? current-cost) (< new-cost current-cost))
                                         (assoc v->state nbr-v {:cost new-cost
                                                                :predecessor vertex})
                                         v->state)))
              update-work-items   (fn [w-items nbr-v]
                                    (let [[current-cost new-cost] (curr-new-cost-pairs nbr-v)]
                                      (if (or (nil? current-cost) (< new-cost current-cost))
                                        (conj w-items [new-cost nbr-v])
                                         w-items)))]
          (recur
           (reduce relax-vertex vertex->state nbr-vertices)
           (reduce update-work-items (disj work-items work-item) nbr-vertices)
           (inc steps)))))))

(defn pt1 [input]
  (let [grid        (parse-input input)
        cell-costs  grid
        start-yx    [0 0]
        end-yx      [(dec (u/y-size cell-costs)) (dec (u/x-size cell-costs))]
        cheapest    (shortest-path start-yx end-yx cell-costs)
        ans         cheapest
        ans         (mapv (fn [[{:keys [yx]} {:keys [cost]}]] {:yx yx :cost cost}) cheapest)
        ans         (min-by :cost (vals cheapest))
        ]
    ans))

;; (defn pt2 [input]
  ;; (let [matrix (parse-input input)
        ;; ans    matrix]
    ;; ans))

(comment
  (time (pt1 small-example))
  (time (pt1 example))

  (time (pt1 input))

  ;; 640 (too high)
  ;; 677 (too high)

  ;; (pt2 example)
  ;; (pt2 input)
  ;; Attempt to brute force example data: 50k steps, 71,921 paths, none completed ~ 15 seconds
  ;;
  )
