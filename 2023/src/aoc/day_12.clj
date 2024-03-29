(ns aoc.day-12
  (:require [aoc.utils :as u]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as str]
            [clojure.math.combinatorics :as prof-b]))

(def example (str/trim "
???.### 1,1,3
.??..??...?##. 1,1,3
?#?#?#?#?#?#?#? 1,3,1,6
????.#...#... 4,1,1
????.######..#####. 1,6,5
?###???????? 3,2,1"))

(def input (u/get-input 12))

(defn parse-input [input]
  (let [lines (str/split-lines input)
        parse-line (fn [l] (let [[template groups] (str/split l #"\s")]
                             {:template template :groups (->> (str/split groups #",")
                                                              (mapv u/parse-int))}))]
    (mapv parse-line lines)))

(defn compatible-with-groups? [template groups]
  (= groups
     (->> (str/split template #"")
          (partition-by #(= "." %))
          (filter #(= "#" (first %)))
          (map count))))

(comment
  (prof-b/partitions [:a :b :c])
  (prof-b/subsets [:a :b :c])

  (prof-b/permutations [:a :b :c])

  (prof-b/combinations [:a :b :c] 2)
  (prof-b/selections [:a :b :c] 2))



(defn- increment-all [dot-config]
  (mapv (fn [i dots] (update dots i inc)) (range) (repeat (count dot-config) (vec dot-config))))

(comment (increment-all [0 1 0])
         (vec (mapcat increment-all [[0 1 0]])))

(defn- dot-config->template [groups dot-config]
  (let [dots   (mapv (fn [num-dots] (repeat num-dots ".")) dot-config)
        hashes (mapv (fn [group-size] (repeat group-size "#")) groups)]
    (-> (interleave dots (conj hashes []))
        flatten
        str/join)))

(comment
  (interleave [[] ["."] ["."] []] [["#"] ["#"] ["#" "#" "#"] []])
  (dot-config->template [1 1 3] [0 1 1 0])
  (dot-config->template [1 1 3] [2 3 2 2]))

(defn get-ways-to-make-groups [template groups]
  (let [template-length         (count template)
        num-dots                (- template-length (u/sum groups))
        ;; need at least one dot separating groups
        num-moveable-dots       (- num-dots (dec (count groups)))
        starting-dot-config     (as-> [0] $
                                  (into $ (repeat (dec (count groups)) 1))
                                  (conj $ 0))
        ;; moveable dots go either before, between or after
        ;; conceptually: [dot resevoir] g1 [dot reservoir] g2 ... gn
        ;; dots: [0 1 1 0] equivalent to [[] g1 [.] g2 [.] g3 []]
        ;; num ways:
        ;;                           g g g
        ;; | is a group separator   ..|.|.. , num separators = num-groups - 1
        ;; . is a moveable dot (there are dots which can't be moved,
        ;;                      imagine attached to the right of group)
        dot-configs
        (loop [rem-dots num-moveable-dots
               dots     [starting-dot-config]]
          (if (= 0 rem-dots)
            dots
            (let [next-configs (vec (set (mapcat increment-all dots)))]
              (recur (dec rem-dots) next-configs))))]
    (mapv (partial dot-config->template groups) (set dot-configs))))

(comment
  (get-ways-to-make-groups ".??.??.###" [1 1 3]))

(defn- way-matches-template? [template way]
  (assert (= (count template) (count way)) (str "Nope. T: " (count template) " W: " (count way)))
  (let [valid-pair? (fn [[template-char way-char]]
                      (if (= \? template-char) true (= way-char template-char)))]
    (every? valid-pair? (map vector template way))))

(comment (way-matches-template? "?.?#" "..##")
         (way-matches-template? "?.?#" "#.##")
         (way-matches-template? "?.?#" "#.#."))

(defn old-num-ways-per-line [{:keys [template groups] :as line}]
  (let [start               (System/nanoTime)
        ways-to-make-groups (get-ways-to-make-groups template groups)
        valid-ways          (filterv (partial way-matches-template? template) ways-to-make-groups)
        num-valid-ways      (count valid-ways)
        end                 (System/nanoTime)
        time-ms             (/ (- end start) 1e6)]

    (when (> time-ms 1000)
      (pprint {:time-ms time-ms
               :line line
               :num-ways-to-make-groups (count ways-to-make-groups)
               :num-valid-ways num-valid-ways}))

    num-valid-ways))

(comment (old-num-ways-per-line {:template "??.??.###" :groups [1 1 3]})
         (old-num-ways-per-line {:template ".??..??...?##." :groups [1 1 3]})
         (time (old-num-ways-per-line {:template "??#????#..?.?.?", :groups [3 2 1 1 1]}))
         (time (old-num-ways-per-line {:template "??#??..#??.", :groups [3 1]}))
         (time (old-num-ways-per-line {:template ".?????.??.???.?#??", :groups [1 1 3]}))
         )

;; ways   (map num-ways-per-line (take 10 (drop 275 parsed)))
(defn pt1 [input]
  (println "--- Pt1 ---")
  (let [parsed (parse-input input)
        ways   (mapv old-num-ways-per-line parsed)
        ;; ways   (map num-ways-per-line (take 700 parsed))
        ans    (u/sum ways)]
    ans))


(defn repeat-data [num-repeats line]
  (-> line
      (update :template (fn [template] (str/join "?" (repeat num-repeats template))))
      (update :groups (fn [groups] (->> groups (repeat num-repeats) flatten vec)))))


;; The max number of ways to make the groups is num-question-marks choose num-moveable-dots
;; but only some of them will be valid groups?

(defn- template->actual [template qm-dot-idxs]
  (let [qm-dot-idxs-set (set qm-dot-idxs)

        ans
        (loop [rem-template     (vec template)
               normalised-?-idx 0
               actual           []]
          (if (empty? rem-template)
            (str/join actual)
            (let [t (first rem-template)
                  a (if (= \? t)
                      (if (contains? qm-dot-idxs-set normalised-?-idx)
                        \.
                        \#)
                      t)]
              (recur (rest rem-template)
                     (if (= \? t) (inc normalised-?-idx) normalised-?-idx)
                     (conj actual a)))))]
    (str/join ans)))

(comment (template->actual "??.??.###" [2 3]))

(defn- valid-arrangement? [template groups arrangement]
  (and (way-matches-template? template arrangement)
       (= (->> (str/split arrangement #"\.+")
               (filterv seq)
               (mapv count))
          groups)))

(defn num-ways-per-line [{:keys [template groups]}]
  (let [template-length       (count template)
        num-question-marks    (count (filterv #(= \? %) template))
        num-template-dots     (count (filterv #(= \. %) template))
        num-required-dots     (- template-length (u/sum groups))
        num-ambiguous-dots    (- num-required-dots num-template-dots)
        ;; if template has four ? => ??.??.### 1,1,3
        ;; num ambiguous dots = 2
        ;; combos is which to make a dot assuming zero indexed list of ? ????
        ;; => ((0 1) (0 2) (0 3) (1 2) (1 3) (2 3))
        qm-dot-idx-combos     (prof-b/combinations (range num-question-marks) num-ambiguous-dots)

        possible-arrangements (->> qm-dot-idx-combos
                                   (mapv (partial template->actual template)))
        valid    (filterv (partial valid-arrangement? template groups) possible-arrangements)]

    (count valid)))

(comment (prof-b/combinations (range 4) 2))

(defn pt2 [input]
  (println "--- Pt2 ---")
  (let [parsed      (parse-input input)
        ;; num-repeats 1
        ;; bigger-data (mapv (partial repeat-data num-repeats) parsed)
        ways        (map num-ways-per-line parsed)
        ans         (u/sum ways)]
    ans))

(comment
  (time (pt2 example))
  (time (pt2 input))
  (time (pt1 example))
  (time (pt1 input))
  ;; pt1 ans 7792

  ;; Observations:
  ;; - Techniques that just use the template or just use the groups to
  ;;   enumerate options have pathalogical data that kill their performance
  ;;   => need a solution that takes info of both into account


  ;; Pathological cases
  ;; ?????...  1,1
  ;; ?#?#?#    1,1,1


  ;;
  {:time-ms 2982.466057,
 :line {:template ".?????.??.???.?#??", :groups [1 1 3]},
 :num-ways-to-make-groups 364,
 :num-valid-ways 76}
{:time-ms 1298.929591,
 :line {:template ".?.??.?.#?#?#.????..", :groups [1 5]},
 :num-ways-to-make-groups 105,
 :num-valid-ways 4}
{:time-ms 11827.337391,
 :line {:template ".....??###??..???.??", :groups [3 1]},
 :num-ways-to-make-groups 136,
 :num-valid-ways 6}
{:time-ms 1194.717369,
 :line {:template "??#??.???..?.?.???", :groups [2 2]},
 :num-ways-to-make-groups 105,
 :num-valid-ways 8}

  ;;
  {:time-ms 2836.041536,
   :line {:template ".?????.??.???.?#??", :groups [1 1 3]}}
  {:time-ms 1253.068307,
   :line {:template ".?.??.?.#?#?#.????..", :groups [1 5]}}
  {:time-ms 12040.617259,
   :line {:template ".....??###??..???.??", :groups [3 1]}}
  {:time-ms 1191.902384,
   :line {:template "??#??.???..?.?.???", :groups [2 2]}}
  {:time-ms 3986.236152,
   :line {:template "...??.?.?#.?????.", :groups [1 1]}}

      ;; (println "--")
    ;; (pprint {:template template :groups groups :num-question-marks num-question-marks
             ;; :qm-dot-idx-combos qm-dot-idx-combos
             ;; :possible-arrangements possible-arrangements
             ;; })
    ;; (println :num-template-dots num-template-dots :num-required-dots num-required-dots :num-ambiguous-dots num-ambiguous-dots)
    ;; (println :valid (count valid))

;
  )
