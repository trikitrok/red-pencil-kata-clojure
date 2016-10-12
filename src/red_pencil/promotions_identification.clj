(ns red-pencil.promotions-identification
  (:require
    [red-pencil.days :as days]))

(def ^:private reduction-ratio-range [0.05 0.3])

(def ^:private minimum-price-duration 30)

(def ^:private maximum-promotion-duration 30)

(defn- reduction-ratio [old-price price]
  (/ (- old-price price) old-price))

(defn- price-reduction-in-range?
  [{old-price :figure} {price :figure} [min-reduction-ratio max-reduction-ratio]]
  (let [reduction-ratio (reduction-ratio old-price price)]
    (and (>= reduction-ratio min-reduction-ratio)
         (<= reduction-ratio max-reduction-ratio))))

(defn- price-reduction? [{old-price :figure} {price :figure}]
  (< price old-price))

(defn- price-duration-in-days [{old-price-ts :change-ts} {new-price-ts :change-ts}]
  (days/from-ms (- new-price-ts old-price-ts)))

(defn- old-price-stable-enough? [old-price price]
  (>= (price-duration-in-days old-price price) minimum-price-duration))

(defn- promotion-still-lasts? [{:keys [change-ts]} query-ts]
  (<= (days/from-ms (- query-ts change-ts)) maximum-promotion-duration))

(defn- price-before-old-price [good]
  (->> good :previous-prices (take-last 2) first))

(defn on-promotion? [good query-ts]
  (let [price (:price good)
        old-price (-> good :previous-prices last)]
    (if (old-price-stable-enough? old-price price)
      (and (price-reduction? old-price price)
           (price-reduction-in-range? old-price price reduction-ratio-range)
           (promotion-still-lasts? price query-ts))

      (and (price-reduction? (price-before-old-price good) old-price)
           (price-reduction-in-range? (price-before-old-price good) old-price reduction-ratio-range)
           (promotion-still-lasts? old-price query-ts))
      )
    ))
