(ns org.jakehoward.aoc.days.day-23
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as str]
            [clojure.set :as set]
            [clojure.algo.generic.functor :refer [fmap]]))

(defn parse-input [input]
  (utils/chars (utils/lines input)))

(defn get-elf-yxs [grid]
  (for [y (range (count grid))
        x (range (count (first grid)))
        :when (= "#" (nth (nth grid y) x))]
    [y x]))

(defn rotate [xs n]
  (take (count xs) (drop (mod n (count xs)) (cycle xs))))

(defn neighbours [[y x]]
  (for [dy [-1 0 1]
        dx [-1 0 1]
        :when (not (and (= 0 dx) (= 0 dy)))]
    [(+ y dy) (+ x dx)]))

(def all-directions [{:name :N :checks [[-1 0] [-1 1] [-1 -1]] :move [-1 0]}
                     {:name :S :checks [[1 0] [1 1] [1 -1]] :move [1 0]}
                     {:name :W :checks [[0 -1] [1 -1] [-1 -1]] :move [0 -1]}
                     {:name :E :checks [[0 1] [1 1] [-1 1]] :move [0 1]}])

(defn add-coords [[ay ax] [by bx]]
  [(+ ay by) (+ ax bx)])

(defn overlaps? [s1 s2]
  (> (count (set/intersection s1 s2)) 0))

(defn group-count [xs]
  (->> xs
       (keep identity)
       (group-by identity)
       (fmap count)))

(defn get-first-viable-move [elf-yx elf-yxs-set all-directions]
  (loop [dirs all-directions]
    (if-let [dir (first dirs)]
      (let [poss-yxs (map #(add-coords elf-yx %) (:checks dir))
            is-on    (not (overlaps? elf-yxs-set (set poss-yxs)))]
        (if is-on
          (add-coords elf-yx (:move dir))
          (recur (rest dirs))))
      nil)))

(defn get-move [elf-yx elf-yxs-set directions]
  (let [nbr-yxs          (set (neighbours elf-yx))
        move             (if (overlaps? nbr-yxs elf-yxs-set)
                           (get-first-viable-move elf-yx elf-yxs-set directions)
                           nil)]
    move))

(defn play-round [elf-yxs-set mode]
  (let [directions     (rotate all-directions mode)
        moves          (for [elf-yx  elf-yxs-set]
                         {:elf-yx elf-yx :move (get-move elf-yx elf-yxs-set directions)})
        ;; _            (println "\n\nmoves:" moves)
        move->count    (group-count (keep (fn [m] (:move m)) moves))
        viable-mvs     (filter (fn [m] (and (not (nil? (:move m)))
                                            (= 1 (get move->count (:move m))))) moves)
        old-pos        (map (fn [m] (:elf-yx m)) viable-mvs)
        new-pos        (map (fn [m] (:move m)) viable-mvs)]
    (set/union (set/difference elf-yxs-set (set old-pos)) (set new-pos))))

(defn move-elves [initial-elf-yxs num-rounds]
  (loop [elf-yxs-set (set initial-elf-yxs)
         round       0]
    (if (< round num-rounds)
      (recur (play-round elf-yxs-set (mod round 4)) (inc round))
      elf-yxs-set)))

(defn move-elves-pt2 [initial-elf-yxs]
  (loop [prev-yxs    nil
         elf-yxs-set (set initial-elf-yxs)
         round       0]
    (if (and (not= prev-yxs elf-yxs-set) (< round 3000))
      (recur elf-yxs-set (play-round elf-yxs-set (mod round 4)) (inc round))
      round)))

;; 0 1 2 3 4 5 6
;; . . . # . . #
(defn num-empty-sq [elf-yxs]
  (let [min-x  (apply min (map second elf-yxs))
        min-y  (apply min (map first elf-yxs))
        max-x  (apply max (map second elf-yxs))
        max-y  (apply max (map first elf-yxs))
        num-sq (* (inc (- max-x min-x)) (inc (- max-y min-y)))]
    (- num-sq (count elf-yxs))))

(defn part-1 [input]
  (let [input-grid   (parse-input input)
        elf-yxs      (get-elf-yxs input-grid)
        num-rounds   10
        elf-yxs      (move-elves elf-yxs num-rounds)
        num-empty    (num-empty-sq elf-yxs)]
    num-empty))

(defn part-2 [input]
  (let [input-grid   (parse-input input)
        elf-yxs      (get-elf-yxs input-grid)
        round        (move-elves-pt2 elf-yxs)]
    round))

(defn viz [elf-yxs]
  (println "\n\n")
  (let [min-x   (apply min (map second elf-yxs))
        min-y   (apply min (map first elf-yxs))
        max-x   (apply max (map second elf-yxs))
        max-y   (apply max (map first elf-yxs))
        [ty tx] [(- min-y) (- min-x)]
        eyx-set (set elf-yxs)
        y-size  (inc (+ max-y ty))
        x-size  (inc (+ max-x tx))]
    (->> (for [y (range y-size)
               x (range x-size)]
           (if (eyx-set [(- y ty) (- x tx)]) "#" "."))
         (partition x-size)
         (map #(str/join %))
         (str/join "\n")
         println)))

(comment
  (time (part-2 example-input))
  (time (part-2 (utils/get-input "23"))) ;; => 1055

  (let [input-grid (parse-input example-input)
        elf-yxs    (get-elf-yxs input-grid)
        elf-yxsp    (move-elves elf-yxs 3)
        _          (viz elf-yxsp)]
    (= (set elf-yxs) elf-yxsp))

  (part-1 example-input)
  (time (part-1 (utils/get-input "23"))) ;; => 4109

  (def example-input (->
                      "
..............
..............
.......#......
.....###.#....
...#...#.#....
....#...##....
...#.###......
...##.#.##....
....#..#......
..............
..............
.............."
                      (str/trim)))
  ;;
  )
