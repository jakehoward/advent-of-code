(ns org.jakehoward.aoc.days.day-2
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as string]))

(def input-to-game {"A" :rock
                    "B" :paper
                    "C" :scissors
                    "X" :rock
                    "Y" :paper
                    "Z" :scissors})

(def winning-states #{[:rock     :paper]
                    [:paper     :scissors]
                    [:scissors  :rock]})

(def points {:rock 1
             :paper 2
             :scissors 3
             :lose 0
             :draw 3
             :win 6})

(def input (->> (utils/get-input 2)
                (utils/lines)
                (map #(string/split % #" "))
                (map #(mapv input-to-game %))))

(defn round-outcome [[elf you]]
  (cond
    (= elf you)                :draw
    (winning-states [elf you]) :win
    :else                      :lose))

(defn play []
  (let [input-with-outcome  (map (fn [[elf you]] [elf you (round-outcome [elf you])]) input)
        scores              (map (fn [[_ you outcome]] (+ (points you) (points outcome)))
                                 input-with-outcome)
        total               (reduce + scores)
        ]
    total))

;; pt2
(def input-to-game-pt2 {"A" :rock
                        "B" :paper
                        "C" :scissors
                        "X" :lose
                        "Y" :draw
                        "Z" :win})

(def input-pt2 (->> (utils/get-input 2)
                (utils/lines)
                (map #(string/split % #" "))
                (map #(mapv input-to-game-pt2 %))))

(def beat {:rock     :paper
           :paper    :scissors
           :scissors :rock})
(def yield-to (into {} (map (fn [[a b]] [b a])) beat))

(defn insert-your-choice [[elf outcome]]
  (cond
    (= :draw outcome) [elf elf             outcome]
    (= :win  outcome) [elf (beat elf)      outcome]
    (= :lose outcome) [elf (yield-to elf)  outcome]
    ))

(defn play-pt2 []
  (let [elf-you-outcome     (map insert-your-choice input-pt2)
        scores              (map (fn [[_ you outcome]] (+ (points you) (points outcome)))
                                 elf-you-outcome)
        total               (reduce + scores)]
    total))
(comment
  (play)      ;; => 8392
  (play-pt2)  ;; => 10116
  )


