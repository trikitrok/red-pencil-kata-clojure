(ns red-pencil.promotions-identification)

(def ^:private reduction-ratio-range [0.05 0.3])

(defn- reduction-ratio [old-price price]
  (/ (- old-price price) old-price))

(defn- price-reduction-in-range?
  [{old-price :figure} {price :figure} [min-reduction-ratio max-reduction-ratio]]
  (let [reduction-ratio (reduction-ratio old-price price)]
    (and (>= reduction-ratio min-reduction-ratio)
         (<= reduction-ratio max-reduction-ratio))))

(defn- price-reduction? [{price :figure} {new-price :figure}]
  (< new-price price))

(defn on-promotion? [good]
  (let [price (:price good)
        old-price (-> good :previous-prices last)]
    (and (price-reduction? old-price price)
       (price-reduction-in-range? old-price price reduction-ratio-range))))
