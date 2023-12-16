(ns aoc.day-16
  (:require [aoc.utils :as u]
            [clojure.string :as str]))

(def example (str/trim "
.|...\\....
|.-.\\.....
.....|-...
........|.
..........
.........\\
..../.\\\\..
.-.-/..|..
.|....-|.\\
..//.|...."))
(def input (u/get-input 16))

(defn parse-input [input]
  (u/input->matrix input))

(comment (parse-input input))

(defn trace-beams [matrix]
  (loop [beam-heads [{:dir :r :yx [0 0]}]
         path-set   #{}]
    (if (empty? beam-heads)
      path-set

      (if (contains? path-set (first beam-heads))
        (recur (rest beam-heads) path-set)

        (let [beam-head      (first beam-heads)
              {:keys [dir yx]} beam-head
              next-coord     (({:r u/right :l u/left :u u/above :d u/below} dir) yx)
              next-sym       (get-in matrix next-coord)
              new-beam-heads (cond (u/matrix-out-of-bounds? matrix next-coord)
                                   nil

                                   (and (#{:l :r} dir) (= "|" next-sym))
                                   [{:dir :u :yx next-coord} {:dir :d :yx next-coord}]

                                   (and (#{:u :d} dir) (= "-" next-sym))
                                   [{:dir :l :yx next-coord} {:dir :r :yx next-coord}]

                                   (= "\\" next-sym )
                                   (case dir
                                     :r [{:dir :d :yx next-coord}]
                                     :l [{:dir :u :yx next-coord}]
                                     :u [{:dir :l :yx next-coord}]
                                     :d [{:dir :r :yx next-coord}])

                                   (= "/" next-sym)
                                   (case dir
                                     :r [{:dir :u :yx next-coord}]
                                     :l [{:dir :d :yx next-coord}]
                                     :u [{:dir :r :yx next-coord}]
                                     :d [{:dir :l :yx next-coord}])

                                   :else
                                   [{:dir dir :yx next-coord}])]
          (recur
           (into (rest beam-heads) new-beam-heads)
           (conj path-set beam-head)))))))

(defn trace-beams-ii
  ([matrix] (trace-beams-ii matrix {:dir :r :yx [0 0]}))
  ([matrix initial-beam-head]
   (loop [beam-heads [initial-beam-head]
          path-set   #{}]
     (if (empty? beam-heads)
       path-set

       (if (or (nil? (:yx (first beam-heads)))
               (u/matrix-out-of-bounds? matrix (:yx (first beam-heads)))
               (contains? path-set (first beam-heads)))
         (recur (vec (rest beam-heads)) path-set)

         (let [beam-head      (first beam-heads)
               {:keys [dir yx]} beam-head
               curr-sym       (get-in matrix yx)
               next-coord     (fn [d] (({:r u/right :l u/left :u u/above :d u/below} d) yx))
               new-dirs       (cond (and (#{:l :r} dir) (= "|" curr-sym))
                                    [:u :d]

                                    (and (#{:u :d} dir) (= "-" curr-sym))
                                    [:l :r]

                                    (= "\\" curr-sym)
                                    (case dir
                                      :r [:d]
                                      :l [:u]
                                      :u [:l]
                                      :d [:r])

                                    (= "/" curr-sym)
                                    (case dir
                                      :r [:u]
                                      :l [:d]
                                      :u [:r]
                                      :d [:l])

                                    :else
                                    [dir])
               new-beam-heads (mapv (fn [d] {:dir d :yx (next-coord d)}) new-dirs)]
           (recur
            (into (vec (rest beam-heads)) new-beam-heads)
            (conj path-set beam-head))))))))

(defn pt1 [input]
  (let [parsed   (parse-input input)
        path-set (trace-beams-ii parsed)
        path-yxs (set (map :yx path-set))
        num-sqs  (count (set (map :yx path-set)))
        viz      (->> (for [y (range (count parsed))
                            x (range (count (first parsed)))]
                       (cond (and (= "." (get-in parsed [y x]))
                                  (contains? path-yxs [y x]))
                             "x"
                             :else
                             (get-in parsed [y x])))
                      (partition (count (first parsed)))
                      (map vec)
                      vec)
        ans num-sqs
        ]
    ans))

(defn num-eng-tiles [matrix initial]
  (count (set (map :yx (trace-beams-ii matrix initial)))))

(defn pt2 [input]
  (let [parsed   (parse-input input)
        x-size   (count (first parsed))
        y-size   (count parsed)
        edges    (->>
                  [(vec (for [x (range x-size)] {:dir :d :yx [0 x]})) ;; top
                   (vec (for [y (range y-size)] {:dir :l :yx [y (dec x-size)]})) ;; right
                   (vec (for [x (range x-size)] {:dir :u :yx [(dec y-size) x]})) ;; bottom
                   (vec (for [y (range y-size)] {:dir :r :yx [y 0]})) ;; left
                   ]
                  (apply concat)
                  vec)
        num-t    (apply max (map #(num-eng-tiles parsed %) edges))
        ans num-t
        ;; ans edges
        ]
    ans))

(comment
  (time (pt1 example))
  (time (pt1 input));; 6902 ;; 49
  (time (pt2 example))
  (time (pt2 input)) ;; 7697
;
)
