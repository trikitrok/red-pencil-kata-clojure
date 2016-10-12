(ns red-pencil.core)

(defn- on-promotion? [{:keys [price]} new-price]
  (< new-price price))

(defn change-price [good new-price]
  (-> good
      (assoc :price new-price)
      (assoc :on-promotion (on-promotion? good new-price))))
