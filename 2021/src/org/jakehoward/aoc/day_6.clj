(ns org.jakehoward.aoc.day-6
  (:require [org.jakehoward.aoc.utils :as utils]
            [clojure.string :as string]))


(def initial-fish (->> (string/split (utils/get-input 6) #",")
                       (map #(Integer/parseInt %))))

(defn tick [fish]
  (cond
    (= 0 fish) [6 8]
    :else [(dec fish)]))


(defn solve [num-days]
  (loop [fishies initial-fish
         days num-days]
    (if (= days 0)
      (count fishies)
      (recur (mapcat tick fishies) (dec days)))))


(defn faster-solve [num-days]
  (let [grouped    (group-by identity initial-fish)
        start-fish {0 0 1 0 2 0 3 0 4 0 5 0 6 0 7 0 8 0}
        start-fish (into start-fish
                         (map (fn [[fish-age fish]] [fish-age (count fish)])
                              grouped))]
    (loop [fishies start-fish
           days num-days]
      (if (= days 0)
        (reduce + (vals fishies))
        (recur (-> fishies
                   (assoc 0 (get fishies 1))
                   (assoc 1 (get fishies 2))
                   (assoc 2 (get fishies 3))
                   (assoc 3 (get fishies 4))
                   (assoc 4 (get fishies 5))
                   (assoc 5 (get fishies 6))
                   (assoc 6 (+ (get fishies 7)
                               (get fishies 0)))
                   (assoc 7 (get fishies 8))
                   (assoc 8 (get fishies 0)))
               (dec days))))))



(comment
  (solve 80) ;; => 345387

  (time (faster-solve 256));; => 1574445493136
  (time (solve 50))

  (faster-solve 0) ;; {0 0,  7 0, 1 69, 4 67, 6 0, 3 45, 2 60, 5 59, 8 0}
  (faster-solve 1) ;; {0 69, 7 0, 1 60, 4 59, 6 0, 3 67, 2 45, 5 0,  8 0}
  (faster-solve 2) ;; {0 60, 7 0, 1 45, 4 0, 6 69, 3 59, 2 67, 5 0, 8 0} ;;

  (mapcat tick [1 0 3]))
