(ns red-pencil.price
  (:require
    [red-pencil.days :as days]))

(defn make [figure change-ts]
  {:figure figure
   :change-ts change-ts})

(defn reduction? [{previous-price :figure} {price :figure}]
  (< price previous-price))

(defn duration-in-days [{previous-price-ts :change-ts} {new-price-ts :change-ts}]
  (days/from-ms (- new-price-ts previous-price-ts)))

(defn reduction-ratio [{previous-price :figure} {price :figure} ]
  (/ (- previous-price price) previous-price))


