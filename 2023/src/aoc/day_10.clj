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

(def example-pt2 (str/trim "
...........
.S-------7.
.|F-----7|.
.||.....||.
.||.....||.
.|L-7.F-J|.
.|..|.|..|.
.L--J.L--J.
..........."))

(def example-pt2-ii (str/trim "
.F----7F7F7F7F-7....
.|F--7||||||||FJ....
.||.FJ||||||||L7....
FJL7L7LJLJ||LJ.L-7..
L--J.L7...LJS7F-7L7.
....F-J..F7FJ|L7L7L7
....L7.F7||L7|.L7L7|
.....|FJLJ|FJ|F7|.LJ
....FJL-7.||.||||...
....L---J.LJ.LJLJ..."))

(def example-pt2-iii (str/trim "
FF7FSF7F7F7F7F7F---7
L|LJ||||||||||||F--J
FL-7LJLJ||||||LJL-77
F--JF--7||LJLJ7F7FJ-
L---JF-JLJ.||-FJLJJ7
|F|F-JF---7F7-L7L|7|
|FFJF7L7F-JF7|JL---7
7-L-JL7||F7|L7F-7F7|
L.L7LFJ|||||FJL7||LJ
L7JLJL-JLJLJL--JLJ.L"))

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

(defn get-s-val [matrix]
  (let [start-yx      (get-start-yx matrix)
        start-nbrs-yx (u/get-neighbours-coords-yx matrix start-yx)
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

    (cond (= #{(right start-yx) (left start-yx)} (set valid-nbrs))
          "-"
          (= #{(above start-yx) (below start-yx)} (set valid-nbrs))
          "|"
          (= #{(below start-yx) (left start-yx)} (set valid-nbrs))
          "7"
          (= #{(right start-yx) (below start-yx)} (set valid-nbrs))
          "F"
          (= #{(above start-yx) (left start-yx)} (set valid-nbrs))
          "J"
          (= #{(above start-yx) (right start-yx)} (set valid-nbrs))
          "L")))

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

(defn vertical? [matrix yx s-val]
  (let [sym (get-in matrix yx)]
    (boolean (#{"|" "L" "J" "F" "7"} (if (= "S" sym) s-val sym)))))

(defn trace-ray [matrix s-val route trace-y]
  (let [all-x-coords (range (count (first matrix)))]

    (comment (println "TY:" trace-y))

    (loop [xs            all-x-coords
           in-loop       false
           in-count      0]

      (do (when (= 7 trace-y) (println "x:" (first xs) "in:" in-loop "ic:" in-count)))

      (if (empty? xs)
        in-count
        (let [coord             [trace-y (first xs)]
              sym               (get-in matrix coord)
              right-sym         (get-in matrix (right coord))
              coord-on-route    (contains? route coord)
              next-in-loop      (if (and coord-on-route
                                         (vertical? matrix coord s-val)
                                         (not (and (contains? route (right coord))
                                                   (#{"L" "F"} sym)
                                                   (#{"-" "7" "J"} right-sym))))
                                     (not in-loop)
                                     in-loop)
              next-in-count     (if (and (not coord-on-route) in-loop)
                                  (inc in-count)
                                  in-count)]
          (comment (println "vertical?" (vertical? matrix coord s-val)))
          (recur (rest xs)
                 next-in-loop
                 next-in-count))))))

(defn pt1 [input]
  (let [matrix   (parse-input input)
        ans (count (get-loop matrix))]
    (/ ans 2)))

(defn -info [input]
  (let [matrix (u/input->matrix input)
        route  (get-loop matrix)
        s-val  (get-s-val matrix)
        s-yx   (get-start-yx matrix)]
    {:matrix-y-size (count matrix)
     :matrix-x-size (count (first matrix))
     :route-count (count route)
     :start-yx s-yx
     :s-val s-val
     :n-route-syms  (take 10 (map #(get-in matrix %) route))}))

(defonce infos (memoize -info))


(defn get-starting-info [matrix ord-route s-val]
  ;;    get a coord we know is on the route
  (let [[a-y a-x]  (first ord-route)
        ;; get the min x coord for that y
        min-x      (->> ord-route
                        (filter (fn [[y x]] (= a-y y)))
                        (map second)
                        (apply min))
        ;; make that the start
        start-coord [a-y min-x]
        _           (assert (contains? (set ord-route) start-coord)
                            (str "Nope, try again: " start-coord))
        start-idx   (.indexOf (vec ord-route) start-coord)
        ;; restart the route with that new start co-ord
        normalised-route (take (count ord-route)
                                (drop start-idx (cycle ord-route)))
        start-symbol (if (= "S" (get-in matrix start-coord)) s-val (get-in matrix start-coord))
        in-dir (cond (= start-symbol "|") :right
                     (= start-symbol "F") (if (right? start-coord (second normalised-route))
                                            :right
                                            :down)
                     (= start-symbol "L") (if (right? start-coord (second normalised-route))
                                            :right
                                            :up)
                     :else (throw (Exception. (str "Invalid start symbol:" start-symbol))))]
    (println "start-coord:" start-coord
             "start-idx:" start-idx
             "start-sym:" start-symbol
             "start-dir:" in-dir
             "normalised-route:" (take 3 normalised-route)
             "normalised-route-syms:" (map #(get-in matrix %) (take 3 normalised-route)))
    {:normalised-route normalised-route :in-dir in-dir}))

(defn quad-counting [matrix route s-val start-direction]
  (let [route-set (set route)
        max-x (dec (count (first matrix)))
        max-y (dec (count matrix))]

    (loop [rem-route  route
           direction  start-direction
           in-count   0
           steps      0
           prev       []]

      (if (empty? rem-route)
        in-count

        (let [curr-yx (first rem-route)
              raw-sym (get-in matrix curr-yx)
              sym     (if (= "S" raw-sym) s-val raw-sym)
              _       (cond (= "-" sym) (when-not (#{:up :down} direction) (println "ERR-A"))
                            (= "|" sym) (when-not (#{:left :right} direction) (println "ERR-B")))
              sym-curr-direction->next-direction
              {"-" {:up :up :down :down}
               "|" {:left :left :right :right}
               "F" {:up :left, :down :right, :left :up, :right :down}
               "7" {:up :right, :down :left, :left :down, :right :up}
               "J" {:up :left, :down :right, :left :up, :right :down}
               "L" {:up :right, :down :left, :left :down, :right :up}}
              direction-to-fn {:up above :down below :left left :right right}
              next-direction (get-in sym-curr-direction->next-direction [sym direction])

              contained-spaces
              (try
                (cond (#{"|" "-"} sym)
                      (->> (iterate (direction-to-fn direction) curr-yx)
                           (drop 1)
                           (take-while #(not (or (contains? route-set %)
                                                 (u/matrix-out-of-bounds? matrix %))))
                           count)

                      (#{"F" "7" "J" "L"} sym)
                      (let [before-dir-change-count
                            (->> (iterate (direction-to-fn direction) curr-yx)
                                 (drop 1)
                                 (take-while #(not (or (contains? route-set %)
                                                       (u/matrix-out-of-bounds? matrix %))))
                                 count)
                            after-dir-change-count
                            (->> (iterate (direction-to-fn next-direction) curr-yx)
                                 (drop 1)
                                 (take-while #(not (or (contains? route-set %)
                                                       (u/matrix-out-of-bounds? matrix %))))
                                 count)]
                        (+ before-dir-change-count after-dir-change-count))

                      :else 0)
                (catch Exception e
                  (println "steps:" steps
                           "rem:" (count rem-route)
                           "p:" (take-last 5 prev)
                           "c:" curr-yx
                           "icfn:" (direction-to-fn direction)
                       "d:" direction "nd:" next-direction "sym:" sym "c:" in-count)
                  (throw e)))]

          (recur (rest rem-route)
                 next-direction
                 (+ in-count contained-spaces)
                 (inc steps)
                 (conj prev [sym curr-yx direction])))))))

(defn pt2 [input]
  (let [matrix (parse-input input)
        route  (get-loop matrix)
        ;; traces (map #(trace-ray matrix
                                ;; (get-s-val matrix)
                                ;; (set route)
        ;; %) (range (count matrix)))
        {:keys [normalised-route in-dir]} (get-starting-info matrix route (get-s-val matrix))
        quad-counted (quad-counting matrix normalised-route (get-s-val matrix) in-dir)]
    (/ quad-counted 4)))

(defn print-route [input]
  (let [matrix (u/input->matrix input)
        route  (get-loop matrix)
        route-set (set route)
        s-val  (get-s-val matrix)
        s-yx   (get-start-yx matrix)
        y-size (count matrix)
        x-size (count (first matrix))
        route-string (for [y (range y-size)
                           x (range x-size)]
                       (if (route-set [y x])
                         "." " "))
        lines (partition x-size route-string)]
    (spit "viz-day-10.txt" (str/join "\n" (map str/join lines)))))

(comment
  (do (println "---") (pt2 example-pt2))
  (do (println "---") (pt2 example-pt2-ii))
  (do (println "---") (pt2 example-pt2-iii))
  (do (println "---") (pt2 example))
  (do (println "--- input ---") (println (pt2 input)))
  (pt2 input)  ;; 1352 (too high)

  (infos input)
  (print-route example-pt2)
  (print-route input)

  (def example-pt2 (str/trim "
...........
.S-------7.
.|F-----7|.
.||.....||.
.||.....||.
.|L-7.F-J|.
.|..|.|..|.
.L--J.L--J.
..........."))

  (def foo (str/trim "
.....
.S-7.
.|.|.
.L-J.
....."))

  (let [input input
        matrix (parse-input input)
        route  (get-loop matrix)
        start-yx (get-start-yx matrix)
        {:keys [normalised-route in-dir]} (get-starting-info matrix route (get-s-val matrix))

        route-ans (drop-while #(not= [86 93] %) route)
        n-route-ans (drop-while #(not= [86 93] %) normalised-route)
        ]
    (println "--- Debug ---")
    (println "Start-yx:" start-yx)
    (println "R:" (take 10 route-ans) "F:" (first route) "L:" (last route))
    (println "NR:" (take 10 n-route-ans)))

  (let [input input
        matrix (parse-input input)
        route  (get-loop matrix)
        ;; traces (map #(trace-ray matrix
                                ;; (get-s-val matrix)
                                ;; (set route)
        ;; %) (range (count matrix)))
        {:keys [normalised-route in-dir]} (get-starting-info matrix route (get-s-val matrix))
        ]
    (println "In dir:" in-dir "rout count:" (count route) "nr c:" (count normalised-route) "set r:" (count (set route)))
    (take 10 (map #(get-in matrix %) normalised-route)))

  (pt1 example)
  (pt1 example-iv)
  (pt1 input) ;; 7012
  (pt2 example)
  (pt2 input)
;
  )
