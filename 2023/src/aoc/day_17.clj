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

(defn- get-neighbours [cell-costs path yx]
  (let [nbr-yxs (u/get-nbr-yxs cell-costs yx {:diagonals false})
        prev    (last (butlast path))
        ans     #break (->> nbr-yxs
                            (filterv (fn [nyx] (not= prev nyx)))
                            (filterv (fn [nyx] (<= (get-run-length (conj path nyx)) 3))))]
    ans))

;; Bug: get-neighbours only gets to look at one path whereas it needs to look at all
;;      possible paths to that point, example [0 6] doesn't get to see [0 7] as
;;      a possible neighbour.
(defn- shortest-path [start-yx end-yx cell-costs]
  (let [max-steps         100000]
    (loop [known-paths   {start-yx {:cost 0 :path [start-yx]}}
           ;; Strictly speakng a vertex is defined by [co-ord, direction, length-of-travel]
           ;; Not clear if this assumption will catch up with us or not...
           vs-to-search  (sorted-set [0 start-yx]) ;; todo - better data structure
           steps         0]

      (when (> steps max-steps) (throw (Exception. (str "Max step count reached: " steps))))

      ;; I think we have work through the entire priority queue because
      ;; the end-yx's we find are conceptually on different graphs, so need to
      ;; search to all end-yxs and get the minimum
      ;; usual break criterion: (= end-yx (second (first vs-to-search)))
      (if (empty? vs-to-search)
        {:steps         steps
         :cost          (:cost (get known-paths end-yx))
         :rem-to-search (count vs-to-search)
         :shortest-path (:path (get known-paths end-yx))}

        (let [[cost yx :as vertex] (first vs-to-search)
              next-vs-to-search    (disj vs-to-search vertex)
              path-to-yx           (-> (get known-paths yx) :path)
              nbr-yxs              (get-neighbours cell-costs path-to-yx yx)
              curr-new-cost-pairs  (fn [nbr-yx]
                                         (let [current-cost (-> (get known-paths nbr-yx) :cost)
                                               new-cost     (+ (get-in cell-costs nbr-yx)
                                                               cost)]
                                           [current-cost new-cost]))
              relax-vertex         (fn [kp nbr-yx]
                                     (let [[current-cost new-cost] (curr-new-cost-pairs nbr-yx)]
                                       (if (or (nil? current-cost) (< new-cost current-cost))
                                         (assoc kp nbr-yx {:path (conj path-to-yx nbr-yx)
                                                           :cost new-cost})
                                         kp)))
              update-vs-to-search (fn [vs nbr-yx]
                                    (let [[current-cost new-cost] (curr-new-cost-pairs nbr-yx)]
                                       (if (or (nil? current-cost) (< new-cost current-cost))
                                         (conj vs [new-cost nbr-yx])
                                         vs)))]
          (recur
           (reduce relax-vertex known-paths nbr-yxs)
           (reduce update-vs-to-search next-vs-to-search nbr-yxs)
           (inc steps)))))))

(defn pt1 [input]
  (let [grid        (parse-input input)
        cell-costs  grid
        start-yx    [0 0]
        end-yx      [(dec (u/y-size cell-costs)) (dec (u/x-size cell-costs))]
        cheapest    (shortest-path start-yx end-yx cell-costs)
        ans         cheapest]
    ans))

;; (defn pt2 [input]
  ;; (let [matrix (parse-input input)
        ;; ans    matrix]
    ;; ans))

(comment
  (time (pt1 small-example))
  (time (pt1 example))

  (time (pt1 input))

  ;; 677 (too high)

  ;; (pt2 example)
  ;; (pt2 input)
  ;; Attempt to brute force example data: 50k steps, 71,921 paths, none completed ~ 15 seconds
  ;;
  )
