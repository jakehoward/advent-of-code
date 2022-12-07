(ns org.jakehoward.aoc.days.day-7
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as string]))

(defn is-cmd? [input]
  (= (:type input) :cmd))

(defn parse-command [line]
  (let [[_ cmd arg] (string/split line #"\s+")]
    {:type :cmd :cmd cmd :arg arg}))

(defn- parse-directory [line]
  (let [[_ dirname] (string/split line #"\s+")]
    {:type :dir :name dirname}))

(defn- parse-file [line]
  (let [[size filename] (string/split line #"\s+")]
    {:type :file :name filename :size size}))

(defn parse-input-line [line]
  (cond
    (string/starts-with? line "$")   (parse-command line)
    (string/starts-with? line "dir") (parse-directory line)
    :else                            (parse-file line)))

(defn parse-input [input]
  (->> (string/split input #"\n")
       (map parse-input-line)))

(defn update-path [path input]
  ;; lacks elegance
  (cond
    (and (is-cmd? input)
         (= (:cmd input) "cd")) (if (= (:arg input) "..")
                                  (pop path)
                                  (conj path (:arg input)))
    :else                       path))


(defn update-path-to-files [path-to-files current-path input]
  (if (= :file (:type input))
    (assoc
     path-to-files
     current-path
     (conj (or (path-to-files current-path) []) input))
    path-to-files))

(defn build-path-to-files [inputs]
  (loop [remaining-inputs  inputs
         current-path      []
         path-to-files     {}]
    (if-let [input (first remaining-inputs)]
      (let [updated-path          (update-path current-path input)
            updated-path-to-files (update-path-to-files path-to-files updated-path input)]
        (recur (rest remaining-inputs)
               updated-path
               updated-path-to-files))
      path-to-files)
    ))


(comment
  (let [inputs (parse-input example-input)
        ptf    (build-path-to-files inputs)]
    (ptf ["/" "d"]))

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

  (defn update-ran-ls [input current]
    (cond
      (= (:cmd input) "ls") true
      (= (:cmd input) "cd") false
      :else                 current))

  (defn ensure-path-exists [tree path]
    (loop [current-tree   tree
           updated-tree   tree
           remaining-path path]
      (let [[path-segment next-path-segment] remaining-path]
        (cond
          (nil? path-segment)         updated-tree
          (nil? next-path-segment)    (recur)
          (= path-segment
             (:name current-tree))    nil))))

  (defn update-tree [tree input path]
    (if-not (#{:dir :file} (:type input))

      tree

      (loop [updated-tree  (ensure-path-exists tree path)
             sub-tree      tree
             current-path  path]
        (if-let [path-segment (first current-path)]
          ()))))

  (defn build-tree [inputs]
    (loop [remaining-inputs inputs
           tree             {}
           path             []
           ran-ls           false] ;; technically don't need for given input

      (if-let [input (first remaining-inputs)]

        (let [updated-path (update-path path input)
              _            (println updated-path)
              updated-tree (update-tree tree input updated-path)
              updated-ls   (update-ran-ls input ran-ls)]
          (recur (rest remaining-inputs)
                 updated-tree
                 updated-path
                 updated-ls))

        tree)))

  (let [inputs (parse-input example-input)
        tree   (build-tree inputs)]
    tree)

  ;; tree
  ;; {:name "/" :children [{:name "sth.txt" :type :file } {...}
  ;;                       {:name "d" :children []}]}

  )
