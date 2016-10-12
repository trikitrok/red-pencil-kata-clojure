(ns red-pencil.core)

(defn change-price [good new-price]
  (let [price (:price good)]
    (if (< new-price price)
      (merge good {:price new-price :on-promotion true})
      (assoc good :price new-price))))
