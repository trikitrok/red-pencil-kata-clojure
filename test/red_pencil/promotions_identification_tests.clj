(ns red-pencil.promotions-identification-tests
  (:require
    [midje.sweet :refer :all]
    [red-pencil.goods :as goods]
    [red-pencil.days :as days]
    [red-pencil.promotions-identification :as promotions-identification]))

(defn- price [figure change-ts]
  {:figure figure
   :change-ts change-ts})

(facts
  "about red pencil promotions"

  (fact
    "price rises don't activate any promotion"
    (let [previous-price (price 200 (days/to-ms 0))
          new-price (price 250 (days/to-ms 35))
          good {:price new-price :previous-prices [previous-price]}]
      (promotions-identification/on-promotion? good) => false))

  (facts
    "price reductions activate promotions"

    (let [previous-price (price 100 (days/to-ms 0))
          price-reduced-5-percent (price 95 (days/to-ms 55))
          good {:price price-reduced-5-percent :previous-prices [previous-price]}]
      (promotions-identification/on-promotion? good) => true)

    (let [previous-price (price 100 (days/to-ms 0))
          price-reduced-30-percent (price 70 (days/to-ms 55))
          good {:price price-reduced-30-percent :previous-prices [previous-price]}]
      (promotions-identification/on-promotion? good) => true)

    (fact
      "except when the price is reduced less than 5%"
      (let [previous-price (price 100 (days/to-ms 0))
            new-price (price 98 (days/to-ms 45))
            good {:price new-price :previous-prices [previous-price]}]
        (promotions-identification/on-promotion? good) => false))

    (fact
      "except when the price is reduced more than 30%"
      (let [previous-price (price 100 (days/to-ms 0))
            new-price (price 69 (days/to-ms 31))
            good {:price new-price :previous-prices [previous-price]}]
        (promotions-identification/on-promotion? good) => false))

    (fact
      "except when the previous price has been stable for less than 30 days"
      (let [previous-price (price 100 (days/to-ms 0))
            new-price (price 80 (days/to-ms 10))
            good {:price new-price :previous-prices [previous-price]}]
        (promotions-identification/on-promotion? good) => false))

    ))
