(ns aoc.day-17
  (:require [aoc.utils :as u]
            [aoc.vis :as vis]
            [flatland.ordered.set :refer [ordered-set]]
            [clojure.string :as str]))

;; Assumptions:
;; - The data doesn't/can't be set up in such a way that the cheapest path
;;   requires re-visiting a node (i.e. in a loop to avoid high cost nodes
;;   but to allow the constraint of only 3 moves in one direction)

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

(def small-example (str/trim "
2413
9215
3215
3214
4322"))

;; Attempt to make that that requires re-visiting node to get cheapest path
;; - Not 100% convinced, but it seems it might not be possible to force a loop.
;;   Would it be (easier?) with larger numbers or a different path max run-length
(def pathalogical-example (str/trim "
11111
99191
99191
99111
9992"))

(def input (u/get-input 17))

(defn parse-input [input]
  (mapv #(mapv u/parse-int %) (u/input->matrix input)))

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

(defn- new-direction [old-direction yx nbr-yx]
  (cond (u/right? yx nbr-yx) :right
        (u/left? yx nbr-yx) :left
        (u/above? yx nbr-yx) :up
        (u/below? yx nbr-yx) :down
        :else (throw (Exception.
                      (str "Unexpected new direction " old-direction
                           " yx: " yx
                           " nbr-yx: " nbr-yx)))))

(defn- get-nbrs [matrix {:keys [direction yxs num-in-row] :as path}]
  (let [yx             (last yxs)
        poss-nbr-yxs   (u/get-neighbours-coords-yx matrix yx {:diagonals false})
        straight-on    (case direction
                         :right (u/right yx)
                         :left  (u/left yx)
                         :up    (u/above yx)
                         :down  (u/below yx))]
    (->> poss-nbr-yxs
         (keep (fn [nbr-yx]
                 ;; technically want to allow revisits but not
                 ;; infinite loops
                 (cond (contains? yxs nbr-yx)
                       nil

                       (= straight-on nbr-yx)
                       (when (< num-in-row 3)
                         {:direction direction :num-in-row (inc num-in-row) :yx nbr-yx})

                       :else
                       {:direction (new-direction direction yx nbr-yx) :num-in-row 1 :yx nbr-yx})))
         vec)))

(defn cheapest-path [matrix]
  (let [start-yx [0 0]
        end-yx   [(dec (u/y-size matrix)) (dec (u/x-size matrix))]
        max-steps 50000]
    (loop [paths           [{:cost 0 :num-in-row 0 :direction :right :yxs (ordered-set start-yx)}]
           completed-paths []
           steps           0]

      ;; #dbg ^{:break/when (= 0 (mod steps 1000))}

      (when (> steps max-steps)
        (throw (Exception. (str "Max step count exceeded. "
                                (count paths) " paths, "
                                (count completed-paths) " completed paths."))))

      (if (empty? paths)
        (min-by :cost completed-paths)

        (let [path      (first paths)
              nbrs      (get-nbrs matrix path)
              new-paths (->> nbrs
                             (mapv (fn [nbr]
                                     (-> path
                                         (assoc :num-in-row (:num-in-row nbr))
                                         (assoc :cost (+ (:cost path) (get-in matrix (:yx nbr))))
                                         (assoc :direction (:direction nbr))
                                         (assoc :yxs (conj (:yxs path) (:yx nbr)))))))
              next-paths (filterv #(not= end-yx (-> (:yxs %) last)) new-paths)]
          (recur
           (into (vec (rest paths)) next-paths)
           (into completed-paths (filterv #(= end-yx (-> (:yxs %) last)) new-paths))
           (inc steps)))))))

(defn pt1 [input]
  (let [matrix   (parse-input input)
        cheapest (cheapest-path matrix)
        ans      (select-keys cheapest [:cost :yxs])]
    ans))

(defn pt2 [input]
  (let [matrix (parse-input input)
        ans    matrix]
    ans))

(comment
  (time (pt1 pathalogical-example))
  (time (pt1 small-example))
  (time (pt1 example))
  ;; Attempt to brute force example data: 50k steps, 71,921 paths, none completed ~ 15 seconds

  (pt1 input)
  (pt2 example)
  (pt2 input)
  ;;
  )
