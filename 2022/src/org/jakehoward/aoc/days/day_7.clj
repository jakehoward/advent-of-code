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
    {:type :file :name filename :size (Integer/parseInt size)}))

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
         (= (:cmd input) "cd")) (cond
                                  (= (:arg input) "..") (pop path)
                                  ;; (= (:arg input) "/")  ["/"]     ;; this is probably buggy as hell...
                                  :else                 (conj path (:arg input)))
    :else                       path))

(defn update-path-to-files [path-to-files current-path input]
  (if (= :file (:type input))
    (assoc
     path-to-files
     current-path
     (conj (or (path-to-files current-path) []) input)) ;; todo: make it a set?
    path-to-files))

(defn build-path-to-files [inputs]
  (loop [remaining-inputs  inputs
         current-path      []
         path-to-files     {}]
    (println current-path)
    (if-let [input (first remaining-inputs)]
      (let [updated-path          (update-path current-path input)
            updated-path-to-files (update-path-to-files path-to-files updated-path input)]
        (recur (rest remaining-inputs)
               updated-path
               updated-path-to-files))
      path-to-files)))

(defn get-all-files-incl-subdir [path path-to-files]
  (let [dirs      (->> path-to-files
                       (filter (fn [[p files]]
                                 (let [possible-overlap (take (count path) p)]
                                   (and (= possible-overlap path)
                                        (>= (count p) (count path)))))))
        dir-files (->> dirs
                       (mapcat second))]
    ;; (concat files subdir-files)
    dir-files))

(defn sum-file-sizes [files]
  (->> files
       (map :size)
       (reduce +)))

(defn sum-dir-sizes [path-to-files]
  (->> path-to-files
       (map (fn [[path files]] [path (get-all-files-incl-subdir path path-to-files)]))
       (map (fn [[path files]] [path (sum-file-sizes files)]))
       (into {})))

(defn part-1 []
  (let [inputs            (parse-input (utils/get-input 7))
        path-to-files     (build-path-to-files inputs)
        dir-sizes         (sum-dir-sizes path-to-files)
        under-100k        (->> dir-sizes
                               (filter (fn [[path size]] (<= size 100000)))
                               (into {}))
        under-100k-sizes  (map second under-100k)]
    (reduce + under-100k-sizes)
    path-to-files
    ;; dir-sizes
    ))

(defn get-dir-sizes [path-to-files]
  (->> path-to-files
       (map (fn [[p files]] [p (reduce + (map :size files))]))
       (into {})))

(defn is-subdir? [path parent-path]
  (and
   (every? (fn [[a b]] (= a b)) (map vector path parent-path))
   (> (count path) (count parent-path))))


(defn get-total-dir-size [path dir-sizes]
  (let [this-size (dir-sizes path)
        sub-dirs  (filter #(is-subdir? (first %) path) dir-sizes)
        sub-dirs-size (reduce + (map second sub-dirs))]
    (+ this-size sub-dirs-size)))

(defn get-total-dir-sizes [dir-sizes]
  (->> dir-sizes
       (map #(get-total-dir-size (first %) dir-sizes))
       (into {})))

(defn part-1-v2 [input]
  (let [inputs            (parse-input input)
        path-to-files     (build-path-to-files inputs)
        dir-sizes         (get-dir-sizes path-to-files)
        total-dir-sizes   (get-total-dir-sizes dir-sizes)
        ;; under-100k        (->> dir-sizes
                               ;; (filter (fn [[path size]] (<= size 100000)))
                               ;; (into {}))
        ;; under-100k-sizes  (map second under-100k)
        ]
    dir-sizes
    total-dir-sizes
    ;; path-to-files
    ))

(comment
  (part-1-v2 example-input-2)
  (part-1)
  (play)
  (def check-dir-sizes (part-1))
  (def check-ptf (part-1))
  (count (get-all-files-incl-subdir ["/"] check-ptf))
  (->> (parse-input (utils/get-input 7))
       (filter #(= (:type %) :file))
       (map :size)
       (reduce +))
  (check-dir-sizes ["/"]) ;; 46090134
  (reduce + (vals (dissoc check-dir-sizes ["/"])))
  ;; => 1375786 ;; => 1375786
  (->> (part-1)
       (filter (fn [[path files]] ((set path) "zmj"))))

  (let [inputs     (parse-input example-input)
        ptf        (build-path-to-files inputs)
        dir-sizes  (sum-dir-sizes ptf)
        under-100k (->> dir-sizes
                        (filter (fn [[path size]] (<= size 100000)))
                        (into {}))
        under-100k-sizes (map second under-100k)]
    (reduce + under-100k-sizes))
  (def example-input-2 "$ cd /
$ ls
dir a
1 b.txt
1 c.dat
dir d
$ cd a
$ ls
dir e
1 f
1 g
1 h.lst
$ cd e
$ ls
1 i
$ cd ..
$ cd ..
$ cd d
$ ls
1 j
1 d.log
1 d.ext
1 k")
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
  '({:type :cmd, :cmd "cd", :arg "/"}
    {:type :cmd, :cmd "ls", :arg nil}
    {:type :dir, :name "a"}
    {:type :file, :name "b.txt", :size 14848514}
    {:type :file, :name "c.dat", :size 8504156}
    {:type :dir, :name "d"}
    {:type :cmd, :cmd "cd", :arg "a"}
    {:type :cmd, :cmd "ls", :arg nil}
    {:type :dir, :name "e"}
    {:type :file, :name "f", :size 29116}
    {:type :file, :name "g", :size 2557}
    {:type :file, :name "h.lst", :size 62596}
    {:type :cmd, :cmd "cd", :arg "e"}
    {:type :cmd, :cmd "ls", :arg nil}
    {:type :file, :name "i", :size 584}
    {:type :cmd, :cmd "cd", :arg ".."}
    {:type :cmd, :cmd "cd", :arg ".."}
    {:type :cmd, :cmd "cd", :arg "d"}
    {:type :cmd, :cmd "ls", :arg nil}
    {:type :file, :name "j", :size 4060174}
    {:type :file, :name "d.log", :size 8033020}
    {:type :file, :name "d.ext", :size 5626152}
    {:type :file, :name "k", :size 7214296}))
