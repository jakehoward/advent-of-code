(ns aoc.utils
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn get-input [day]
  (-> (format "day-%s.txt" day)
      io/resource
      slurp
      str/trim))

(defn parse-int [s] (Integer/parseInt s))

(defn sum [xs] (reduce + 0 xs))
