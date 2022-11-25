(ns org.jakehoward.aoc.day-10
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as string]))

(def data-lines (-> (utils/get-input 10)
                    (utils/lines)))

(def data (map #(string/split % #"") data-lines))

(def matching-pairs {\{ \}, \[ \], \( \), \< \>})
(def reverse-matching-pairs (->> matching-pairs
                                 (map reverse)
                                 (map vec)
                                 (into {})))

(defn update-tally [tally char]
  (let [updated-tally (if (contains? matching-pairs char)
                       (update tally char inc)
                       (update tally (reverse-matching-pairs char) dec))]
    (println "char:" char "tally:" updated-tally)
    updated-tally))

(defn get-offending-char [tally]
  (->> tally
       (some #(when (< (second %) 0) %))
       first
       matching-pairs))

(defn first-illegal-character [chars]
  (loop [tally            {\{ 0, \[ 0, \( 0, \< 0}
         remaining-chars  (seq chars)]

    (let [offending-char (get-offending-char tally)
          char (first remaining-chars)]

      (cond
        (some? offending-char)  offending-char
        (not (some? char))      nil
        :else                   (recur (update-tally tally char) (rest remaining-chars))))))

(def points {\) 3 \] 57 \} 1197 \> 25137})

(defn play []
  (let [illegal-chars (keep first-illegal-character data)
        score (reduce + (map points illegal-chars))]
    score))

(comment
  (let [test-input "{([(<{}[<>[]}>{[]{[(<()>"]
    (first-illegal-character test-input))

  (let [test-input ["[({(<(())[]>[[{[]{<()<>>"
                    "[(()[<>])]({[<{<<[]>>("
                    "{([(<{}[<>[]}>{[]{[(<()>"
                    "(((({<>}<{<{<>}{[]{[]{}"
                    "[[<[([]))<([[{}[[()]]]"
                    "[{[{({}]{}}([{[{{{}}([]"
                    "{<[[]]>}<{[{[{[]{()[[[]"
                    "[<(<(<(<{}))><([]([]()"
                    "<{([([[(<>()){}]>(<<{{"
                    "<{([{{}}[<[[[<>{}]]]>[]]"]]
    (map first-illegal-character test-input))

  (let [examples ["{([(<{}[<>[]}>{[]{[(<()>" ;; Expected ], but found } instead.
                  "[[<[([]))<([[{}[[()]]]"   ;; Expected ], but found ) instead.
                  "[{[{({}]{}}([{[{{{}}([]"  ;; Expected ), but found ] instead.
                  "[<(<(<(<{}))><([]([]()"   ;; Expected >, but found ) instead.
                  "<{([([[(<>()){}]>(<<{{"   ;; Expected ], but found > instead.
                  ]]
    (map first-illegal-character (take 1 examples)))

  (last data)
  ;; => 126882
  (play))
