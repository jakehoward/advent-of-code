(ns aoc.day-10
  (:require [aoc.utils :as u]
            [clojure.string :as str]))

(def example (str/trim "
.....
.S-7.
.|.|.
.L-J.
....."))

(def example-ii (str/trim "
-L|F7
7S-7|
L|7||
-L-J|
L|-JF"))

(def example-iii (str/trim "
..F7.
.FJ|.
SJ.L7
|F--J
LJ..."))

(def example-iv (str/trim "
7-F7-
.FJ|7
SJLL7
|F--J
LJ.LJ"))

(def input (u/get-input 10))

(defn parse-input [input]
  (u/input->matrix input))

;; todo: matrix iter (zipper) with shortcut return on finding item
(defn get-start-yx [matrix]
  (let [all-yx (for [y (range (count matrix))
                     x (range (count (first matrix)))]
                 [y x])]
    (reduce (fn [start-yx yx]
              (if (= "S" (get-in matrix yx))
                yx
                start-yx)) all-yx)))

(defn right [[y x]]
  [y (inc x)])

(defn right? [this-yx [y x]]
  (= this-yx [y (dec x)]))

(defn left [[y x]]
  [y (dec x)])

(defn left? [this-yx [y x]]
  (= this-yx [y (inc x)]))

(defn below [[y x]]
  [(inc y) x])

(defn below? [this-yx [y x]]
  (= this-yx [(dec y) x]))

(defn above [[y x]]
  [(dec y) x])

(defn above? [this-yx [y x]]
  (= this-yx [(inc y) x]))

(defn get-valid-start-nbrs
  "Valid if only two valid neighbours are next to start"
  [matrix start-yx]
  (let [start-nbrs-yx (u/get-neighbours-coords-yx matrix start-yx)
        valid-nbrs    (->> start-nbrs-yx
                           (filter (fn [n-yx]
                                     (cond (above? start-yx n-yx)
                                           (boolean (#{"7" "|" "F"} (get-in matrix n-yx)))

                                           (right? start-yx n-yx)
                                           (boolean (#{"J" "-" "7"} (get-in matrix n-yx)))

                                           (left? start-yx n-yx)
                                           (boolean (#{"L" "-" "F"} (get-in matrix n-yx)))

                                           (below? start-yx n-yx)
                                           (boolean (#{"J" "|" "L"} (get-in matrix n-yx)))))))]
    (assert (= (count valid-nbrs) 2) (str "expected 2 valid nbrs for start-yx:" start-yx
                                          " but got: " valid-nbrs))
    (->> valid-nbrs
         (map (fn [n-yx] [(get-in matrix n-yx) n-yx]))
         set)))

(comment (get-valid-start-nbrs (u/input->matrix example) [1 1]))

(defn valid-neighbour? [matrix this-yx n-yx]
  (let [valid-n-lookup {"-" (fn [yx] #{["-" (right yx)]
                                       ["-" (left yx)]
                                       ["F" (left yx)]
                                       ["7" (right yx)]
                                       ["J" (right yx)]
                                       ["L" (left yx)]
                                       ["S" (left yx)]
                                       ["S" (right yx)]})
                        "|" (fn [yx] #{["F" (above yx)]
                                       ["7" (above yx)]
                                       ["J" (below yx)]
                                       ["L" (below yx)]
                                       ["|" (above yx)]
                                       ["|" (below yx)]
                                       ["S" (below yx)]
                                       ["S" (above yx)]})
                        "F" (fn [yx] #{["7" (right yx)]
                                       ["J" (below yx)]
                                       ["J" (right yx)]
                                       ["-" (right yx)]
                                       ["|" (below yx)]
                                       ["L" (below yx)]
                                       ["S" (right yx)]
                                       ["S" (below yx)]})
                        "7" (fn [yx] #{["-" (left yx)]
                                       ["F" (left yx)]
                                       ["L" (left yx)]
                                       ["J" (below yx)]
                                       ["|" (below yx)]
                                       ["L" (below yx)]
                                       ["S" (left yx)]
                                       ["S" (below yx)]})
                        "J" (fn [yx] #{["|" (above yx)]
                                       ["7" (above yx)]
                                       ["F" (above yx)]
                                       ["-" (left yx)]
                                       ["F" (left yx)]
                                       ["L" (left yx)]
                                       ["S" (above yx)]
                                       ["S" (left yx)]})
                        "L" (fn [yx] #{["|" (above yx)]
                                       ["7" (above yx)]
                                       ["F" (above yx)]
                                       ["-" (right yx)]
                                       ["J" (right yx)]
                                       ["7" (right yx)]
                                       ["S" (above yx)]
                                       ["S" (right yx)]})
                        "." (fn [_] #{})
                        "S" (fn [start-yx] (get-valid-start-nbrs matrix start-yx))}
        this-sym (get-in matrix this-yx)
        n-sym    (get-in matrix n-yx)]
    (contains? ((valid-n-lookup this-sym) this-yx) [n-sym n-yx])))

(comment (->> [[0 1] [1 0]]
              (map #(valid-neighbour? [["F" "-" "7"]
                                       ["|" "." "|"]
                                       ["L" "-" "J"]] [0 0] %))))

(defn get-loop [matrix]
  (let [start-yx (get-start-yx matrix)]
    (loop [curr-yx start-yx
           route   []]

      (comment (println "curr-yx" curr-yx))

      (if (and (= curr-yx start-yx) (seq route))
        route
        (let [all-n-yx (u/get-neighbours-coords-yx matrix curr-yx {:diagonals false})
              n-yx     (filter (partial valid-neighbour? matrix curr-yx) all-n-yx)

              _ (when (not (contains? (u/get-neighbours-coords-yx matrix curr-yx) start-yx))
                  (assert (= 2 (count n-yx))
                          (str "Expected 2 valid neighbours but got: "
                               (vec n-yx) "for this: " curr-yx)))
              next-yx (filter #(not= (last route) %) n-yx)

              ;; start node has two valid options
              _ (when (seq route)
                  (assert (= 1 (count next-yx)) (str "Expected exactly one next-yx, got: " next-yx)))]
          (recur (first next-yx) (conj route curr-yx)))))))

(defn pt1 [input]
  (let [matrix   (parse-input input)
        ;; start-yx (get-start-yx matrix)
        ;; ans      {:start start-yx :matrix matrix}
        ans (count (get-loop matrix))]
    (/ ans 2)))

(defn pt2 [input]
  (let [parsed (parse-input input)
        ans    parsed]
    ans))

(comment
  (def foo (str/trim "
.....
.S-7.
.|.|.
.L-J.
....."))

  (pt1 example)
  (pt1 example-iv)
  (pt1 input) ;; 7012
  (pt2 example)
  (pt2 input)
;
  )
