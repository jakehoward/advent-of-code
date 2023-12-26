(ns aoc.day-12
  (:require [aoc.utils :as u]
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

(defn og-ways-per-line [{:keys [template groups]}]
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
        dot-configs
        (loop [rem-dots num-moveable-dots
               dots     [starting-dot-config]]
          (if (= 0 rem-dots)
            dots
            (let [next-configs (vec (mapcat increment-all dots))]
              (recur (dec rem-dots) next-configs))))]
    (mapv (partial dot-config->template groups) dot-configs)))

(comment
  (get-ways-to-make-groups ".??.??.###" [1 1 3]))

(defn- way-matches-template [template way]
  (assert (= (count template) (count way)) (str "Nope. T: " (count template) " W: " (count way)))
  (let [valid-pair? (fn [[template-char way-char]]
                      (if (= \? template-char) true (= way-char template-char)))]
    (every? valid-pair? (map vector template way))))

(comment (way-matches-template "?.?#" "..##")
         (way-matches-template "?.?#" "#.##")
         (way-matches-template "?.?#" "#.#."))

(defn num-ways-per-line [{:keys [template groups]}]
  (let [ways-to-make-groups (get-ways-to-make-groups template groups)
        valid-ways          (filterv (partial way-matches-template template) ways-to-make-groups)]
    (count valid-ways)))

(defn pt1 [input]
  (let [parsed (parse-input input)
        ways   (map og-ways-per-line parsed)
        ans    (map count ways)]
    (u/sum ans)))

(defn pt2 [input]
  (let [parsed (parse-input input)
        ways   (map num-ways-per-line parsed)
        ans    (u/sum ways)]
    ans))

(comment
  (time (pt2 example))
  (time (pt2 input))
  (time (pt1 example))
  (time (pt1 input)) ;; 7792
;
  )
