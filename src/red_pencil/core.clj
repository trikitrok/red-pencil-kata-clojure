(ns red-pencil.core
  (:require
    [red-pencil.days :as days]))

(defn- reduction-ratio [price new-price]
  (/ (- price new-price) price))

(def ^:private reduction-ratio-range [0.05 0.3])

(defn- price-reduction-in-range?
  [{price :figure} {new-price :figure} [min-reduction-ratio max-reduction-ratio]]
  (let [reduction-ratio (reduction-ratio price new-price)]
    (and (>= reduction-ratio min-reduction-ratio)
         (<= reduction-ratio max-reduction-ratio))))

(defn- price-reduction? [{price :figure} {new-price :figure}]
  (< new-price price))

(defn- price-duration-in-days [{old-price-ts :change-ts} {new-price-ts :change-ts}]
  (days/from-ms (- new-price-ts old-price-ts)))

(defn- old-price-stable-enough? [price new-price]
  (> (price-duration-in-days price new-price) 30))

(defn- on-promotion? [{:keys [price]} new-price]
  (and (price-reduction? price new-price)
       (price-reduction-in-range? price new-price reduction-ratio-range)
       (old-price-stable-enough? price new-price)))

(defn change-price [good new-price]
  (-> good
      (assoc :price new-price)
      (assoc :on-promotion (on-promotion? good new-price))))
