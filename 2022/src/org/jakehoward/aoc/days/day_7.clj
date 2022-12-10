(ns org.jakehoward.aoc.days.day-7
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as string]
            [clojure.string :as str]
            [clojure.zip :as zip]))

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
        total-size     (+ this-size subdir-sizes)]
    total-size))

;; u
(defn build-path-to-size [path-to-files]
  (->> (for [path (keys path-to-files)]
         [path (calculate-dir-size path path-to-files)])
       (into {})))

(defn part-1 [raw-input]
  (let [inputs          (map parse-input (utils/lines raw-input))
        path-to-files   (build-path-to-files inputs)
        path-to-size    (build-path-to-size path-to-files)
        all-sizes       (vals path-to-size)
        sizes-upto-100k (filter #(<= % (* 100 1000)) all-sizes)
        sum-upto-100k   (reduce + sizes-upto-100k)
        ;; ans             sizes-upto-100k
        ans             sum-upto-100k]
    ans))

(defprotocol FileTreeNode
  (is-dir? [node])
  (get-children [node])
  (make-node [node children]))

(defrecord File [name size]
  FileTreeNode
  (is-dir? [this] false)
  (get-children [this] nil)
  (make-node [node children]
    (->File (:name node) (:size node))))

(defrecord Dir [name children]
  FileTreeNode
  (is-dir? [this] true)
  (get-children [this] children)
  (make-node [node children] (->Dir (:name node) children)))

(defn fs-zip [root-dir]
  (zip/zipper
   is-dir?
   get-children
   make-node
   root-dir))

(defn find-dir [loc name]
  (if-let [first-child (zip/down loc)]
    (let [dir-contents (iterate zip/right first-child)
          node (->> dir-contents
                    (drop-while #(and
                                  (not (nil? %))
                                  (or
                                   (not= (:name (zip/node %)) name)
                                   (not= Dir (type (zip/node %))))))
                    first)]
      (when (nil? node)
        (throw (Exception. (str "Could not find dir " name " at loc"))))
      node)
    (throw (Exception. (str "Could not find dir " name " at loc: no children")))))

(def example-file-tree
  (->Dir "/" [(->Dir "d" [(->File "f.txt" 23)])
              (->Dir "a" [(->File "foo.blah" 4556)])
              (->File "b.bat" 1234)]))

(comment (println "\n\n")
    (-> (fs-zip example-file-tree)
       ;; (zip/append-child (->Dir "foodir" []))
       ;; (zip/node)
        ((fn [loc] (find-dir loc "b.bat")))
        clojure.pprint/pprint))

(defn build-file-tree [all-inputs]
  (let [z (fs-zip (->Dir "/" []))]
    (loop [loc    z
           inputs all-inputs]
      (if-let [input (first inputs)]
        (cond (= "cd" (:cmd input))
              (cond (= "/" (:arg input))
                    (recur loc (rest inputs))

                    (= ".." (:arg input))
                    (recur (zip/up loc) (rest inputs))

                    :else (recur (find-dir loc (:arg input))
                                 (rest inputs)))

              (= "ls" (:cmd input))
              (recur loc (rest inputs))

              (= :dir (:type input))
              (recur (zip/append-child loc (->Dir (:name input) []))
                     (rest inputs))

              (= :file (:type input))
              (recur (zip/append-child loc (->File (:name input)
                                                   (:size input)))
                     (rest inputs)))
        (zip/root loc)))))

(defn build-example-file-tree []
  (build-file-tree (map parse-input (utils/lines example-input))))

(defn pp-file-tree [tree]
  (->> (iterate zip/next (fs-zip tree))
       (take-while #(not (zip/end? %)))
       (map zip/node)
       clojure.pprint/pprint))

(defn part-1-tree [raw-input]
  (let [inputs          (map parse-input (utils/lines raw-input))
        file-tree       (build-file-tree inputs)]
    file-tree))

(comment
  (part-1 example-input) ;; => 95437
  (part-1 (utils/get-input 7)) ;; => 1375786

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
7214296 k"))
