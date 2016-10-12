(ns red-pencil.promotions-identification
  (:require
    [red-pencil.days :as days]))

(def ^:private reduction-ratio-range [0.05 0.3])

(def ^:private minimum-price-duration 30)

(def ^:private maximum-promotion-duration 30)

(defn- reduction-ratio [previous-price price]
  (/ (- previous-price price) previous-price))

(defn- price-reduction-in-range?
  [{previous-price :figure} {price :figure} [min-reduction-ratio max-reduction-ratio]]
  (let [reduction-ratio (reduction-ratio previous-price price)]
    (and (>= reduction-ratio min-reduction-ratio)
         (<= reduction-ratio max-reduction-ratio))))

(defn- price-reduction? [{previous-price :figure} {price :figure}]
  (< price previous-price))

(defn- price-duration-in-days [{previous-price-ts :change-ts} {new-price-ts :change-ts}]
  (days/from-ms (- new-price-ts previous-price-ts)))

(defn- previous-price-stable-enough? [previous-price price]
  (>= (price-duration-in-days previous-price price) minimum-price-duration))

(defn- promotion-still-lasts? [{:keys [change-ts]} query-ts]
  (<= (days/from-ms (- query-ts change-ts)) maximum-promotion-duration))

(defn- price-before-previous-price [good]
  (->> good :previous-prices (take-last 2) first))

(defn- activates-promotion? [previous-price price query-ts]
  (and (price-reduction? previous-price price)
       (price-reduction-in-range? previous-price price reduction-ratio-range)
       (promotion-still-lasts? price query-ts)))

(defn- overall-reduction-in-range?
  [{original-price :figure} {price :figure} max-reduction-ratio]
  (<= (reduction-ratio original-price price) max-reduction-ratio))

(defn original-price [good]
  (-> good :previous-prices first))

(defn on-promotion? [good query-ts]
  (let [price (:price good)
        previous-price (-> good :previous-prices last)]
    (if (previous-price-stable-enough? previous-price price)
      (activates-promotion? previous-price price query-ts)
      (and (price-reduction? previous-price price)
           (overall-reduction-in-range? (original-price good) price 0.3)
           (activates-promotion? (price-before-previous-price good) previous-price query-ts)))))
