(ns org.jakehoward.aoc.day-4
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as string]))

(def board-width 5)

(defprotocol Markable
  (mark [this]))

;; built in interface to override only toString?
(defprotocol Printable
  (asString [this]))

(defprotocol Grid
  (columns [this])
  (rows [this]))

(defrecord Cell [value marked]
  Markable
  (mark [this] (->Cell value true))
  Printable
  (asString [this] (str value (when marked "*"))))

(defrecord Board [cells width]
  Grid
  (rows [this] (partition width cells))
  (columns [this] (apply map vector (.rows this)))
  Printable
  (asString [this] (let [str-rows (->> cells
                                       (map #(.asString %))
                                       (partition width)
                                       (map #(string/join " " %)))]
                     (string/join "\n" str-rows))))

;; ============
;; data parsing
;; ============
(def data (utils/get-input 4))

(def bingo-numbers (-> data
                       utils/lines
                       first
                       (string/split #",")
                       ((fn [nums]
                          (map #(Integer/parseInt %) nums)))))

(def board-numbers
  (let [board-strings       (->> (string/split data #"\n\n")
                                 (drop 1))
        get-board-numbers   (fn [board-string] (->> (string/split board-string #"\s+")
                                                    (filter seq)
                                                    (map #(Integer/parseInt %))))]
    (map get-board-numbers board-strings)))

(def boards (->> board-numbers
                 (map (fn [cell-nums] (map #(->Cell % false) cell-nums)))
                 (map #(->Board % board-width))))

(defn print-board [board]
  (println "\n" (.asString board)))

;; ========
;; gameplay
;; ========
(defn bingo? [board]
  (-> (or (some true? (map (fn [row] (every? :marked row)) (.rows board)))
          (some true? (map (fn [col] (every? :marked col)) (.columns board))))
      boolean))

(defn mark-board [board number]
  (->Board (map (fn [cell]
                  (if (= number (.value cell))
                    (.mark cell)
                    cell))
                (.cells board))
           (.width board)))

(defn score [board last-num-called]
  (let [unmarked-cells (filter #(not (:marked %)) (.cells board))
        values         (map :value unmarked-cells)
        sum-total      (reduce + values)]
    (*
     sum-total
     last-num-called)))

(defn play []
  ;; outcomes is a lazy seq so can define
  ;; all outcomes but not realise all of them
  ;; (map #(mark-board % number) boards)
  ;; (let [outcomes (for [number bingo-numbers board boards] (mark-board board number))])
  (loop [bs boards
         ns bingo-numbers]

    (let [n           (first ns)
          updated-bs  (map #(mark-board % n) bs)
          winner      (->> updated-bs
                           (drop-while #(not (bingo? %)))
                           first)]
      (if winner
        (score winner n)
        (recur updated-bs (rest ns))))))

;; (play) ;; => 87456

(defn play-to-last-board []
  (loop [bs boards
         ns bingo-numbers]

    (let [n           (first ns)
          updated-bs  (map #(mark-board % n) bs)
          remaining   (filter #(not (bingo? %)) updated-bs)]
      (if (= 1 (count remaining))
        {:board (first remaining) :numbers ns}
        (recur remaining (rest ns))))))

(defn play-pt2 []
  (let [{:keys [board numbers]} (play-to-last-board)]
    (loop [b   board
           ns  numbers]
      (let [updated-board (mark-board b (first ns))]
        (if (bingo? updated-board)
          (score updated-board (first ns))
          (recur updated-board (rest ns)))))))

;; (def last-board (play-to-last-board))
;; (score (first (drop-while #(not (bingo? %)) (map #(mark-board (:board last-board) %) (:numbers last-board)))) 10)
(play-pt2) ;; => 15561

(comment
  (-> (first boards)
      ;; row
      (mark-board 38)
      (mark-board 80)
      (mark-board 60)
      (mark-board 23)
      (mark-board 82)
      ;; cols
      (mark-board 38)
      (mark-board 25)
      (mark-board 40)
      (mark-board 32)
      ;; (mark-board 13)
      bingo?
      ;; print-board
      )
  ;; even better...
  (-> (reduce mark-board (first boards) [38 80 60 23 82])
      bingo?)

  (-> (reduce mark-board (first boards) [38 80 60 23 82])
      (score 10))

  (reduce + [38 80 60 23 82])

  (println "\n" (.asString (mark-board (first boards) 99))))
