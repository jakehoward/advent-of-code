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
(def pathological-example (str/trim "
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

(defn first-comp [a b]
  (let [c (comp (first a) (first b))]
    (if (not= 0 c)
      c
      (comp (rest a) (rest b)))))

(defn neighbours [routes yx]
  (u/get-neighbours-coords-yx routes yx {:diagonals false}))

(defn best-case-cost-to-end [size y x]
  (- (+ size size) y x 2))

(defn path-cost [node-cost cheapest-nbr-cost]
  (+ node-cost cheapest-nbr-cost))

(defn best-total-cost [the-path-cost size [y x]]
  (+ the-path-cost
     (best-case-cost-to-end size y x)))

(defn get-neighbours [all-paths path]
  ;; get the neighbours from the routes, not the yxs
  ;; Routes Vec, of Vec of Vec of routes (y, x, run-length/direction options)
  ;; Valid nbr is one where yx is an extension of an existing route
  ;; that does not move more than 3 steps in a single direction

  ;; Get up/down/left/right yx from point
  ;; If a neighbour only has a path such that yx would be the 4th in a given direction
  ;; then it's not a valid nbr

  ;; If there is no route for a given yx return it with dir and num-in-row of 1

  ;; Special case for yx = start ([0 0]) ?
  )

;; Route: cost, direction, num-in-row, yxs

(defn- same-direction-and-run-len? [path-a path-b]
  (and (= (:direction path-a) (:direction path-b))
       (= (:run path-a) (:run path-b))))

(defn a-star [start-yx cell-costs]
  (let [size       (count cell-costs)
        first-path {:cost 0 :direction :right :run 0 :yxs [start-yx]}
        max-steps 1500]
    (loop [steps 0
           all-paths (vec (repeat size (vec (repeat size nil))))
           work-todo (sorted-set-by first-comp [0 first-path])]

      (when (> steps max-steps)
        (throw (Exception. (str "Max step count exceeded. "
                                (count work-todo) " work-items "
                                (count (filter identity (flatten all-paths))) " ends reached."))))

      ;; todo: is it safe to end when reached cell? If you get there is it inherently
      ;;       the cheapest way to do so? Could another work item usurp it?
      (if (empty? work-todo) ;;(or (empty? work-todo) (not (nil? (peek (peek routes)))))

        ;; TODO: maintain multiple routes (min-by :cost ???)
        {:route (peek (peek all-paths)) :steps steps}

        (let [path-to-explore (first work-todo)
              yx              (last (:yxs path-to-explore))
              rest-work-todo  (disj work-todo path-to-explore)
              nbrs            (get-neighbours all-paths path-to-explore)
              ;; TODO: somehow need to ensure that cheapest nbrs for other directions and
              ;;       run lengths are still explored. Does this happen naturally?
              ;;       Choosing this cheapest nbr basically shuts down future options
              ;;       which may be cheaper. But without choosing one like this, there's
              ;;       no point to this algorithm, it might as well be brute force...
              cheapest-nbr    (min-by :cost nbrs)
              newcost         (path-cost (get-in cell-costs yx) (:cost cheapest-nbr 0))
              oldcost         (:cost path-to-explore)
              new-run         (if (= (:direction cheapest-nbr) (:direction path-to-explore))
                                (inc (:run path-to-explore))
                                1)]
          ;; TODO: is this logic still valid? Yes: there's a cheaper way to get to this exact point
          ;;       ONLY true if we consider this point, direction and run length
          (if (and oldcost (>= newcost oldcost))
            (recur (inc steps) all-paths rest-work-todo)
            (recur (inc steps)
                   ;; TODO: maintain multiple routes
                   (assoc-in all-paths yx
                             (as-> (get-in all-paths yx) $
                               ;; how much work is it saving if we still maintain all
                               ;; other run lengths and directions? Or is it about ensuring
                               ;; we maintain valid options?
                               (filterv #(not (same-direction-and-run-len? path-to-explore %)) $)
                               (conj $ {:cost      newcost
                                        :direction (:direction path-to-explore)
                                        :run       new-run
                                        :yxs       (conj (:yxs cheapest-nbr []) yx)})))
                   (into rest-work-todo
                         (map (fn [nbr]
                                [(best-total-cost newcost size (-> nbr :yxs last))
                                 (-> nbr :yxs last)])
                              nbrs)))))))))


(defn pt1 [input]
  (let [matrix   (parse-input input)
        cheapest (a-star [0 0] matrix)
        ans      (select-keys cheapest [:cost :yxs])]
    ans))

(defn pt2 [input]
  (let [matrix (parse-input input)
        ans    matrix]
    ans))

(comment
  (time (pt1 pathological-example))
  (time (pt1 small-example))
  (time (pt1 example))
  ;; Attempt to brute force example data: 50k steps, 71,921 paths, none completed ~ 15 seconds

  (pt1 input)
  (pt2 example)
  (pt2 input)
  ;;
  )
