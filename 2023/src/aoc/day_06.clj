(ns aoc.day-06)

(def example [{:time 7 :distance 9}
              {:time 15 :distance 40}
              {:time 30 :distance 200}])

(def example-pt2 {:time 71530 :distance 940200})

(def input  [{:time 48 :distance 296}
             {:time 93 :distance 1928}
             {:time 85 :distance 1236}
             {:time 95 :distance 1391}])

(def input-pt2 {:time 48938595 :distance 296192812361391})

(defn num-ways-to-win [{:keys [time distance]}]
  (let [ways         (range 0 time)
        winning-ways (filter #(> (* % (- time %)) distance) ways)]
    (count winning-ways)))

(defn pt1 [input]
  (reduce * 1 (map num-ways-to-win input)))

(defn pt2 [input]
  (num-ways-to-win input))

(comment
  (pt1 example)
  (pt1 input)
  (pt2 example-pt2)
  (time (pt2 input-pt2))
;
)
