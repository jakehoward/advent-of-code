(ns org.jakehoward.aoc.days.day-16
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as str]))

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

(defn part-1 [input]
  (let [valves        (parse-input input)
        valves-index  (build-index valves :valve)
        valve->rate   (build-lookup valves :valve :rate)
        ans           valves-index]
    ans))

(comment
  (time
   (let [valves        (parse-input example-input)
         valves-index  (build-index valves :valve)
         lcr           (get-lowest-cost-route valves-index "AA" "HH")
         ans           lcr]
     ans))

  (part-1 example-input)
  (part-1 (utils/get-input "16"))


  ;; Perf ideas
  ;; - use keywords instead of strings for valve names (or...are strings interned already?)

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
  )
