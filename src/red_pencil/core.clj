(ns red-pencil.core)

(defn- reduction-ratio [price new-price]
  (/ (- price new-price) price))

(defn- get-price-figure [price]
  price)

(defn- update-price-figure [new-price price]
  new-price)

(def ^:private reduction-ratio-range [0.05 0.3])

(defn- reduction-in-range? [price new-price [min-reduction-ratio max-reduction-ratio]]
  (let [reduction-ratio (reduction-ratio price new-price)]
    (and (>= reduction-ratio min-reduction-ratio)
         (<= reduction-ratio max-reduction-ratio))))

(defn- on-promotion? [{:keys [price]} new-price]
  (and (< new-price (get-price-figure price))
       (reduction-in-range? (get-price-figure price) new-price reduction-ratio-range)))

(defn change-price [good new-price]
  (-> good
      (update :price (partial update-price-figure new-price))
      (assoc :on-promotion (on-promotion? good new-price))))
