(ns red-pencil.core)

(defn- reduction-percentage [price new-price]
  (/ (- price new-price) 100.0))

(defn- on-promotion? [{:keys [price]} new-price]
  (and (< new-price price)
       (>= (reduction-percentage price new-price) 0.1)))

(defn change-price [good new-price]
  (-> good
      (assoc :price new-price)
      (assoc :on-promotion (on-promotion? good new-price))))
