(ns red-pencil.promotions-identification
  (:require
    [red-pencil.days :as days]
    [red-pencil.price :as price]))

(def ^:private max-reduction-ratio 0.3)

(def ^:private reduction-ratio-range [0.05 max-reduction-ratio])

(def ^:private minimum-price-duration 30)

(def ^:private maximum-promotion-duration 30)

(defn price-reduction-in-range?
  [previous-price price [min-reduction-ratio max-reduction-ratio]]
  (let [reduction-ratio (price/reduction-ratio previous-price price)]
    (and (>= reduction-ratio min-reduction-ratio)
         (<= reduction-ratio max-reduction-ratio))))

(defn- previous-price-stable-enough? [previous-price price]
  (>= (price/duration-in-days previous-price price) minimum-price-duration))

(defn- promotion-still-lasts? [{:keys [change-ts]} query-ts]
  (<= (days/from-ms (- query-ts change-ts)) maximum-promotion-duration))

(defn- overall-reduction-in-range? [original-price price]
  (<= (price/reduction-ratio original-price price) max-reduction-ratio))

(defn original-price [good]
  (-> good :previous-prices first))

(defn- next-price-activating-promotion [initial-price price-activating-promotion price]
  (let [previous-price (or price-activating-promotion initial-price)]
    (if (and (previous-price-stable-enough? previous-price price)
             (price/reduction? previous-price price)
             (price-reduction-in-range? previous-price price reduction-ratio-range))
      price
      previous-price)))

(defn- find-price-activating-promotion [previous-prices]
  (reduce (partial next-price-activating-promotion (first previous-prices))
          nil
          (rest previous-prices)))

(defn- promotion-already-activated? [price-activating-promotion]
  (not (nil? price-activating-promotion)))

(defn on-promotion? [good query-ts]
  (let [price (:price good)
        original-price (original-price good)
        price-activating-promotion (find-price-activating-promotion (:previous-prices good))]

    (if (promotion-already-activated? price-activating-promotion)
      (and (promotion-still-lasts? price-activating-promotion query-ts)
           (price/reduction? price-activating-promotion price)
           (overall-reduction-in-range? original-price price))
      (and (promotion-still-lasts? price query-ts)
           (price/reduction? original-price price)
           (price-reduction-in-range? original-price price reduction-ratio-range)
           (previous-price-stable-enough? original-price price)))))
