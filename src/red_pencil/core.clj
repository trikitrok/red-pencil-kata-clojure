(ns red-pencil.core)

(defn- reduction-ratio [price new-price]
  (/ (- price new-price) price))

(def ^:private reduction-ratio-range [0.05 0.3])

(defn- price-reduction-in-range? [{price :figure} {new-price :figure} [min-reduction-ratio max-reduction-ratio]]
  (let [reduction-ratio (reduction-ratio price new-price)]
    (and (>= reduction-ratio min-reduction-ratio)
         (<= reduction-ratio max-reduction-ratio))))

(defn- price-reduction? [{price :figure} {new-price :figure}]
  (< new-price price))

(defn- old-price-stable-enough? [{old-price-ts :change-ts} {new-price-ts :change-ts}]
  (let [ms-in-day (* 24 60 60 1000)]
    (> (/ (- new-price-ts old-price-ts) ms-in-day) 30)))

(defn- on-promotion? [{:keys [price]} new-price]
  (and (price-reduction? price new-price)
       (price-reduction-in-range? price new-price reduction-ratio-range)
       (old-price-stable-enough? price new-price)))

(defn change-price [good new-price]
  (-> good
      (assoc :price new-price)
      (assoc :on-promotion (on-promotion? good new-price))))
