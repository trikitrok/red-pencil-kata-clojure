(ns red-pencil.core-test
  (:require
    [midje.sweet :refer :all]
    [red-pencil.core :as red-pencil]
    [red-pencil.days :as days]))

(defn- price [figure & {:keys [change-ts] :or {change-ts 0}}]
  {:figure figure
   :change-ts change-ts})

(def days days/to-ms)

(defn- days-after [ts num]
  (+ ts (days num)))

(facts
  "about red pencil promotions"

  (fact
    "price rises don't activate any promotion"
    (let [good {:price (price 200) :on-promotion false}
          new-price (price 250 :change-ts (days 35))]
      (red-pencil/change-price good new-price) => {:price new-price :on-promotion false}))

  (facts
    "price reductions activate promotions"
    (let [good {:price (price 100) :on-promotion false}]

      (let [price-reduced-5-percent (price 95 :change-ts (days 55))]
        (red-pencil/change-price good price-reduced-5-percent) => {:price price-reduced-5-percent :on-promotion true})

      (let [price-reduced-30-precent (price 70 :change-ts (days 33))]
        (red-pencil/change-price good price-reduced-30-precent) => {:price price-reduced-30-precent :on-promotion true})

      (fact
        "except when the price is reduced less than 5%"
        (let [new-price (price 98 :change-ts (days 45))]
          (red-pencil/change-price good new-price) => {:price new-price :on-promotion false}))

      (fact
        "except when the price is reduced more than 30%"
        (let [new-price (price 69 :change-ts (days 31))]
          (red-pencil/change-price good new-price) => {:price new-price :on-promotion false})))

    (fact
      "except when the previous price has been stable for less than 30 days"
      (let [previous-price-ts 0
            previous-price (price 100 :change-ts previous-price-ts)
            good {:price previous-price :on-promotion false}
            new-price-ts (days-after previous-price-ts 10)
            new-price (price 80 :change-ts new-price-ts)]
        (red-pencil/change-price good new-price) => {:price new-price :on-promotion false}))))
