(ns red-pencil.core)

(defn change-price [good new-price]
  (assoc good :price new-price))
