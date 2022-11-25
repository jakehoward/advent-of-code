(ns org.jakehoward.aoc.day-10
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as string]))

(def data (-> (utils/get-input 10)
              (utils/lines)))

(def matching-pairs {\{ \}, \[ \], \( \), \< \>})
(def reverse-matching-pairs (->> matching-pairs
                                 (map reverse)
                                 (map vec)
                                 (into {})))

(defn is-opening? [char]
  (boolean (matching-pairs char)))

(defn first-illegal-character [chars]
  (loop [remaining-chars (seq chars)
         char-stack      []]
    (let [char (first remaining-chars)]
      (cond
        (not (some? char))   nil
        ;; (not (#{\{ \}, \[ \], \( \), \< \>} char)) (throw (Exception. (str "Err, no:" char)))

        (is-opening? char)   (recur (rest remaining-chars) (conj char-stack char))

        ;; we assume...
        :is-closing          (if (= (reverse-matching-pairs char)
                                    (peek char-stack))
                               (recur (rest remaining-chars) (pop char-stack))
                               char)))))

(defn get-missing-chars [chars]
  (loop [remaining-chars (seq chars)
         char-stack      []]
    (let [char (first remaining-chars)]
      (cond
        (not (some? char)) (reverse (map matching-pairs char-stack))
        ;; (not (#{\{ \}, \[ \], \( \), \< \>} char)) (throw (Exception. (str "Err, no:" char)))

        (is-opening? char)   (recur (rest remaining-chars) (conj char-stack char))

        ;; we assume...
        :is-closing          (if (= (reverse-matching-pairs char)
                                    (peek char-stack))
                               (recur (rest remaining-chars) (pop char-stack))
                               (throw
                                (Exception. "Don't call me with corrupted lines, dude")))))))

(def points {\) 3 \] 57 \} 1197 \> 25137})

(defn play []
  (let [illegal-chars (keep first-illegal-character data)
        score (reduce + (map points illegal-chars))]
    score))

(def points-pt2 {\) 1 \] 2 \} 3  \> 4})

(defn score-pt2 [chars]
  (reduce (fn [acc char] (+ (points-pt2 char) (* 5 acc))) 0 chars))

(defn play-pt2 []
  (let [incomplete-lines (filter #(nil? (first-illegal-character %)) data)
        completions      (map get-missing-chars incomplete-lines)
        scores           (map score-pt2 completions)
        sorted-scores    (sort scores)
        middle-score     (first (drop (int (/ (count sorted-scores) 2)) sorted-scores))]
    middle-score))

(comment
  (let [test-input ["[({(<(())[]>[[{[]{<()<>>" ;; Complete by adding }}]])})].
                    ]]
    (map get-missing-chars test-input))
  ;; 2801302861
  (play-pt2)

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

  ;; it's a stack where the closing
  ;; character can pop an item off the
  ;; stack, if it's not closing then
  ;; it's illegal
  (let [examples ["{([(<{}[<>[]}>{[]{[(<()>" ;; Expected ], but found } instead.
                  "[[<[([]))<([[{}[[()]]]"   ;; Expected ], but found ) instead.
                  "[{[{({}]{}}([{[{{{}}([]"  ;; Expected ), but found ] instead.
                  "[<(<(<(<{}))><([]([]()"   ;; Expected >, but found ) instead.
                  "<{([([[(<>()){}]>(<<{{"   ;; Expected ], but found > instead.
                  ]]
    (map first-illegal-character
         ;; (take 1 (drop 1 examples))
         examples))

  (last data)
  ;; => 341823
  (play))
