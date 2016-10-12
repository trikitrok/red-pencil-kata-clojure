(ns red-pencil.days)

(def ^:private ms-in-day (* 24.0 60 60 1000))

(defn to-ms [num-days]
  (* num-days ms-in-day))

(defn from-ms [ms]
  (/ ms ms-in-day))

