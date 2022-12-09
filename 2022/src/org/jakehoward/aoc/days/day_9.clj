(ns org.jakehoward.aoc.days.day-9
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as str]))

(defn- parse-input-line [line]
  (let [[direction num] (str/split line #"\s+")]
    (repeat (Integer/parseInt num) (keyword direction))))

(defn input->instructions
  "Turn the raw input string into a list of single
  instructions. E.g. U 4 => U U U U, etc"
  [input]
  (->> (utils/lines input)
       (mapcat parse-input-line)))

(defn up [[x y]]
  [x (inc y)])

(defn down [[x y]]
  [x (dec y)])

(defn left [[x y]]
  [(dec x) y])

(defn right [[x y]]
  [(inc x) y])

(defn update-x [[x y] n]
  [(+ x n) y])

(defn update-y [[x y] n]
  [x (+ y n)])

(def instruction->update {:U up :D down :L left :R right})

(defn- move-head [head instruction]
  ((instruction->update instruction) head))

(defn- move-tail [tail head]
  (let [[hx hy] head
        [tx ty] tail
        dx (- hx tx)
        dy (- hy ty)]
    (cond
      (= 2  dx) (-> (right tail) (update-y dy))
      (= -2 dx) (-> (left tail)  (update-y dy))
      (= 2  dy) (-> (up tail)    (update-x dx))
      (= -2 dy) (-> (down tail)  (update-x dx))
      :else     tail)))

(defn process-instructions
  "Go over each instruction and return a list of
  co-ordinates that head and tail have visited.
  Uses the kind of cartesian co-ord system you may
  be familiar with from school (up, right are +ve)"
  [initial-instructions]

  (loop [head-coords  [[0 0]]
         tail-coords  [[0 0]]
         instructions initial-instructions]
    (if-let [instruction (first instructions)]
      (let [head      (last head-coords)
            tail      (last tail-coords)
            new-head  (move-head head instruction)
            new-tail  (move-tail tail new-head)
            _         (comment (println "Head:" head
                                        "Tail:" tail
                                        "New head:" new-head
                                        "New tail:" new-tail))]
        (recur (conj head-coords new-head)
               (conj tail-coords new-tail)
               (rest instructions)))
      {:head-coords head-coords :tail-coords tail-coords})))

(defn part-1 [input]
  (let [instructions                      (input->instructions input)
        {:keys [head-coords tail-coords]} (process-instructions instructions)
        ans tail-coords]
    (count (set tail-coords))
    ;; tail-coords
    ))

(comment
  (part-1 example-data)
  (part-1 (utils/get-input 9)) ;; => 6391

  (input->instructions example-data)
  (count (input->instructions (utils/get-input 9)))
  (def example-data "R 4
U 4
L 3
D 1
R 4
D 1
L 5
R 2"))
