(ns red-pencil.promotions-identification)

(defn- price-reduction? [{price :figure} {new-price :figure}]
  (< new-price price))

(defn on-promotion? [good]
  (price-reduction? (-> good :previous-prices last)
                    (:price good) ))
