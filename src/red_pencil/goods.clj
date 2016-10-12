(ns red-pencil.goods)

(defn change-price [good new-price]
  (-> good
      (update :previous-prices conj (:price good))
      (assoc :price new-price)))
