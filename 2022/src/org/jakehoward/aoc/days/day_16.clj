(ns org.jakehoward.aoc.days.day-16
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as str]
            [clojure.set :as set]))

(defn parse-line [line]
  (let [[_ v _ _ rate-str] (str/split line #"\s+")
        rate               (-> (str/split rate-str #"=") second drop-last str/join
                               (Integer/parseInt))
        rep                (fn [s] (str/replace s
                                                "tunnel leads to valve "
                                                "tunnels lead to valves "))
        leads-to-str       (-> line rep (str/split #"tunnels lead to valves ") second)
        leads-to           (-> leads-to-str (str/split #", ") set)]
    {:valve v :rate rate :leads-to leads-to}))

(defn parse-input [input]
  (->> (str/split input #"\n")
       (map parse-line)))

(defn build-index [xs attr]
  (reduce (fn [index item] (assoc index (get item attr) item)) {} xs))

(defn build-lookup [xs from to]
  (reduce (fn [lookup item] (assoc lookup (get item from) (get item to))) {} xs))

(defn first-comp [x y]
  (let [c (compare (first x) (first y))]
    (if (not= 0 c)
      c
      (compare (:route (second x)) (:route (second y))))))

;; {:total-flow 0 :minute 0 :current-valve "AA" :on-valves #{}}
(defn flow-comp [x y]
  (let [c (compare (:total-flow x) (:total-flow y))
        c1 (compare (count (:on-valves x)) (count (:on-valves y)))
        c2 (* -1 (compare (:minute x) (:minute y)))
        c3 (compare (:current-valve x) (:current-valve y))]
    (if (= 0 c)
      (if (= 0 c1)
        (if (= 0 c2)
          (if (= 0 c3)
            (compare (vec (:on-valves x)) (vec (:on-valves y)))
            c3)
          c2)
        c1)
      c)))

(defn has-circular-route [{:keys [route]}]
  (< (count (set route)) (count route)))

(defn get-lowest-cost-route [valves-index from to]
  (let [num-valves         (count (keys valves-index))
        first-options      (:leads-to (get valves-index from))
        initial-options    (map (fn [lead-to] [1 {:route [lead-to]}]) first-options)
        initial-work-items (apply sorted-set-by first-comp initial-options)]
    (loop [work-items initial-work-items]
      ;; always taking the lowest cost route from the set
      ;; first so if we find something we're confident
      ;; it's one of the shortest routes (could be ties)
      (if-let [item (first work-items)]
        (let [[cost info] item]
          (if (= (-> info :route last) to)

            {:cost cost :route (:route info)}

            (let [route            (-> info :route)
                  last-item        (last route)
                  last-leads-to    (-> (get valves-index last-item) :leads-to)
                  new-cost         (inc cost)
                  extra-work-items (->> last-leads-to
                                        (map (fn [v] [new-cost {:route (conj route v)}]))
                                        (filter (fn [[_ info]] (not (has-circular-route info)))))
                  updated-work     (disj work-items item)
                  updated-work     (apply conj updated-work extra-work-items)]
              (recur updated-work))))
        (throw (Exception. (str "Could not find route from: " from " to: " to)))))))

;; {:total-flow 0 :minute 0 :current-valve "AA" :on-valves #{}}
;; (map (fn [v] [0 (assoc default-state :current-valve v)])
;; (:leads-to (get valves-index "AA")))
;; (def MAX_TIME 3)
(def MAX_TIME 30)

(defn get-updated-total-flow [valve->rate total-flow on-valves minutes]
  (->> on-valves
       (map (fn [v] (* (valve->rate v) minutes)))
       (reduce +)
       (+ total-flow)))

(defn finalise-rates [{:keys [total-flow minute current-valve on-valves] :as item} valve->rate]
  ;; (println "tf:" total-flow "ov:" on-valves "ms:" (- MAX_TIME minute))
  (-> item
      (assoc :total-flow
             (get-updated-total-flow valve->rate total-flow on-valves (- MAX_TIME minute)))
      (assoc :minute MAX_TIME)))

(defn process-unopened-valve
  [unopened-valve item valves-index valve->rate get-shortest-path]
  (let [{:keys [total-flow minute current-valve on-valves]} item
        from           current-valve]
    (let [to               unopened-valve
          {:keys [cost]}   (get-shortest-path valves-index current-valve to)
          u-minute         (+ minute cost 1)
          u-on-valves      (conj on-valves to)
          u-total-flow     (get-updated-total-flow valve->rate total-flow on-valves (inc cost))]
      (if (<= u-minute MAX_TIME)
        {:total-flow u-total-flow :minute u-minute :current-valve to :on-valves u-on-valves}
        (finalise-rates item valve->rate)))))

(defn get-next-work-items
  "Takes a work item and returns a list of new work-items
  representing the various options from given node or nil
  if all the work has been done"
  [{:keys [total-flow minute current-valve on-valves] :as item}
   non-zero-valves
   valves-index
   valve->rate
   get-shortest-path]

  (if (= (count non-zero-valves) (count on-valves))
    nil
    (let [unopened-valves (set/difference non-zero-valves on-valves)]
      ;; (println "uovs:" unopened-valves)
      (->> unopened-valves
           (map
            (fn [unopened-valve]
              (process-unopened-valve
               unopened-valve item valves-index valve->rate get-shortest-path)))
           (filter #(not (nil? %)))))))

(def default-state {:total-flow 0 :minute 0 :current-valve nil :on-valves #{}})

(defn max-flow-rate [valves-index valve->rate get-shortest-path]
  (let [non-zero-valves (set (map first (filter #(> (second %) 0) valve->rate)))]
    (loop [work-items (sorted-set-by flow-comp (assoc default-state :current-valve "AA"))
           completed  (sorted-set-by flow-comp)]

      (comment
        (doseq [i work-items
                :when (or (= (:on-valves i) #{"DD"}) (= (:on-valves i) #{"DD" "BB"}))]
          (println "m:" (:minute i) "ov:" (:on-valves i) "\tt:" (:total-flow i))))

      (if-let [item (last work-items)]
        (let [next-work-items (get-next-work-items item non-zero-valves valves-index valve->rate get-shortest-path)]
          (if (or (nil? next-work-items) (= MAX_TIME (:minute item)))
            (recur (disj work-items item) (conj completed item))
            (recur (apply conj (disj work-items item) next-work-items) completed)))

        completed))))

(defn part-1 [input]
  (let [valves         (parse-input input)
        valves-index   (build-index valves :valve)
        valve->rate    (build-lookup valves :valve :rate)
        all-flow-rates (max-flow-rate valves-index valve->rate (memoize get-lowest-cost-route))
        final-rates    (map #(finalise-rates % valve->rate) all-flow-rates)
        ans            (-> (sort-by :total-flow final-rates) last)]
    ans))

(comment
  (time
   (let [valves        (parse-input example-input)
         valves-index  (build-index valves :valve)
         lcr           (get-lowest-cost-route valves-index "AA" "HH")
         ans           lcr]
     ans))
  (parse-input example-input)

  (time (part-1 simple-example-input))
  (time (part-1 example-input))
  (time (part-1 (utils/get-input "16"))) ;; => 1792 (22 seconds)

  ;; Perf ideas
  ;; - use keywords instead of strings for valve names (or...are strings interned already?)

  (def simple-example-input (->
                             "
Valve AA has flow rate=0; tunnels lead to valves DD, BB
Valve BB has flow rate=13; tunnels lead to valves AA
Valve DD has flow rate=20; tunnels lead to valves AA"
                             (str/trim)))

  (def example-input (->
                      "
Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
Valve BB has flow rate=13; tunnels lead to valves CC, AA
Valve CC has flow rate=2; tunnels lead to valves DD, BB
Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
Valve EE has flow rate=3; tunnels lead to valves FF, DD
Valve FF has flow rate=0; tunnels lead to valves EE, GG
Valve GG has flow rate=0; tunnels lead to valves FF, HH
Valve HH has flow rate=22; tunnel leads to valve GG
Valve II has flow rate=0; tunnels lead to valves AA, JJ
Valve JJ has flow rate=21; tunnel leads to valve II"
                      (str/trim)))
  ;;
  (apply sorted-set-by flow-comp
         [(-> default-state
              (assoc :total-flow 1)
              (assoc :minute 1)
              (assoc :current-valve "BB")
              (assoc :on-valves #{"AA"}))
          (-> default-state
              (assoc :total-flow 1)
              (assoc :minute 1)
              (assoc :current-valve "BB")
              (assoc :on-valves #{"AA" "BB"}))]))
