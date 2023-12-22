(ns aoc.day-19
  (:require [aoc.utils :as u]
            [clojure.string :as str]
            [clojure.math.combinatorics :refer [combinations]]))

;; bug: need to take into account conditions before the one
;;      being parsed to know the true range

(def example (str/trim "
px{a<2006:qkq,m>2090:A,rfg}
pv{a>1716:R,A}
lnx{m>1548:A,A}
rfg{s<537:gd,x>2440:R,A}
qs{s>3448:A,lnx}
qkq{x<1416:A,crn}
crn{x>2662:A,R}
in{s<1351:px,qqz}
qqz{s>2770:qs,m<1801:hdj,R}
gd{a>3333:R,R}
hdj{m>838:A,pv}

{x=787,m=2655,a=1222,s=2876}
{x=1679,m=44,a=2067,s=496}
{x=2036,m=264,a=79,s=2244}
{x=2461,m=1339,a=466,s=291}
{x=2127,m=1623,a=2188,s=1013}"))

(def input (u/get-input 19))

;; inclusive
(defrecord Range [from to])

(defn overlaps? [r1 r2]
  (and (<= (:from r1) (:to r2))
       (>= (:to r1) (:from r2))
       (<= (:from r2) (:to r1))
       (>= (:to r2) (:from r1))))

(defn range-overlap [r1 r2]
  ;; ---x-----y---
  ;; -x----y------
  ;; ------x----y-
  ;; -x---------y-
  ;; ---- x-y-----
  (let [from (max (:from r1) (:from r2))
        to   (min (:to r1) (:to r2))]
    (if (overlaps? r1 r2)
      (->Range from to)
      (->Range 0 0))))

(comment
  (range-overlap (->Range 0 10) (->Range 10 15))
  (range-overlap (->Range 0 10) (->Range 5 10))
  (range-overlap (->Range 0 10) (->Range 11 20))
  (range-overlap (->Range 1 3) (->Range 2 5))
  (range-overlap (->Range 11 20) (->Range 2 5)))


(defn- parse-condition [condition-str]
  (let [spy false
        [_ v f-str n-str] (re-matches #"(.)(.)(.+)" condition-str)
        n (u/parse-int n-str)]
    (assert (#{">" "<"} f-str) (str "Untrusted input: '" f-str "', only '>' or '<' allowed"))
    (fn [arg]
      (let [ans ((eval (symbol f-str)) (get arg v) n)]
        (when spy
          (println "arg:" arg "f-str:" f-str "v:" v "n:" n "ans:" ans))
        ans))))

(comment ((parse-condition "x>2345") {"x" 2345})
         ((parse-condition "x>2345") {"x" 2346}))

(defn- condition-range [segment-str]
  (let [[_ v f-str n-str] (re-matches #"(.)(.)(.+)" (-> (str/split segment-str #":") first))
        n (u/parse-int n-str)
        r (cond (= "<" f-str)
                (->Range 1 (dec n))
                (= ">" f-str)
                (->Range (inc n) 4000)
                :else (throw (Exception. "No")))]
    {:key (keyword v) :range r}))

(comment (condition-range "A")
         (condition-range "R")
         (condition-range "s<1351:px"))

(defn- parse-condition-segment [segment-str]
  (let [contains-condition (.contains segment-str ":")]
    {:target       (if contains-condition
                     (-> (str/split segment-str #":") second)
                     segment-str)
     :condition-fn (if contains-condition
                     (-> (str/split segment-str #":")
                         first
                         parse-condition)
                     (fn [_] true))
     :range (if contains-condition (condition-range segment-str) {:key :all :range (->Range 1 4000)})}))

(defn- parse-rule [rule-str]
  (let [[_ name rules-strs] (re-matches #"^(\w+)\{(.+)\}" rule-str)
        conditions          (->> (str/split rules-strs #",")
                                 (mapv parse-condition-segment))]
    {:name       name
     :conditions conditions}))

(comment (parse-rule "rfg{s<537:gd,x>2440:R,A}"))

(defn- parse-part [part-str]
  (reduce (fn [acc item] (let [[k v] (str/split item #"=")] (assoc acc k (u/parse-int v))))
          {}
          (-> (re-matches #"^\{(.+)\}$" part-str)
              second
              (str/split  #","))))

(comment (parse-part "{x=787,m=2655,a=1222,s=2876}"))

(defn parse-input [input]
  (let [[rules-blob parts-blob] (str/split input #"\n\n")
        rules      (->> (str/split-lines rules-blob)
                        (mapv parse-rule))
        parts (->> (str/split-lines parts-blob)
                         (mapv parse-part))]
    {:rules (u/build-unique-index rules :name) :parts parts}))

(defn score [parts]
  (u/sum (mapcat vals parts)))

(defn process-part [rules part]
  (loop [rule  "in"
         steps 0]
    (when (> steps 20)
      (throw (Exception. "You ran out of steps, buddy")))

    (let [next-rule (->> (get rules rule)
                           :conditions
                           (reduce (fn [target condition]
                                     ;; (println "p:" part "c:" condition)
                                     (if (and (nil? target) ((:condition-fn condition) part))
                                       (:target condition)
                                       target))
                                   nil))]
      ;; (println "nr:" next-rule)
      (if (#{"A" "R"} next-rule)
        next-rule
        (recur next-rule (inc steps))))))

(defn pt1 [input]
  (println "---- Pt1 ----")
  (let [{:keys [rules parts] :as parsed} (parse-input input)
        {:strs [A R]} (group-by (partial process-part rules) parts)
        ans  (score A)
        ;; ans {:a  A :r R }
        ]
    ans))

(defn paths-to-A-ii [rules]
  (loop [work  [[{:target "in" :range {:key :all :range (->Range 1 4000)}}]]
         paths []
         steps 0]
    (when (> steps 200)
      (throw (Exception. "You ran out of steps in paths-to-A-ii, buddy")))
    (if (empty? work)
      (filterv #(= "A" (:target (last %))) paths)
      (let [updated-work (->> work
                              (mapv (fn [wi]
                                      (let [target       (:target (last wi))
                                            rule         (get rules target)
                                            next-targets (mapv (fn [{:keys [target range]}]
                                                                 ;; todo: need the ranges of
                                                                 ;; all before this point too
                                                                 {:target target :range range})
                                                               (:conditions rule))]
                                        (mapv #(conj wi %) next-targets))))
                              (apply concat)
                              (into []))
            completed    (filterv #(#{"A" "R"} (:target (last %))) updated-work)
            incomplete   (filterv #(not (#{"A" "R"} (:target (last %)))) updated-work)]
        (recur incomplete (into paths completed) (inc steps))))))

(defn- reduce-range
  ([path]
   (let [full-range (->Range 1 4000)
         initial {:x full-range :m full-range :a full-range :s full-range}]
     (reduce-range path initial)))
  ([path initial]
   (reduce
      (fn [acc-range {:keys [target range]}]
        (case (:key range)
          :all acc-range ;; assume unchanged
          (assoc acc-range (:key range) (range-overlap (:range range) (get acc-range (:key range))))))
      initial
      path)
   ))

(comment (let [x :hex] (case x :hey :there :foo)))

(defn- double-count-amount [[r1 r2]]
  ;; (clojure.pprint/pprint { :r1  r1  :r2  r2 })
  (->> {:x (range-overlap (:x r1) (:x r2))
        :m (range-overlap (:m r1) (:m r2))
        :a (range-overlap (:a r1) (:a r2))
        :s (range-overlap (:s r1) (:s r2))}
       vals
       (map (fn [{:keys [from to]}] (if (and (not= 0 from) (not= 0 to)) (inc (- to from)) 0)))
       (reduce * 1)))

(defn pt2 [input]
  (println "---- Pt2 ----")
  (let [{:keys [rules]} (parse-input input)
        paths  (paths-to-A-ii rules)
        ranges (map reduce-range paths)
        all-combinations (->> ranges
                              (map #(apply * (map (fn [{:keys [from to]}]
                                                    (if (and (not= 0 from) (not= 0 to))
                                                      (inc (- to from))
                                                      0)) (vals %))))
                              u/sum)
        double-counts    (->> (combinations ranges 2)
                              (map double-count-amount)
                              u/sum)
        _ (doseq [p paths] (clojure.pprint/pprint (map :target p)))
        _ (clojure.pprint/pprint ranges)
        ans    paths
        ans    ranges
        ans    rules]
    {:ans (- all-combinations double-counts) :all all-combinations :dbl double-counts}))

(comment
  ;; x,m,a,s [1,4000]
  ;; 256000000000000 (* 4000 4000 4000 4000)
  ;; 256661886000000 all-combos (including double counting)
  ;;  95157656700000 double-counts
  ;; 496534091000000 all
  ;; 421012480808000 double
  ;;  75521610192000 ans
  ;; 167409079868000 correct
  (pt2 example)

  ;; paths
  [[{:target "in", :range {:key :all, :range #aoc.day_19.Range{:from 1, :to 4000}}}
    {:target "px", :range {:key :s, :range #aoc.day_19.Range{:from 1, :to 1350}}}
    {:target "qkq", :range {:key :a, :range #aoc.day_19.Range{:from 1, :to 2005}}}
    {:target "crn", :range {:key :all, :range #aoc.day_19.Range{:from 1, :to 4000}}}
    {:target "A", :range {:key :x, :range #aoc.day_19.Range{:from 2663, :to 4000}}}]
   ["..."]]

  (pt2 input)

  (combinations [:a :b :c :d] 2)
  (pt1 example) ;; 19114
  (pt1 input) ;; 446935

  (defn pt2-debug []
  (println "---- Pt2 debug ----")
  (let [{:keys [rules]} (parse-input input)
        paths  [[{:range {:key :x :range (->Range 1 3)}}
                 {:range {:key :m :range (->Range 1 3)}}
                 {:range {:key :a :range (->Range 1 3)}}
                 {:range {:key :s :range (->Range 1 3)}}]
                [{:range {:key :x :range (->Range 1 1)}}]]

        paths  [[{:range {:key :x :range (->Range 1 2)}}
                 {:range {:key :m :range (->Range 1 2)}}
                 {:range {:key :a :range (->Range 1 2)}}
                 {:range {:key :s :range (->Range 1 2)}}]
                [{:range {:key :x :range (->Range 1 2)}}
                 {:range {:key :m :range (->Range 1 2)}}
                 {:range {:key :a :range (->Range 1 3)}}
                 {:range {:key :s :range (->Range 1 2)}}]]

        ranges (map #(reduce-range % {:x (->Range 1 3)
                                      :m (->Range 1 3)
                                      :a (->Range 1 3)
                                      :s (->Range 1 3)}) paths)
        all-combinations (->> ranges
                              (map #(apply * (map (fn [{:keys [from to]}]
                                                    (if (and (not= 0 from) (not= 0 to))
                                                      (inc (- to from))
                                                      0)) (vals %))))
                              u/sum)
        double-counts    (->> (combinations ranges 2)
                              (map double-count-amount)
                              u/sum)
        ans (- all-combinations double-counts)]
    {:ans ans :all all-combinations :dbl double-counts}))


  (partition-by even? [2 2 2 2])
  (split-with even? [2 2 3 2 2 5 6])

  {:rules {"px" {:name "px", :conditions [{:target "qkq", :condition-fn "..." :range {:key :a :range {:from 1 :to 2005}}}
                                          {:target "A", :condition-fn "..."}
                                          {:target "rfg", :condition-fn "..."}]}
           "lnx" {:name "lnx", :conditions [{:target "A", :condition-fn "..."} "..."]}},
   :parts  [{"x" 787, "m" 2655, "a" 1222, "s" 2876}
            {"x" 1679, "m" 44, "a" 2067, "s" 496}
            {"x" 2036, "m" 264, "a" 79, "s" 2244}
            {"x" 2461, "m" 1339, "a" 466, "s" 291}
            {"x" 2127, "m" 1623, "a" 2188, "s" 1013}]}
;
)
