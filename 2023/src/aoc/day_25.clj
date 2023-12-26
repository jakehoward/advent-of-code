(ns aoc.day-25
  (:require [aoc.utils :as u]
            [clojure.string :as str]))

(def example (str/trim "
jqt: rhn xhk nvd
rsh: frs pzl lsr
xhk: hfx
cmg: qnr nvd lhk bvb
rhn: xhk bvb hfx
bvb: xhk hfx
pzl: lsr hfx nvd
qnr: nvd
ntq: jqt hfx bvb xhk
nvd: lhk
lsr: lhk
rzs: qnr cmg lsr rsh
frs: qnr lhk lsr"))

;; hfx/pzl
;; bvb/cmg
;; nvd/jqt

(defn to-graphviz [g]
  (str "graph {"
       (str/join "\n" (map (fn [[id cs]] (str id " -- {" (str/join "," cs) "}")) g))
       "}"))

(def input (u/get-input 25))

(defn parse-line [line]
  (let [[cpnt connects-to-str] (str/split line #": ")
        connects-to (str/split connects-to-str #"\s+")]
    {:id cpnt :connects-to (set connects-to)}))

(defn parse-input [input]
  (mapv parse-line (str/split-lines input)))

(defn build-graph-single [nodes]
  (reduce (fn [graph node]
                           (->> (:connects-to node)
                                (into (or (get graph (:id node)) #{}))
                                (assoc graph (:id node)))) {} nodes))

(defn build-graph [nodes]
  (let [graph    (build-graph-single nodes)
        conn->id (mapcat (fn [{:keys [id connects-to]}] (map (fn [c] [c id]) connects-to)) nodes)
        graph    (reduce (fn [graph [c id]]
                           (assoc graph c (conj (or (get graph c) #{}) id))) graph conn->id)]
    graph))

(defn pt1 [input]
  (let [parsed (parse-input input)
        ans    (build-graph parsed)]
    ans))

(defn pt2 [input]
  (let [parsed (parse-input input)
        ans    parsed]
    ans))

(defn num-conns [graph]
  (/ (u/sum (map count (vals graph))) 2))

(comment
  (pt1 example)
  (pt1 input)
  (pt2 example)
  (pt2 input)

  ;; 6,458,620,620 ways to cut three edges (3385 choose 3)
  (->> (nth [example input] 1)
       parse-input
       build-graph
       num-conns)
  
  (->> (nth [example input] 0)
       parse-input
       build-graph-single
       to-graphviz
       (spit "day-25-ex.dot"))
;
)
