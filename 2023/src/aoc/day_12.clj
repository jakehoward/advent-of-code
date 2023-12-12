(ns aoc.day-12
  (:require [aoc.utils :as u]
            [clojure.string :as str]))

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

(defn ways-per-line [{:keys [template groups]}]
  (loop [templates [template]
         candidates []
         steps      0]
    (if (or (> steps 5000000) (empty? templates))

      (if (> steps 5000000) :failed (filter #(compatible-with-groups? % groups) candidates))

      (let [t       (first templates)
            next-ts (mapv #(str/replace-first t "?" %) ["." "#"])
            grouped (group-by #(.contains % "?") next-ts)
            [baking ready] [(get grouped true) (get grouped false)]]

        (recur (into (rest templates) baking)
               (into candidates ready)
               (inc steps))))))

(defn a-faster-ways-per-line [{:keys [template groups]}]
  (let [groups-as-template (str/join "." (map #(str/join "" (repeat % "#")) groups))
        num-dots           (- (count template) (count groups-as-template))]
    ;; dots can go at the beginning, end or where existing dots are
    ;; only valid if existing dots and hashes line up

    ;; use as many dots as you need to get existing dots and hashes to line up
    ;; then any ### ??? groups are "how many arrangements" problems
    [template groups-as-template num-dots]))

(defn is-possible? [half-baked groups]
  (let [half-baked-groups (->> (str/split half-baked #"[^#?]+")
                               (filter seq))
        ;; first-group       (first half-baked-groups)
        ]

    ;; false negative, second group might be first
    ;; if first is only question marks
    ;; (<= (count (filter #(= \# %) first-group))
        ;; (first groups)
    ;; (count first-group))

    ;; false negative #?#? counts as one group
    ;; but it could be 2
    (>= (count half-baked-groups) (count groups))))

(comment
  (str/split "..##?.?.#.##...." #"[^#?]+")
  (is-possible? "..##?.?##.." [4, 1, 2]))

(defn faster-ways-per-line [{:keys [template groups]}]
  (let [max-steps 50000]
    (loop [templates [template]
           candidates []
           steps      0
           pruned     0]
      (if (or (> steps max-steps) (empty? templates))

        (if (> steps max-steps)
          :failed
          {:ways       (filter #(compatible-with-groups? % groups) candidates)
           :num-steps  steps
           :num-pruned pruned})

        (let [t       (first templates)
              next-ts (mapv #(str/replace-first t "?" %) ["." "#"])
              grouped (group-by #(.contains % "?") next-ts)
              [baking ready] [(get grouped true) (get grouped false)]
              possible-baking (filter #(is-possible? % groups) baking)]

          (recur (into (rest templates) possible-baking)
                 (into candidates ready)
                 (inc steps)
                 (+ pruned (- (count baking) (count possible-baking)))))))))


(defn pt1 [input]
  (let [parsed (parse-input input)
        ways   (map ways-per-line parsed)
        ans    (map count ways)]
    (u/sum ans)))

(defn faster-pt1 [input]
  (let [parsed (parse-input input)
        ways   (map faster-ways-per-line parsed)
        ans    (map #(count (:ways %)) ways)]
    (u/sum ans)))

(defn pt2 [input]
  (let [parsed (parse-input input)
        ways   (map faster-ways-per-line parsed)
        ;; ans    (map count ways)
        ans ways
        ]
    ans))


(comment
  (pt2 example)

  (["???.###"
    "#.#.###" 0]

   [".??..??...?##."
    ".#...#....###." 5]

   ["?#?#?#?#?#?#?#?"
    ".#.###.#.######" 1]

   ["????.#...#..."
    "####.#.#" 5]

   ["????.######..#####."
    "#.######.#####" 5]

   ["?###????????"
    "###.##.#" 4])

  (pt2 input)
  (time (pt1 example))
  (time (faster-pt1 example))
  (time (pt1 input))
  (time (faster-pt1 input))
  (pt1 input) ;; 7792

  (ways-per-line {:template "???.###" :groups [1,1,3]})
  (do
    (println "--")
    (ways-per-line {:template "???..#" :groups [1,1,3]}))

  (do
    (println "--")
    (time
     (ways-per-line
      {:template (str/join "?" (repeat 5 "????.######..#####."))
       :groups (flatten (repeat 5 [1,6,5]))})))

  (str/replace-first ".#??#." "?" ".")
  (split-with #(.contains % "?") ["asd" "asde?"])

  (->> (str/split "..###.#..#" #"")
       (partition-by #(= "." %))
       (filter #(= "#" (first %)))
       (map count))

  (get (group-by #(.contains % "?") ["asde?"]) false)
  
;
)
