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
  ([initial-instructions] (process-instructions initial-instructions [0 0] [0 0]))
  ([initial-instructions initial-head initial-tail]

   (loop [head-coords  [initial-head]
          tail-coords  [initial-tail]
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
       {:head-coords head-coords :tail-coords tail-coords}))))

(defn follow-head
  "Given a list of head-coords, use them to calc tail position"
  ([initial-head-coords]

   (loop [head-coords  initial-head-coords
          tail-coords  [[0 0]]]
     (if-let [head (first head-coords)]
       (let [tail      (last tail-coords)
             new-tail  (move-tail tail head)
             _         (comment (println "Head:" next-head
                                         "Tail:" tail
                                         "New tail:" new-tail))]
         (recur (rest head-coords)
                (conj tail-coords new-tail)))
       tail-coords))))

(defn part-1 [input]
  (let [instructions                      (input->instructions input)
        {:keys [head-coords tail-coords]} (process-instructions instructions)
        ans tail-coords]
    (count (set tail-coords))))

(defn follow-the-leader [leader-coords num-followers]
  (println "\n\n")
  (loop [remaining-followers  num-followers
         last-tail            leader-coords]

    (println "Remaining followers:" remaining-followers)

    (if (> remaining-followers 0)
      (let [next-tail (follow-head last-tail)]
        (recur (dec remaining-followers)
               next-tail))
      last-tail)))

(defn part-1-ftl
  "Solve part 1 using the solution to part two so I
  can check it works against a known problem/solution
  pair"
  [input]
  (let [instructions                      (input->instructions input)
        {:keys [head-coords tail-coords]} (process-instructions instructions)
        ftl-tail-coords                   (follow-the-leader head-coords 1)]
    (count (set ftl-tail-coords))))

(defn part-2
  "Need to recur num-knots times where the previous tail
  becomes the new head"
  [input]
  (let [instructions                      (input->instructions input)
        {:keys [head-coords tail-coords]} (process-instructions instructions)
        last-tail                         (follow-the-leader tail-coords 8)]
    (count (set last-tail))))

(comment
  (part-1 example-data)
  (part-1 (utils/get-input 9)) ;; => 6391
  (part-1-ftl (utils/get-input 9)) ;; => 6391
  (part-2 example-data-2)
  ;; => 2252 => your answer is too low.
  ;; => ;; => 2541 too low???
  (part-2 (utils/get-input 9))

  (input->instructions example-data)
  (count (input->instructions (utils/get-input 9)))
  (def example-data "R 4
U 4
L 3
D 1
R 4
D 1
L 5
R 2")
(def example-data-2 "R 5
U 8
L 8
D 3
R 17
D 10
L 25
U 20")
  )
