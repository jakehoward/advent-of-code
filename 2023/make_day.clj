#!/usr/bin/env bb
(require '[babashka.fs :as fs])

(def day (Integer/parseInt (first *command-line-args*)))

(def file-name (str "day_" (format "%02d" day) ".clj"))

(def file-contents (-> (slurp (str (fs/parent *file*) "/day_n.clj"))
                       (str/replace #"\{\{n\}\}" (str day))
                       (str/replace #"\{\{ns-n\}\}" (format "%02d" day))))

(def write-path (str (fs/canonicalize (str (fs/parent *file*) "/src/aoc/" file-name))))
(prn "Making" write-path)
(spit write-path file-contents)
