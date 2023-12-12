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


(defn pt1 [input]
  (let [parsed (parse-input input)
        ways   (map ways-per-line parsed)
        ans    (map count ways)]
    (u/sum ans)))

(defn pt2 [input]
  (let [parsed (parse-input input)
        ans    parsed]
    ans))

(comment
  (pt1 example)
  (pt1 input) ;; 7792
  (pt2 example)
  (pt2 input)

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
