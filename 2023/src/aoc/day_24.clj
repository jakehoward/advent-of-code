(ns aoc.day-24
  (:require [aoc.utils :as u]
            [clojure.string :as str]
            [clojure.math.combinatorics :refer [combinations]]))

(def example (str/trim "
19, 13, 30 @ -2,  1, -2
18, 19, 22 @ -1, -1, -2
20, 25, 34 @ -2, -2, -4
12, 31, 28 @ -1, -2, -1
20, 19, 15 @  1, -5, -3"))

(def input (u/get-input 24))

(defn parse-line [line]
  (let [[xyz vs]   (str/split line #"\s+@\s+")
        [x y z]    (->> (str/split xyz #",\s+") (map u/parse-int))
        [vx vy vz] (->> (str/split vs #",\s+") (map u/parse-int))]
    {:points [x y z] :vs [vx vy vz]}))

(defn parse-input [input]
  (mapv parse-line (str/split-lines input)))

(defn add-gradient [t]
  (let [[vx vy]  (:vs t)
        grad (/ vy vx)]
    (assoc t :m grad)))

(defn add-y-intersect [t]
  (let [[x y]  (:points t)
        grad (:m t)]
    (assoc t :c (- y (* grad x)))))

(defn- get-intersection [[t1 t2]]
  (let [m1 (:m t1)
        m2 (:m t2)
        c1 (:c t1)
        c2 (:c t2)
        i  (if (= m1 m2)
             nil
             (let [x (/ (- c2 c1) (- m1 m2))
                   y (+ (* m1 x) c1)]
               [x y]))]
    {:t1 t1 :t2 t2 :intersection i}))

(defn- before? [t [ix iy]]
  (let [[vx vy] (:vs t)
        [x  y]  (:points t)]
    (cond (and (neg? vx) (neg? vy))
          (and (> x ix) (> y iy))

          (and (pos? vx) (pos? vy))
          (and (< x ix) (< y iy))

          (and (neg? vx) (pos? vy))
          (and (> x ix) (< y iy))

          (and (pos? vx) (neg? vy))
          (and (< x ix) (> y iy)))))

(defn in-box-and-in-future [lower upper {:keys [t1 t2 intersection]}]
  (let [[ix iy] intersection]
    (and (<= lower ix upper)
         (<= lower iy upper)
         (before? t1 [ix iy])
         (before? t2 [ix iy]))))

(defn pt1 [input lower upper]
  (let [ts  (->> (parse-input input) ;; "trajectories" cba to tpye that again and again
                 (mapv add-gradient)
                 (mapv add-y-intersect))
        intersections (->> (combinations ts 2)
                           (mapv get-intersection)
                           (filterv :intersection)
                           (filterv (partial in-box-and-in-future lower upper))
                           (mapv #(mapv double (:intersection %))))
        ans (count intersections)
        ]
    ans))

(defn pt2 [input]
  (let [parsed (parse-input input)
        ans    parsed]
    ans))

(comment
  (pt1 example 7 27)
  (pt1 input 200000000000000 400000000000000) ;; 16779
  (pt2 example)
  (pt2 input)
  ;
)
