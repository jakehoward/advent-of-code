(ns org.jakehoward.aoc.days.day-7-again
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as string]))

(defn parse-line [line]
  (string/starts-with?))

(defn part-1 [input]
  (let [raw-lines (utils/lines input)
        ans       raw-lines]
    (count ans)))

(comment
  (part-1 example-input)
  (part-1 (utils/get-input 7))

  

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

