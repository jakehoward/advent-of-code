(ns aoc.day-17
  (:require [aoc.utils :as u]
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

(defn estimate-cost [step-cost-est matrix y x]
  (* step-cost-est
     (- (+ (u/y-size matrix) (u/x-size matrix)) y x 2)))

(defn path-cost [node-cost cheapest-nbr cost-to-move]
  (+ node-cost cost-to-move (* cost-to-move (count (:yxs cheapest-nbr [])))
     (or (:cost cheapest-nbr) 0)))

(defn total-cost [newcost step-cost-est matrix [y x]]
  (+ newcost
     (estimate-cost step-cost-est matrix y x)))

(defn get-run-length [direction yx routes]
  (let [nbr-fn (case direction :right u/left :left u/right :down u/above :up u/below)
        path   (-> (or (get-in routes (into (nbr-fn yx) [:yxs])) [])
                   (into [yx]))
        run-fn (case direction
                 :right (fn [[[ay ax] [by bx]]] (and (= ay by)
                                                     (= 1 (- ax bx))))
                 :left  (fn [[[ay ax] [by bx]]] (and (= ay by)
                                                     (= 1 (- bx ax))))
                 :down  (fn [[[ay ax] [by bx]]] (and (= ax bx)
                                                     (= 1 (- ay by))))
                 :up    (fn [[[ay ax] [by bx]]] (and (= ax bx)
                                                     (= 1 (- by ay)))))
        run-length (->> path
                        reverse
                        (partition 2 1)
                        (take-while run-fn)
                        (map first)
                        count
                        (+ 1))
        ;; _ (println "path:" path "runl:" run-length)
        ]
    (if (= 0 (count path))
      0
      run-length)))

(defn get-nbr-yxs [y-size x-size yx direction routes]
  ;; if 3 in same direction in a row, have to do 90 degree turn
  ;; otherwise straight on, or 90 degree turn
  (let [straight-ahead (case direction
                         :down  {:yx (u/below yx) :dir :down}
                         :up    {:yx (u/above yx) :dir :up}
                         :left  {:yx (u/left yx)  :dir :left}
                         :right {:yx (u/right yx) :dir :right})
        num-in-row     (get-run-length direction yx routes)
        ;; _ (println "num in row:" num-in-row)
        lr-90s         (case direction
                         :down  [{:yx (u/left yx) :dir :left}  {:yx (u/right yx) :dir :right}]
                         :up    [{:yx (u/right yx) :dir :right} {:yx (u/left yx) :dir :left}]
                         :left  [{:yx (u/above yx) :dir :up} {:yx (u/below yx) :dir :down}]
                         :right [{:yx (u/below yx) :dir :down} {:yx (u/above yx) :dir :up}])
        ;; todo: these nbrs aren't actually possible
        ;;       as there may have been no way to get
        ;;       to this point in this direction (or
        ;;       even if there were, it wasn't the route)
        ;;       => maybe the route has to have coords and
        ;;          direciton rather than just yx
        poss-prev-nbrs (case direction
                         :down  [{:yx (u/above yx) :dir :down}
                                 {:yx (u/above yx) :dir :right}
                                 {:yx (u/above yx) :dir :left}]
                         :up    [{:yx (u/below yx) :dir :up}
                                 {:yx (u/below yx) :dir :right}
                                 {:yx (u/below yx) :dir :left}]
                         :left  [{:yx (u/right yx) :dir :left}
                                 {:yx (u/right yx) :dir :up}
                                 {:yx (u/right yx) :dir :down}]
                         :right [{:yx (u/left yx) :dir :right}
                                 {:yx (u/left yx) :dir :up}
                                 {:yx (u/left yx) :dir :down}])
        prev-nbrs      (->> poss-prev-nbrs
                            (filter (fn [{:keys [yx]}] (seq (get-in routes yx))))
                            vec)
        in-bounds?     (fn [[y x]] (and (< -1 y y-size) (< -1 x x-size)))

        all-nbrs (->> (into (if (<= num-in-row 3) [straight-ahead] [])
                            (concat lr-90s prev-nbrs))
                      (filter (fn [{:keys [yx]}] (in-bounds? yx)))
                      vec)
        ]
    all-nbrs))

;; (comment (pt1 example))

(comment
  (get-nbr-yxs 3 3 [0 0] :right [[nil nil nil] [nil nil nil]])
  (get-nbr-yxs 4 4 [0 2] :right [[{:cost 1 :yxs [[0 0]]}
                                  {:cost 1 :yxs [[0 0] [0 1]]}
                                  {:cost 1 :yxs [[0 0] [0 1] [0 2]]}
                                   nil]
                                 [nil nil nil]])
  (get-nbr-yxs 4 4 [0 1] :left [[nil
                                 nil
                                 {:cost 1 :yxs [[0 3] [0 2]]}
                                 {:cost 1 :yxs [[0 3]]}]
                                [nil nil nil]])
  ;
  )

(defn work-item-sorter [w1 w2]
  (let [c (compare (first w1) (first w2))]
    (if (not= c 0)
      c
      (compare (:yx w1) (:yx w2)))))


(comment
  (-> (sorted-set-by work-item-sorter [0 {:yx [0 0] :dir :up}])
      (conj [3 {:yx [1 1] :dir :up}])
      (conj [1 {:yx [1 1] :dir :up}])))

(defn a-star [start-yx step-est cell-costs cost-to-move]
  (println "-- Starting A* --")
  (let [y-size (u/y-size cell-costs)
        x-size (u/x-size cell-costs)]
    (loop [steps 0
           routes (vec (repeat y-size (vec (repeat x-size nil))))
           work-todo (sorted-set-by work-item-sorter [0 {:yx start-yx :dir :right}])]
      (if (or (> steps 50000) (empty? work-todo))
        {:route (peek (peek routes))
         :route-len (count (:yxs (peek (peek routes))))
         :num-steps steps} ;; assumes ends at bottom right (the peek peek)

        (let [[_ item :as work-item] (first work-todo)
              {:keys [yx dir]}     item
              rest-work-todo       (disj work-todo work-item)
              ;; _ (println "yx:" yx "d:" dir "rs:" routes)
              nbr-dir-yxs          (get-nbr-yxs y-size x-size yx dir routes)
              cheapest-nbr         (min-by :cost (keep #(get-in routes (:yx %)) nbr-dir-yxs))
              newcost              (path-cost (get-in cell-costs yx) cheapest-nbr cost-to-move)
              oldcost              (:cost (get-in routes yx))
              ;; _ (println "---- ITER ----")
              ;; _ (println "wi:" work-item)
              ;; _ (println "wis:" work-todo)
              ;; _ (println "nyxs:" nbr-dir-yxs)
              ;; _ (println "cn:" cheapest-nbr )
              ;; _ (println "rs:" (keep #(get-in routes (:yx %)) nbr-dir-yxs))
              ;; _ (println "mbc:" (min-by :cost (keep #(get-in routes (:yx %)) nbr-dir-yxs)))
              ;; _ (println "nc:" newcost "oc:" oldcost)
              ;; _ (println  "rrs:" routes)
              ]
          (if (and oldcost (>= newcost oldcost))
            (recur (inc steps) routes rest-work-todo)
            (recur (inc steps)
                   (assoc-in routes yx {:cost newcost :yxs (conj (:yxs cheapest-nbr []) yx)})
                   (into rest-work-todo
                         (map (fn [w] [(total-cost newcost step-est cell-costs (:yx w)) w]) nbr-dir-yxs)))))))))


(defn pt1 [input]
  (let [matrix    (parse-input input)
        path-res  (a-star [0 0] 1 matrix 1)
        route     (get-in path-res [:route :yxs])
        route-set (set route)
        heat-cost (->> (drop 1 route)
                       (map (fn [yx] (get-in matrix yx)))
                       u/sum)
        viz      (->> (for [y (range (count matrix))
                            x (range (count (first matrix)))]
                        (cond (contains? route-set [y x])
                             "x"
                             :else
                             "."))
                      (partition (count (first matrix)))
                      (map vec)
                      vec)
        ans         path-res
        ;; ans      route-set
        ;; ans      heat-cost
        ;; ans      viz
        ]
    ans))

(defn pt2 [input]
  (let [parsed (parse-input input)
        ans    parsed]
    ans))

(comment
  (pt1 example)
  (pt1 input)
  (pt2 example)
  (pt2 input)
;
)
