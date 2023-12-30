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

;; Attempt to make example that requires re-visiting node to get cheapest path
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

(defn first-comp [a b]
  (let [c (comp (first a) (first b))]
    (if (not= 0 c)
      c
      (comp (rest a) (rest b)))))

(defn best-case-cost-to-end [x-size y-size y x]
  (- (+ x-size y-size) y x 2))

(defn path-cost [node-cost cheapest-nbr-cost]
  (+ node-cost cheapest-nbr-cost))

(defn best-total-cost [the-path-cost x-size y-size [y x]]
  (+ the-path-cost
     (best-case-cost-to-end x-size y-size y x)))

(defn- get-nbr-yxs [cell-costs all-paths yx]
  (let [run-length          (fn [nyx yx]
                               (->> (:yxs (get-in all-paths nyx) [])
                                    reverse
                                    (take-while #(or (= (first %)  (first yx))
                                                     (= (second %) (second yx))))
                                    count))
        path-run-length     (fn [yxs]
                              (->> yxs
                                   reverse
                                   (take-while #(or (= (first %)  (first (last yxs)))
                                                    (= (second %) (second (last yxs)))))
                                   count))
        candidate-yxs       (u/get-neighbours-coords-yx cell-costs yx {:diagonals false})
        existing-paths      (->> candidate-yxs
                                 (keep #(get-in all-paths %))
                                 (mapv #(update % :yxs conj yx))
                                 (filterv #(<= (path-run-length (:yxs %)) 3)))

        existing-path-nbr   (mapv #(last (butlast (:yxs %))) existing-paths)
        ;; only valid if existing path through yx => nbr is less than 4 in same direction
        valid-extension-nbr nil
        valid-nbr-yxs (->> candidate-yxs
                           (filter #(< (run-length % yx) 3)))
        ]
    nil))

;; > > c i c
;; v > > c

;; > > c i c
;; - - - c
(defn path-run-length [yxs]
  (->> yxs
       reverse
       (take-while #(or (= (first %)  (first (last yxs)))
                        (= (second %) (second (last yxs)))))
       count))

(defn is-valid-next-nbr? [paths-to-yx nbr-yx]
  ;; valid nexts are existing + yx + candidate
  ;; that satisfy: not going back on self, run length <= 3
  (->>  paths-to-yx
        (filter (fn [path-to-yx]
                  (and (<= (path-run-length (conj (vec path-to-yx) nbr-yx)) 3)
                       (not= (last path-to-yx) nbr-yx))))
        seq
        boolean))

(defn is-valid-existing-nbr? [all-paths yx nbr-yx]
  (let [existing-path (get-in all-paths nbr-yx)]
    (if existing-path
      (<= (path-run-length (conj (:yxs existing-path) yx)) 3)
      false)))

(defn- existing-path-nbr-yxs [cell-costs all-paths yx]
  (let [candidate-yxs (u/get-neighbours-coords-yx cell-costs yx {:diagonals false})]
    (filterv (partial is-valid-existing-nbr? all-paths yx) candidate-yxs)))

(defn- get-next-nbr-yxs [cell-costs all-paths yx]
  (let [candidate-yxs (u/get-neighbours-coords-yx cell-costs yx {:diagonals false})
        paths-to-yx   (->> (existing-path-nbr-yxs cell-costs all-paths yx)
                           (mapv (fn [nyx] (-> (get-in all-paths nyx) :yxs (conj yx)))))]

    (when (and (not= yx [0 0]) (some #(= 0 (count %)) paths-to-yx))
      (throw (Exception. (str "Invalid: yx: " yx " paths-to-yx: " paths-to-yx "all: " all-paths))))

    (if (= [0 0] yx)
      candidate-yxs
      (filterv (partial is-valid-next-nbr? paths-to-yx) candidate-yxs))))


;; Bug: The valid path that you rely on existing can be
;;      eradicated by a subsequent iteration.
(defn a-star [start-yx cell-costs]
  (let [max-steps 2500
        y-size (u/y-size cell-costs)
        x-size (u/x-size cell-costs)]
    (loop [steps 0
           all-paths (vec (repeat y-size (vec (repeat x-size nil))))
           work-todo (sorted-set [0 start-yx])]

      (when (> steps max-steps)
        (throw (Exception. (str "Max step count exceeded. "
                                (count work-todo) " work-items "
                                (count (filter identity (flatten all-paths))) " ends reached."))))

      ;; try: (or (empty? work-todo) (not (nil? (peek (peek routes)))))
      ;; (not 100% sure it's safe)
      (if (empty? work-todo)

        {:route (peek (peek all-paths)) :steps steps}

        (let [[_ yx :as work-item] (first work-todo)
              rest-work-todo    (disj work-todo work-item)

              next-nbr-yxs      (get-next-nbr-yxs cell-costs all-paths yx)
              existing-nbr-yxs  (existing-path-nbr-yxs cell-costs all-paths yx)
              nbr-yxs           (concat existing-nbr-yxs next-nbr-yxs)

              ;; can't simply look at all-neighbours as a valid yx to extend into
              ;; isn't necessarily a valid path to extend from
              cheapest-nbr     (min-by :cost (map #(get-in all-paths %) existing-nbr-yxs))
              newcost          (path-cost (get-in cell-costs yx) (:cost cheapest-nbr 0))
              oldcost          (:cost (get-in all-paths yx))
              foo              #dbg ^{:break/when (contains? (set next-nbr-yxs) [3 1])} nbr-yxs]
          (if (and oldcost (>= newcost oldcost))
            (recur (inc steps) all-paths rest-work-todo)
            (recur (inc steps)
                   (assoc-in all-paths yx
                             {:cost newcost
                              :yxs  (conj (:yxs cheapest-nbr []) yx)})
                   (into rest-work-todo
                         (mapv (fn [nyx] [(best-total-cost newcost x-size y-size nyx) nyx])
                               nbr-yxs)))))))))


(defn pt1 [input]
  (let [matrix   (parse-input input)
        cheapest (a-star [0 0] matrix)
        ans      cheapest]
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
