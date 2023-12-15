(ns aoc.day-15
  (:require [aoc.utils :as u]
            [clojure.string :as str]))

(def example (str/trim "rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7"))
(def input (str/trim (u/get-input 15)))

(defn parse-input [input]
  (str/split input #","))

;; (def hash-c (comp #(rem % 256) #(* 17 %) int))
(defn hash-fn [s] (reduce (fn [hash-val c] (-> c int (+ hash-val) (* 17) (rem 256))) 0 s))

(defn pt1 [input]
  (let [parsed  (parse-input input)
        hashes  (map hash-fn parsed)
        ans     (u/sum hashes)]
    ans))

(defn- arrange-lenses [all-instructions]
  (loop [instructions all-instructions
         boxes        {}]
    (if (empty? instructions)
      boxes
      (let [i      (first instructions)
            label  (apply str (take-while #(not (#{\- \= "-" "="} %)) i)) ;; (subs i 0 2)
            box    (hash-fn label)
            op     (nth i (count label))
            lens   (if (= \= op) (nth i (inc (count label))) nil)
            uboxes (update boxes box
                           (fn [lenses]
                             (cond (and (= \= op) (nil? lenses))
                                   [[label lens]]

                                   (= \= op)
                                   (if (> (count (filter (fn [[l]] (= l label)) lenses)) 0)
                                     (reduce (fn [all [lbl lns]]
                                               (if (= lbl label)
                                                 (conj all [label lens])
                                                 (conj all [lbl lns]))) [] lenses)
                                     (conj lenses [label lens]))

                                   (= \- op)
                                   (filterv (fn [[l _]] (not= label l)) lenses)

                                   :else
                                   (throw (Exception.
                                           (str "Unexpected...i: '" i
                                                "' op: '" op
                                                "' lens: '" lens
                                                "' label: '" label "'"))))))]
        (recur (rest instructions) uboxes)))))

;; (update {1 [:x]} 1 (fn [l] (if (nil? l) [:x] (conj l :y))))

(defn pt2 [input]
  (let [parsed     (parse-input input)
        boxes      (arrange-lenses parsed)
        p          #(u/parse-int (str %))
        box-scores (map (fn [[n lenses]]
                          (println "n:" n "lenses:" lenses)
                          (->> lenses
                               (map-indexed (fn [idx [_ lens-char]] (* (inc n)
                                                                       (inc idx)
                                                                       (p lens-char))))
                               u/sum)) boxes)
        ans (u/sum box-scores)]
    ans))

;; hash the first part (so hash rn in rn=1) => box number
;;   - => remove lens rn from box and move all remaninging ones "forward" (you mean backwards, surely?) filling the space vacated (no lens, noop)
;;   = => next number is focal length (1 in rn=1), if lens exists, replace it

(comment
  (pt1 example)
  (pt1 input) ;; 511257
  ;; {0 [["rn" \1] ["cm" \2]], 1 [], 3 [["ot" \7] ["ab" \5] ["pc" \6]]}
  (pt2 example)
  (time (pt2 input));; 239484

    ;; (hash-c \H)
  (hash-fn "HASH")
  (hash-fn "qp")
  (u/parse-int (str \9))

  (map int "foo")
;
  )
