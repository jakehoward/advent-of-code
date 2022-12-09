(ns org.jakehoward.aoc.days.day-7
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as string]
            [clojure.string :as str]))

(defn parse-input [input]
  (cond
    (string/starts-with? input "$")
    (let [[_ cmd arg] (string/split input #"\s+")]
      {:type :cmd :cmd cmd :arg arg})

    (string/starts-with? input "dir")
    (let [[_ name] (string/split input #"\s+")]
      {:type :dir :name name})

    (boolean (re-matches #"^\d+ .+" input))
    (let [[size name] (string/split input #"\s+")]
      {:type :file :name name :size (Integer/parseInt size)})

    :else
    (throw (Exception. (str "Failed to parse input: " input)))))


(defn update-path [raw-current-path input]
  (let [current-path (vec raw-current-path)]
    (cond
      (and
       (= (:cmd input) "cd")
       (= (:arg input) "..")) (pop current-path)

      (= (:cmd input) "cd")   (conj current-path (:arg input))

      :else                   current-path)))


(defn update-path-to-file [path-to-file path input]
  (if (= :file (:type input))
    (assoc path-to-file path (conj (or (get path-to-file path) #{}) input))
    path-to-file))


(defn build-path-to-files [inputs]
  (loop [remaining-inputs inputs
         current-path     []
         path-to-file     {}]
    (if-let [input (first remaining-inputs)]
      (let [updated-path         (update-path current-path input)
            updated-path-to-file (update-path-to-file path-to-file current-path input)]
        (recur (rest remaining-inputs)
               updated-path
               updated-path-to-file))
      path-to-file)))

(defn get-subdirs [path paths]
  (->> paths
       (filter (fn [other] (and (> (count other) (count path))
                                   (= (take (count path) other) path))))))

(defn sum-file-sizes [files]
  (reduce + (map :size files)))

;; u
(defn- calculate-dir-size [path path-to-files]
  (let [this-files     (path-to-files path)
        this-size      (sum-file-sizes this-files)

        subdirs        (get-subdirs path (keys path-to-files))
        ;; _              (println "subdirs:" subdirs)
        subdir-files   (apply concat (vals (select-keys path-to-files subdirs)))
        ;; _              (println "subdir files:" subdir-files)
        subdir-sizes   (sum-file-sizes subdir-files)
        ;; _              (println "subdir sizes:" subdir-sizes)
        total-size     (+ this-size subdir-sizes)
        ]
    total-size))

;; u
(defn build-path-to-size [path-to-files]
  (->> (for [path (keys path-to-files)]
         [path (calculate-dir-size path path-to-files)])
       (into {})))

(defrecord Node [name children])

(defn child-exists? [node name]
  (some #{name} (map :name (:children node))))

(defn get-child [node name]
  (first (filter #(= (:name %) name) (:children node))))

(defn- update-tree [root-node path files]
  (loop [path-segments path
         root-node     (transient root-node)
         current-node  (transient root-node)]
    (if-let [path-segment (first path-segments)]
      ;; create/find missing children and recur
      (if (child-exists? current-node path-segment)
        (let [child (get-child current-node path-segment)]
          (recur (rest path-segments)
                 root-node
                 child))
        (recur (rest path-segments)
               root-node
               (assoc! current-node
                       :children
                       (conj (get current-node :children) (->Node path-segment #{})))))

      ;; put files against node + return root
      (do
        (assoc! current-node :children files)
        (persistent! root-node)))
    )
  )

(defn build-tree [all-path-to-files]
  (loop [path-to-files (vec all-path-to-files)
         root-node     (->Node "/" #{})]
    (if-let [p->fs (first path-to-files)]
      (let [[path files] p->fs]
        (update-tree root-node path files))
      root-node)))


(defn part-1 [raw-input]
  (let [inputs          (map parse-input (utils/lines raw-input))
        path-to-files   (build-path-to-files inputs)
        path-to-size    (build-path-to-size path-to-files)
        all-sizes       (vals path-to-size)
        sizes-upto-100k (filter #(<= % (* 100 1000)) all-sizes)
        sum-upto-100k   (reduce + sizes-upto-100k)
        ;; ans             sizes-upto-100k
        ans             sum-upto-100k
        ]
    ans))

(defn part-1-ex []
  (part-1 example-input))

(defn part-1-prod []
  (part-1 (utils/get-input 7)))

(comment
  (part-1-ex);; => 95437
  (part-1-prod);; => 1375786

  (def example-input "$ cd /
$ ls
dir a
14848514 b.txt
8504156 c.dat
dir d
$ cd a
$ ls
dir e
29116 f
2557 g
62596 h.lst
$ cd e
$ ls
584 i
$ cd ..
$ cd ..
$ cd d
$ ls
4060174 j
8033020 d.log
5626152 d.ext
7214296 k")
  )
