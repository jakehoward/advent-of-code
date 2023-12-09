(ns aoc.day-08
  (:require [aoc.utils :as u]
            [clojure.string :as str]
            [clojure.set :as set]))

(def example (str/trim "
LLR

AAA = (BBB, BBB)
BBB = (AAA, ZZZ)
ZZZ = (ZZZ, ZZZ)"))

(def example-pt2 (str/trim "
LR

11A = (11B, XXX)
11B = (XXX, 11Z)
11Z = (11B, XXX)
22A = (22B, XXX)
22B = (22C, 22C)
22C = (22Z, 22Z)
22Z = (22B, 22B)
XXX = (XXX, XXX)"))

(def input (u/get-input 8))

(defn parse-input [input]
  (let [[dir-str graph-s] (str/split input #"\n\n")
        g-lines           (str/split-lines graph-s)
        parse-node        (fn [line] (rest
                                      (re-matches
                                       #"^(...)\s+=\s+\((...),\s+(...)\)$"
                                       (str/trim line))))
        graph             (->> g-lines
                               (map parse-node)
                               (map (fn [[node left right]] {:node node :left left :right right})))
        directions        (str/split dir-str #"")]
    {:directions directions
     :raw-graph graph
     :graph (reduce #(assoc %1 (:node %2) %2) {} graph)}))

(comment (parse-input example)
         (re-matches #"^(\w+)\s+=\s+\((\w+),\s+(\w+)\)$" "BBB = (AAA, ZZZ)"))

(defn- count-steps-to-zzz [{:keys [directions graph]}]
  (loop [curr-node (get graph "AAA")
         dirs      (cycle directions)
         steps     0]
    (if (or (> steps 100000)
            (= "ZZZ" (:node curr-node)))
      steps
      (recur (get graph (if (= "L" (first dirs)) (:left curr-node) (:right curr-node)))
             (rest dirs)
             (inc steps)))))

(defn- count-ghosty-steps-to-xxz [{:keys [directions graph]}]
  (let [starting-nodes (->> (keys graph)
                            (filter (fn [n] (.endsWith n "A")))
                            (map #(get graph %)))]
    (loop [curr-nodes starting-nodes
           dirs       (cycle directions)
           steps      0]
      (if (or (> steps 10000000)
              (every? (fn [n] (.endsWith (:node n) "Z")) curr-nodes))
        steps
        (recur (->> curr-nodes
                    (mapv #(get graph (if (= "L" (first dirs)) (:left %) (:right %)))))
               (rest dirs)
               (inc steps))))))

(defn- get-ghosty-z-steps-single [{:keys [directions graph]} start-node]
  (comment (println ">>"))

  (loop [curr-node start-node
         dirs      (cycle directions)
         z-steps   []
         steps     0]

    (comment (println "c:" curr-node "d:" (first dirs) "z-s:" z-steps "s:" steps "..z?:" (.endsWith (:node curr-node) "Z")))

    (if (> steps 100000)
      z-steps
      (recur (get graph (if (= "L" (first dirs)) (:left curr-node) (:right curr-node)))
             (rest dirs)
             (if (.endsWith (:node curr-node) "Z") (conj z-steps steps) z-steps)
             (inc steps)))))

;; once you get into a cycle, you will keep getting z nodes at predictable
;; intervals
;; ---- not-a-cycle -------> c-start ------------> c-end (repeats)
(defn get-step-cycle-for-node [{:keys [directions graph]} start-node]
  (let [num-dirs (count directions)

        cycle-log
        (loop [curr-node    start-node
               dirs         (cycle directions)
               cycle-check  #{}
               cycle-data   []
               steps        0]
          (if (or (> steps 1000000) (contains? cycle-check [(:node curr-node) (mod steps num-dirs)]))
            (conj cycle-data [(:node curr-node) (mod steps num-dirs) steps])
            (let [is-z-node (.endsWith (:node curr-node) "Z")]
              (recur (get graph (if (= "L" (first dirs)) (:left curr-node) (:right curr-node)))
                     (rest dirs)
                     (conj cycle-check [(:node curr-node) (mod steps num-dirs)])
                     (conj cycle-data [(:node curr-node) (mod steps num-dirs) steps])
                     (inc steps)))))]
    (let [cycle-start (first (drop-while (fn [[n]] (not= n (first (last cycle-log)))) cycle-log))
          zs          (filter (fn [[n]] (.endsWith n "Z")) cycle-log)
          cycle-end   (last (butlast cycle-log))]
      (mapv (fn [[a b c]] {:node a :dir-idx b :steps c})
            (vec (concat [cycle-start] zs [cycle-end]))))))

(defn pt1 [input]
  (let [parsed (parse-input input)
        ans    (count-steps-to-zzz parsed)]
    ans))

(defn prime-factors [n]
  (loop [factors []
         n       n
         f       2]
    (cond (= 1 n)
          factors

          (= 0 (mod n f))
          (recur (conj factors f) (/ n f) f)

          :else
          (recur factors n (inc f)))))
(comment (prime-factors 24))

(defn gcd [nums]
  )
(defn lcm [nums])

(defn shit-lcm [nums]
  (let [all-prime-factors (map prime-factors nums)
        max-pfs           (->> all-prime-factors
                               (map frequencies)
                               (map (fn [freqs] (let [max-power (apply max (vals freqs))]
                                                  (->> freqs
                                                       (filter (fn [[n p]] (= max-power p)))
                                                       (map first)
                                                       (apply max))))))
        ;; max-pfs           (map #(apply max %) all-prime-factors)
        max-only          (mapcat (fn [m pfs] (filter #(= m %) pfs)) max-pfs all-prime-factors)]
    (reduce * 1N max-only)))

(comment
  (sort-by second (frequencies [2 2 2 7 7 7 3 3]))
  (lcm [8 9 21])
  (lcm [3 9]))


(defn pt2 [input]
  (let [parsed         (parse-input input)
        starting-nodes (->> (keys (:graph parsed))
                            (filter (fn [n] (.endsWith n "A")))
                            (map #(get (:graph parsed) %)))
        all-cycles     (map (partial get-step-cycle-for-node parsed) starting-nodes)
        z-info         (->> all-cycles
                            (map (fn [cycles] {:length (- (inc (:steps (last cycles)))
                                                          (:steps (first cycles)))
                                               :z-posns  (map :steps (butlast (drop 1 cycles)))})))
        z-cycles       (->> z-info
                            (mapcat (fn [z] (map (fn [uz] [[:length (:length z)] [:z-pos uz]])
                                                 (:z-posns z))))
                            (map #(into {} %)))]
    (comment
      (loop [orig-nums (mapv :z-pos z-cycles)
             last-nums orig-nums
             sets      (map (fn [v] #{}) orig-nums)
             steps     0]
        (if (or (> steps 100000)
                (= (count starting-nodes) (count (apply set/intersection sets))))
          (apply set/intersection sets)
          (recur orig-nums
                 (map + last-nums orig-nums)
                 (map #(conj %1 %2) sets last-nums)
                 (inc steps)))))
    (lcm (map :z-pos z-cycles))
    (map :z-pos z-cycles)
    ))

;; 1 2 3 4 5 6 7 8 9 0 1 2 3 4
;; x x c z - e c z - e c z - e



;; todo - wrap in future to abort long running loops?
(comment
  (pt1 example)
  (pt1 input)
  (time (pt2 example-pt2))
  (time (pt2 input)) ;; 632711491215049 (too high)
  )
