(ns org.jakehoward.aoc.days.day-10
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as str]))

(defn parse-input [input]
  (->> (utils/lines input)
       (map #(str/split % #"\s+"))
       (map (fn [[cmd arg]] {:cmd (keyword cmd) :arg (if arg (Integer/parseInt arg) arg)}))))

(defn update-cycle->X [cycle->X current-value instr]
  (if (= :addx (:cmd instr))
    (-> cycle->X
        (conj current-value)
        (conj (+ current-value (:arg instr))))
    (conj cycle->X current-value)))

(defn process-instructions [all-instructions]
  (loop [instructions  all-instructions
         cycle->X      [1]]
    (if-let [instr (first instructions)]
      (let [current-value    (last cycle->X)
            updated-cycle->X (update-cycle->X cycle->X current-value instr)]
        (recur (rest instructions) updated-cycle->X))
      cycle->X)))

(defn part-1 [input]
  (let [all-instructions (parse-input input)
        cycle->X         (process-instructions all-instructions)
        signal-strength  (fn [n] (* n (nth cycle->X (dec n))))]
    (+
     (signal-strength 20)
     (signal-strength 60)
     (signal-strength 100)
     (signal-strength 140)
     (signal-strength 180)
     (signal-strength 220))))

(defn draw [cycle X]
  (if (#{(dec X) X (inc X)} (mod cycle 40))
    "#"
    "."))

(defn part-2 [input]
  (let [all-instructions (parse-input input)
        cycle->X         (process-instructions all-instructions)
        crt-output       (->> cycle->X
                              (map-indexed draw)
                              (partition 40)
                              (map #(str/join "" %)))]
    (println (str/join "\n" crt-output))))

(comment
  (part-1 (utils/get-input "10-ex"))
  ;; => 15660 your answer is too high
  ;; => 14540 ("during" the nth cycle is important..!)
  (part-1 (utils/get-input "10"))
  (part-2 (utils/get-input "10"))
  (process-instructions (parse-input (utils/get-input "10-ex")))
  (process-instructions (parse-input example-input))
  (def example-input
    (-> "
addx 10
noop
addx 10"
        (str/triml)))
  ;;
  )

