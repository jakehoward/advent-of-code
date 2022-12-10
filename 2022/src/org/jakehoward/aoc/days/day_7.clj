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

(defn calc-dir-sizes [file-tree]
  (loop [dirs-to-search [{:path ["/"] :dirs (.get-children file-tree)}]
         sizes          {}]
    (if-let [dir-to-search (first dirs-to-search)]
      (let [{:keys [path dirs]} dir-to-search
            dir-children        (filter #(= Dir (type %)) dirs)
            file-children       (filter #(= File (type %)) dirs)
            child-dirs (->> dir-children
                            (map (fn [dir] {:path (conj path (:name dir))
                                            :dirs (.get-children dir)})))]
        (recur (concat (rest dirs-to-search) child-dirs)
               (assoc sizes (:path dir-to-search) (->> file-children
                                                       (map :size)
                                                       (reduce +)))))
      sizes)))

(defn- is-subdir? [base dir]
  (and (< (count base) (count dir))
       (= (take (count base) dir) base)))

(defn- calc-total-dir-sizes [path->size]
  (for [[path size]  path->size]
    (let [other-sizes (for [[other-path other-size] path->size
                            :when (is-subdir? path other-path)]
                        other-size)]
      [path (+ size (reduce + other-sizes))])))

(defn part-1-tree [raw-input]
  (let [inputs          (map parse-input (utils/lines raw-input))
        file-tree       (build-file-tree inputs)
        path->size      (calc-dir-sizes file-tree)
        total-sizes     (calc-total-dir-sizes path->size)
        sizes-up-to100k (filter (fn [[path size]] (<= size (* 100 1000))) total-sizes)
        ans             (reduce + (map second sizes-up-to100k))]
    sizes-up-to100k
    total-sizes
    ans))

(defn part-2-tree [raw-input]
  (let [inputs          (map parse-input (utils/lines raw-input))
        file-tree       (build-file-tree inputs)
        path->size      (calc-dir-sizes file-tree)
        total-sizes     (into {} (calc-total-dir-sizes path->size))
        total-space     (* 70 1000 1000)
        req-space       (* 30 1000 1000)
        used-space      (get total-sizes ["/"])
        free-space      (- total-space used-space)
        min-size-to-del (- req-space free-space)
        del-candidates  (->> total-sizes
                             (filter (fn [[path size]] (>= size min-size-to-del)))
                             (sort-by second))]
    (second (first del-candidates))))

(comment
  (part-1 example-input) ;; => 95437
  (part-1 (utils/get-input 7)) ;; => 1375786

  (part-1-tree example-input)
  (part-1-tree (utils/get-input 7)) ;; => 1491614

  (part-2-tree example-input)

  ;; => 34257857 your answer is too high
  ;; => 6400111
  (part-2-tree (utils/get-input 7))

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
