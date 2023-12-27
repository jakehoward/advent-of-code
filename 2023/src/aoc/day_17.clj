(ns aoc.day-17
  (:require [aoc.utils :as u]
            [aoc.vis :as vis]
            [clojure.string :as str]))

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
(def input (u/get-input 17))

(defn parse-input [input]
  (mapv #(mapv u/parse-int %) (u/input->matrix input)))

(defn min-by [f coll]
  (when (seq coll)
    (reduce (fn [min other]
              (if (> (f min) (f other))
                other
                min))
            coll)))

(defn neighbours [size yx]
  (let [deltas [[-1 0] [1 0] [0 -1] [0 1]]]
    (->> deltas
         (map #(vec (map + yx %)))
         (filter (fn [new-yx] (every? #(< -1 % size) new-yx))))))

(defn joy-heuristic [step-cost-est size y x]
  (* step-cost-est
     (- (+ size size) y x 2)))

;; estimate-cost => h(...) in the literature (presumably "heuristic")
(def estimate-cost joy-heuristic)

;; path-cost => g(...) in the literature (presumably "god, it's annoying when ppl use symbols")
(defn path-cost [node-cost cheapest-nbr cost-to-move]
  (+ node-cost cost-to-move (* cost-to-move (count (:xys cheapest-nbr [])))
     (or (:cost cheapest-nbr) 0)))

;; total-cost => f(...) in the literature
(defn total-cost [newcost step-cost-est size [y x]]
  (+ newcost
     (estimate-cost step-cost-est size y x)))

(comment
  (-> (sorted-set [0 0 [0 0]])
      (conj [0 1 [1 1]])
      (conj [9 -2 [9 9]])
      (conj [1 -1 [1 1]])
      (conj [9 -3 [9 9]])))

(defn a-star [start-yx step-est cell-costs cost-to-move]
  (let [size (count cell-costs)
        progression (atom [])]
    (loop [steps 0
           routes (vec (repeat size (vec (repeat size nil))))
           ;;                    [cost LIFO-order yx]
           work-todo (sorted-set [0 0 start-yx])]
      (swap! progression (fn [c] (conj c routes)))
      (if (or (empty? work-todo) (not (nil? (peek (peek routes)))))
        {:route (peek (peek routes)) :steps steps :progression @progression}
        (let [[_ _ yx :as work-item] (first work-todo)
              rest-work-todo (disj work-todo work-item)
              nbr-yxs (neighbours size yx)
              cheapest-nbr (min-by :cost (keep #(get-in routes %) nbr-yxs))
              newcost (path-cost (get-in cell-costs yx) cheapest-nbr cost-to-move)
              oldcost (:cost (get-in routes yx))]
          (if (and oldcost (>= newcost oldcost))
            (recur (inc steps) routes rest-work-todo)
            (recur (inc steps)
                   (assoc-in routes yx {:cost newcost :yxs (conj (:yxs cheapest-nbr []) yx)})
                   (into rest-work-todo
                         (map (fn [w] [(total-cost newcost step-est size w)
                                       ;; ensure LIFO behaviour when cost is equal
                                       (if (seq rest-work-todo)
                                         (->> rest-work-todo (map second) (apply min) dec)
                                         0)
                                       w]) nbr-yxs)))))))))


(defn pt1 [input]
  (let [matrix  (parse-input input)
        ans     (a-star [0 0] 1 matrix 1)]
    ans))

(defn pt2 [input]
  (let [parsed (parse-input input)
        ans    parsed]
    ans))

(comment
  (count (:progression (pt1 example)))
  (pt1 input)
  (pt2 example)
  (pt2 input)

  (let [matrix [[1  99 99 99 99 99 99 99 99]
                [1  66 66 66 66 66 66 66 66]
                [1  1  1  1  1  1  1  1  1 ]
                [66 66 66 66 66 66 66 66 1 ]
                [99 99 99 99 99 99 99 99 1 ]
                [1  1  1  1  1  1  1  1  1 ]
                [1  66 66 66 66 66 66 66 66]
                [1  99 99 99 99 99 99 99 99]
                [1  1  1  1  1  1  1  1  1 ]]
        {:keys [progression route]} (a-star [0 0] 1 matrix 1)
        path-yxs-set (set (:yxs route))
        grids-to-vis (->> progression
                          (mapv
                           (fn [grid]
                             (mapv (fn [row] (mapv (fn [i] (:cost i)) row)) grid))))
        max-cost    (apply max (filter identity (flatten grids-to-vis)))
        scale-cost (fn [cost] (int (* 9 (/ cost max-cost))))]
    (->> (vis/grids->html grids-to-vis
                          (fn [m yx]
                            {:shape :rect
                             :text (or (str (get-in matrix yx)) "-")
                             :color (if (get-in m yx)
                                      (if (contains? path-yxs-set yx)
                                        "green"
                                        (nth vis/ten-step-palette (scale-cost (get-in m yx))))
                                      "darkgray")}))
         (spit "vis.html")))

  (def random-matrix (vec (repeatedly 15 #(vec (repeatedly 15 (fn [] (rand-int 10)))))))

  (let [matrix                (parse-input example)
        ;; {:keys [progression]} (a-star [0 0] 1 matrix 1)
        matrix random-matrix
        {:keys [progression route]} (a-star [0 0] 1 matrix 1)
        path-yxs-set (set (:yxs route))
        grids-to-vis (->> progression
                          (mapv
                           (fn [grid]
                             (mapv (fn [row] (mapv (fn [i] (:cost i)) row)) grid))))
        max-cost    (apply max (filter identity (flatten grids-to-vis)))
        scale-cost (fn [cost] (int (* 9 (/ cost max-cost))))]
    (->> (vis/grids->html grids-to-vis
                          (fn [m yx]
                            {:shape :rect
                             :text (or (str (get-in m yx)) "-")
                             ;; :text (or (str (get-in matrix yx)) "-")
                             :color (if (get-in m yx)
                                      (if (contains? path-yxs-set yx)
                                        "green"
                                        (nth vis/ten-step-palette (scale-cost (get-in m yx))))
                                      "darkgray")}))
         (spit "vis.html")))

((fn [cost] (int (* 9 (/ cost 109)))) 109)
(->> (vis/grids->html [(parse-input example)]
                        (fn [m yx]
                          {:shape :rect :color (nth vis/ten-step-palette (get-in m yx))}))
       (spit "vis.html"))
;
)
