(ns aoc.utils
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

;; =====
;; input
;; =====
(defn get-input [day]
  (-> (format "day-%s.txt" day)
      io/resource
      slurp
      str/trim))

(defn lines [txt]
  (str/split-lines txt))


(defn split-line [line]
  (str/split line #""))

(defn input->matrix [input]
  (->> input
       lines
       (map split-line)
       vec))

(def input->grid input->matrix)

(defn cols [grid]
  (apply map vector grid))

(defn rows [grid]
  grid)

(comment
  (let [input "abc\ndef"]
    (->> input
         input->matrix)))

(defn y-size [grid] (count grid))

(defn x-size [grid] (count (first grid)))

(defn get-neighbours-coords-yx-sz
  ([y-size x-size yx] (get-neighbours-coords-yx-sz y-size x-size yx {}))
  ([y-size x-size [y x] {:keys [diagonals] :or {diagonals true}}]
   (let [deltas [[-1 0] [1 0] [0 -1] [0 1]]
         deltas (into deltas (if diagonals [[-1 1] [1 1] [1 -1] [-1 -1]] []))]

     (when (>= y y-size)
       (throw (Exception. (str "idx y: " y " is out of bounds of matrix size: " [x-size, y-size]))))
     (when (>= x x-size)
       (throw (Exception. (str "x: " x " is greater than matrix-x-size: " x-size))))

     (->> deltas
          (mapv #(mapv + [y x] %))
          (filterv (fn [[new-y new-x]] (and (< -1 new-y y-size) (< -1 new-x x-size))))))))

(defn get-neighbours-coords-yx
  ([grid yx] (get-neighbours-coords-yx grid yx {}))
  ([grid yx opts]
   (let [x-sz (x-size grid)
         y-sz (y-size grid)]
     (get-neighbours-coords-yx-sz y-sz x-sz yx opts))))

(defn matrix-out-of-bounds? [grid [y x]]
  (let [x-size (count (first grid))
        y-size (count grid)]
    (not (and (< -1 y y-size)
              (< -1 x x-size)))))

(def grid-out-of-bounds? matrix-out-of-bounds?)
;; long hand for editor assistance
(defn grid-in-bounds? [grid yx] ((complement grid-out-of-bounds?) grid yx))

(defn get-nbr-yxs [grid yx opts]
  (assert (boolean? (:diagonals opts)) "You must pass opts {:diagonals true/false}")
  (get-neighbours-coords-yx grid yx opts))

(defn get-nbr-yxs-with-size [y-size x-size yx opts]
  (assert (boolean? (:diagonals opts)) "You must pass opts {:diagonals true/false}")
  (get-neighbours-coords-yx-sz y-size x-size yx opts))

(comment
  (matrix-out-of-bounds? [[nil nil nil] [nil nil nil]] [2 2])
  )

;; ==========================
;; 2d cartesian co-oridinates
;; ==========================
(defn get-neighbours
  ([matrix yx] (get-neighbours matrix yx {}))
  ([matrix yx opts]
   (let [nbr-yx (get-neighbours-coords-yx matrix yx opts)]
     (mapv #(get-in matrix %) nbr-yx))))

(def get-nbrs get-neighbours)

(comment
  (get-neighbours-coords-yx [[:a :b :c] [:d :e :f]] [0 0] {:diagonals false}) ;; [[1 0] [0 1]]
  (get-neighbours-coords-yx [[:a :b :c] [:d :e :f]] [1 2] {:diagonals false}) ;; [[0 2] [1 1]]
  (get-neighbours-coords-yx [[:a :b :c] [:d :e :f]] [0 0] {:diagonals true})
  ;; [[1 0] [0 1] [1 1]]
  (get-neighbours-coords-yx [[:a :b :c]
                             [:d :e :f]
                             [:g :h :1]] [1 1] {:diagonals true})
  ;; [[0 1] [2 1] [1 0] [1 2] [0 2] [2 2] [2 0] [0 0]]
  (get-neighbours [[:a :b :c]
                   [:d :e :f]
                   [:g :h :1]] [1 1])
  (get-neighbours [[:a :b :c]
                   [:d :e :f]
                   [:g :h :1]] [2 0] {:diagonals false})
  ;;
  )

(defn matrix-coords-yx [matrix]
  (for [y (range (count matrix))
        x (range (count (first matrix)))]
    [y x]))

(defn all-grid-coords [grid]
  (matrix-coords-yx grid))

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

;; =======
;; parsing
;; =======
;; todo
;; require [clojure.edn :as edn]
;; change to (edn/read-string "3409451394")
;; (deals with big ints)
;; ??

(defn parse-int [s] (Long/parseLong s))


;; ===========
;; convenience
;; ===========
(defn sum [xs] (reduce + 0 xs))
(defn halve [x] (* 1/2 x))
(defn build-unique-index [xs k] (reduce (fn [acc item]
                                          (assert (nil? (get acc (get item k)))
                                                  (str "Cannot duplicate key: '" (get item k) "' in unique index"))
                                          (assoc acc (get item k) item)) {} xs))
(comment (build-unique-index [{:name "foo" :vals :blah} {:name "bar" :vals :splat}] :name)
         (build-unique-index [{:name "foo" :vals :blah} {:name "foo" :vals :splat}] :name))
(defn reverse-mapping [m] (reduce-kv (fn [acc k v] (assoc acc v k)) {} m))
(comment (reverse-mapping {:from :to :A :B}))


;; ====
;; data
;; ====

(def a->z-chars (->> (range 97 (+ 97 26))
                     (map char)))
(def a->z (mapv str a->z-chars))
(def A->Z-chars (map #(Character/toUpperCase %) a->z-chars))
(def A->Z (mapv str A->Z-chars))

(def zero->nine-strings #{"0" "1" "2" "3" "4" "5" "6" "7" "8" "9"})

;; =====
;; debug
;; =====
(defn spy [x] (println "spy:" x) x)
(defn sample-spy [p x] (when (< (rand) p) (println "spy:" x)) x)
