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

(defn get-next-yxs-old [y-size x-size path]
  ;; if 3 in same direction in a row, have to do 90 degree turn
  ;; otherwise straight on, or 90 degree turn
  (let [last-3  (take-last 3 path)
        [second-last-yx last-yx] (take-last 2 path)
        [last-yx] (take-last 1 path)
        ;; _ (println "s:" second-last-yx "l:" last-yx "p:" path)
        direction (cond (= [[0 0]] path)
                        :right
                        (u/below? second-last-yx last-yx)
                        :down
                        (u/right? second-last-yx last-yx)
                        :right
                        (u/left? second-last-yx last-yx)
                        :left
                        (u/above? second-last-yx last-yx)
                        :up
                        :else (throw (Exception. (str "Unexpected, path: " path))))
        straight-ahead (case direction
                         :down  (u/below last-yx)
                         :up    (u/above last-yx)
                         :left  (u/left last-yx)
                         :right (u/right last-yx))
        lr-90s         (case direction
                         :down  [(u/left last-yx)  (u/right last-yx)]
                         :up    [(u/right last-yx) (u/left last-yx)]
                         :left  [(u/above last-yx) (u/below last-yx)]
                         :right [(u/below last-yx) (u/above last-yx)])
        in-bounds?     (fn [[y x]] (and (< -1 y y-size) (< -1 x x-size)))]
    (if (and (= 3 (count last-3))
             (or (every? (fn [[y x]] (= y (ffirst last-3))) last-3)
                 (every? (fn [[y x]] (= x (second (first last-3)))) last-3)))
      (into (vec (filter in-bounds? lr-90s)) [last-yx])
      (into (vec (filter in-bounds? (into [straight-ahead] lr-90s))) [last-yx]))))

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

(defn get-nbr-yxs [y-size x-size yx direction routes]
  ;; if 3 in same direction in a row, have to do 90 degree turn
  ;; otherwise straight on, or 90 degree turn
  (let [straight-ahead (case direction
                         :down  {:yx (u/below yx) :dir :down}
                         :up    {:yx (u/above yx) :dir :up}
                         :left  {:yx (u/left yx)  :dir :left}
                         :right {:yx (u/right yx) :dir :right})
        lr-90s         (case direction
                         :down  [{:yx (u/left yx) :dir :left}  {:yx (u/right yx) :dir :right}]
                         :up    [{:yx (u/right yx) :dir :right} {:yx (u/left yx) :dir :left}]
                         :left  [{:yx (u/above yx) :dir :up} {:yx (u/below yx) :dir :down}]
                         :right [{:yx (u/below yx) :dir :down} {:yx (u/above yx) :dir :up}])
        poss-prev-nbrs (case direction
                         :down  [{:yx (u/above yx) :dir :down} {:yx (u/above yx) :dir :right} {:yx (u/above yx) :dir :left}]
                         :up    [{:yx (u/below yx) :dir :up} {:yx (u/below yx) :dir :right} {:yx (u/below yx) :dir :left}]
                         :left  [{:yx (u/right yx) :dir :left} {:yx (u/right yx) :dir :up} {:yx (u/right yx) :dir :down}]
                         :right [{:yx (u/left yx) :dir :right} {:yx (u/right yx) :dir :up} {:yx (u/right yx) :dir :down}])
        prev-nbrs      (->> poss-prev-nbrs
                            (filter (fn [{:keys [yx]}] (seq (get-in routes yx))))
                            vec)
        in-bounds?     (fn [[y x]] (and (< -1 y y-size) (< -1 x x-size)))
        ;; poss-nbrs      (u/get-neighbours-coords-yx-sz y-size x-size yx {:diagonals false})
        ;; todo: see if out of possible nbrs there are valid ones
        ;;       given the direction and yx. If so, is straight on a
        ;;       valid choice?
        all-nbrs (->> (into [straight-ahead] (concat lr-90s prev-nbrs))
                      (filter (fn [{:keys [yx]}] (in-bounds? yx)))
                      vec)
        ]
    all-nbrs))

(comment
  (get-nbr-yxs 3 3 [0 0] :right [[nil nil nil] [nil nil nil]])
  (get-nbr-yxs 4 4 [0 0] :right [[[{:cost 1 :yxs [[0 0]]}]
                                   [{:cost 1 :yxs [[0 0] [0 1]]}]
                                   [{:cost 1 :yxs [[0 0] [0 1] [0 2]]}]
                                   nil]
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

(comment (pt1 example))
(defn a-star [start-yx step-est cell-costs cost-to-move]
  (println "-- Starting A* --")
  (let [y-size (u/y-size cell-costs)
        x-size (u/x-size cell-costs)]
    (loop [steps 0
           routes (vec (repeat y-size (vec (repeat x-size nil))))
           work-todo (sorted-set-by work-item-sorter [0 {:yx start-yx :dir :right}])]
      (if (or (> steps 5000) (empty? work-todo))
        {:route (peek (peek routes)) :num-steps steps} ;; assumes ends at bottom right (the peek peek)
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
  (let [matrix (parse-input input)
        ans    (a-star [0 0] 1 matrix 1)]
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
