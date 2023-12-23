(ns aoc.day-23
  (:require [aoc.utils :as u]
            [clojure.string :as str]
            [flatland.ordered.set :refer [ordered-set]]
            [clojure.set :as set]))

(def example (str/trim "
#.#####################
#.......#########...###
#######.#########.#.###
###.....#.>.>.###.#.###
###v#####.#v#.###.#.###
###.>...#.#.#.....#...#
###v###.#.#.#########.#
###...#.#.#.......#...#
#####.#.#.#######.#.###
#.....#.#.#.......#...#
#.#####.#.#.#########v#
#.#...#...#...###...>.#
#.#.#v#######v###.###v#
#...#.>.#...>.>.#.###.#
#####v#.#.###v#.#.###.#
#.....#...#...#.#.#...#
#.#########.###.#.#.###
#...###...#...#...#.###
###.###.#.###v#####v###
#...#...#.#.>.>.#.>.###
#.###.###.#.###.#.#v###
#.....###...###...#...#
#####################.#"))
(def input (u/get-input 23))

(defn parse-input [input]
  (u/input->matrix input))

(defn binary-group-by [xs f]
  (let [g (group-by f xs)]
    [(vec (get g true)) (vec (get g false))]))

(comment (binary-group-by [1 2 3 4 5 6 7 8] even?)
         (binary-group-by [1 2 3 4 5 6 7 8] odd?)
         (binary-group-by [2 4 6 8] even?)
         (binary-group-by [2 4 6 8] odd?))

(comment (-> (sorted-set)
              (conj [0 [[0 0]]])
              (conj [2 [[0 0] [0 1] [0 2]]])
              (conj [1 [[0 0] [0 1]]])
              last))

(defn longest-path [matrix start-yx end-yx part]
  (let [get-nbrs (memoize
                  (fn [yx] (let [current-pos-sym (get-in matrix yx)]
                             (if (= :pt1 part)
                               (case current-pos-sym
                                 ">" [(u/right yx)]
                                 "^" [(u/above yx)]
                                 "<" [(u/left yx)]
                                 "v" [(u/below yx)]
                                 (->> (u/get-neighbours-coords-yx matrix yx {:diagonals false})
                                      (filterv (fn [nyx] (not= "#" (get-in matrix nyx))))))
                               (->> (u/get-neighbours-coords-yx matrix yx {:diagonals false})
                                      (filterv (fn [nyx] (not= "#" (get-in matrix nyx)))))))))
        path->next (fn [path]
                     (let [head (last path)
                           ;; seen (set path) ;; perf improvement: keep path as a sorted set instead
                           nbrs (->> (get-nbrs head)
                                     (filterv #(not (contains? path %))))]
                       (mapv #(conj path %) nbrs)))]

    (loop [paths    [(ordered-set start-yx)]
           complete []
           duds     []
           steps    0]
      (assert (< steps 3000) "Max step count exceeded for longest-path fn")
      (when (= 0 (mod steps 100)) (println "step:" steps
                                           "num paths:" (count paths)
                                           "num complete:" (count complete)
                                           "num duds:" (count duds)))

      (if (empty? paths)
        (->> complete (sort-by count) reverse first count dec)

        (let [start (System/nanoTime)
              curr-and-next-paths (->>  paths
                                        (mapv (fn [path]
                                                {:path path
                                                 :next-paths (path->next path)})))
              end (System/nanoTime)
              _ (comment (when (= 0 (mod steps 100)) (println "1:" (/ (- end start) 1e6))))

              start (System/nanoTime)
              curr-and-next-path  (->> curr-and-next-paths
                                       (mapv (fn [{:keys [path next-paths]}]
                                               (mapv (fn [np] {:path path :next-path np})
                                                     next-paths)))
                                       (apply concat))
              end (System/nanoTime)
              _ (comment (when (= 0 (mod steps 100)) (println "2:" (/ (- end start) 1e6))))

              start (System/nanoTime)
              [this-duds next-or-complete-paths] (binary-group-by
                                                  curr-and-next-path
                                                  (fn [{:keys [path next-path]}]
                                                    (or (= (count path)
                                                           (count next-path))
                                                        (= 0 (count next-path)))))
              end (System/nanoTime)
              _ (comment (when (= 0 (mod steps 100)) (println "3:" (/ (- end start) 1e6))))

              start (System/nanoTime)
              [newly-complete next-paths]        (binary-group-by
                                                  (mapv :next-path next-or-complete-paths)
                                                  (fn [path] (= end-yx (last path))))
              end (System/nanoTime)
              _ (comment (when (= 0 (mod steps 100)) (println "4:" (/ (- end start) 1e6))))]
          (recur next-paths
                 (into complete newly-complete)
                 (into duds (map :path this-duds))
                 (inc steps)))))))

(defn faster-longest-path [matrix start-yx end-yx part]
  (let [get-nbrs (memoize
                  (fn [yx] (let [current-pos-sym (get-in matrix yx)]
                             (if (= :pt1 part)
                               (case current-pos-sym
                                 ">" [(u/right yx)]
                                 "^" [(u/above yx)]
                                 "<" [(u/left yx)]
                                 "v" [(u/below yx)]
                                 (->> (u/get-neighbours-coords-yx matrix yx {:diagonals false})
                                      (filterv (fn [nyx] (not= "#" (get-in matrix nyx))))))
                               (->> (u/get-neighbours-coords-yx matrix yx {:diagonals false})
                                      (filterv (fn [nyx] (not= "#" (get-in matrix nyx)))))))))
        path->next (fn [path]
                     (let [head (last path)
                           nbrs (->> (get-nbrs head)
                                     (filterv #(not (contains? path %))))]
                       (mapv #(conj path %) nbrs)))]

    (loop [paths    [(ordered-set start-yx)]
           complete []
           duds     []
           steps    0]
      ;; (assert (< steps 1250) "Max step count exceeded for longest-path fn")
      (assert (< steps 500) "Max step count exceeded for longest-path fn")
      (when (= 0 (mod steps 100)) (println "step:" steps
                                           "num paths:" (count paths)
                                           "num complete:" (count complete)
                                           "num duds:" (count duds)))

      (if (empty? paths)
        (->> complete (sort-by count) reverse first count dec)

        (let [start (System/nanoTime)
              curr-and-next-paths (->>  paths
                                        (mapv (fn [path]
                                                {:path path
                                                 :next-paths (path->next path)})))
              end (System/nanoTime)
              _ (comment (when (= 0 (mod steps 100)) (println "1:" (/ (- end start) 1e6))))

              start (System/nanoTime)
              curr-and-next-path  (->> curr-and-next-paths
                                       (mapv (fn [{:keys [path next-paths]}]
                                               (mapv (fn [np] {:path path :next-path np})
                                                     next-paths)))
                                       (apply concat))
              end (System/nanoTime)
              _ (comment (when (= 0 (mod steps 100)) (println "2:" (/ (- end start) 1e6))))

              start (System/nanoTime)
              [this-duds next-or-complete-paths] (binary-group-by
                                                  curr-and-next-path
                                                  (fn [{:keys [path next-path]}]
                                                    (or (= (count path)
                                                           (count next-path))
                                                        (= 0 (count next-path)))))
              end (System/nanoTime)
              _ (comment (when (= 0 (mod steps 100)) (println "3:" (/ (- end start) 1e6))))

              start (System/nanoTime)
              [newly-complete next-paths]        (binary-group-by
                                                  (mapv :next-path next-or-complete-paths)
                                                  (fn [path] (= end-yx (last path))))
              end (System/nanoTime)
              _ (comment (when (= 0 (mod steps 100)) (println "4:" (/ (- end start) 1e6))))

              ;; if your last node has been seen in any other path, only keep
              ;; the longest path (presumably the other one?) - if equal, pick
              ;; either one
              curr-last-items (set (mapv last next-paths))
              paths-to-keep (->>  next-paths
                                  (filterv
                                   ;; remove any paths that previously contain a point that is the
                                   ;; last item of a current path head. The current path has found
                                   ;; a longer way to get to the same point.
                                   ;; => (pathalogical input could probably break this!!)
                                   (fn [p]
                                     (let [all-but-last (disj p (last p))
                                           last-items (disj curr-last-items (last p))]
                                       (empty? (set/intersection all-but-last last-items)))))
                                  set)
              ]
          ;; (println "next-paths:" next-paths)
          ;; (println "paths-cull:" paths-to-keep)
          (recur (filterv #(contains? paths-to-keep %) next-paths)
                 (into complete newly-complete)
                 (into duds (map :path this-duds))
                 (inc steps)))))))

(defn get-end-yx [matrix]
  [(dec (u/y-size matrix)) (.indexOf (last matrix) ".")])
(comment (get-end-yx (parse-input example)))

(defn pt1 [input]
  (println "--- Pt1 ---")
  (let [matrix   (parse-input input)
        start-yx [0 1]
        end-yx   (get-end-yx matrix)
        ans      (longest-path matrix start-yx end-yx :pt1)]
    ans))

(defn pt2 [input]
  (println "--- Pt2 ---")
  (let [matrix   (parse-input input)
        start-yx [0 1]
        end-yx   (get-end-yx matrix)
        ans      (faster-longest-path matrix start-yx end-yx :pt2)]
    ans))


(comment
  (time (pt1 example)) ;; 94
  (time (pt1 input)) ;; 2310
  (time (pt2 example)) ;; 154
  (time (pt2 input))
;
)
