(ns red-pencil.promotions-identification-tests
  (:require
    [midje.sweet :refer :all]
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
          price-change-ts (days/to-ms 35)
          new-price (price 250 price-change-ts)
          good {:price new-price :previous-prices [previous-price]}
          query-ts (+ price-change-ts (days/to-ms 2))]
      (promotions-identification/on-promotion? good query-ts) => false))

  (facts
    "price reductions activate promotions"

    (let [previous-price (price 100 (days/to-ms 0))
          price-change-ts (days/to-ms 55)
          price-reduced-5-percent (price 95 price-change-ts)
          good {:price price-reduced-5-percent :previous-prices [previous-price]}
          query-ts (+ price-change-ts (days/to-ms 5))]
      (promotions-identification/on-promotion? good query-ts) => true)

    (let [previous-price (price 100 (days/to-ms 0))
          price-change-ts (days/to-ms 30)
          price-reduced-30-percent (price 70 price-change-ts)
          good {:price price-reduced-30-percent :previous-prices [previous-price]}
          query-ts (+ price-change-ts (days/to-ms 4))]
      (promotions-identification/on-promotion? good query-ts) => true)

    (fact
      "except when the price is reduced less than 5%"
      (let [previous-price (price 100 (days/to-ms 0))
            price-change-ts (days/to-ms 45)
            new-price (price 98 price-change-ts)
            good {:price new-price :previous-prices [previous-price]}
            query-ts (+ price-change-ts (days/to-ms 2))]
        (promotions-identification/on-promotion? good query-ts) => false))

    (fact
      "except when the price is reduced more than 30%"
      (let [previous-price (price 100 (days/to-ms 0))
            price-change-ts (days/to-ms 31)
            new-price (price 69 price-change-ts)
            good {:price new-price :previous-prices [previous-price]}
            query-ts (+ price-change-ts (days/to-ms 4))]
        (promotions-identification/on-promotion? good query-ts) => false))

    (fact
      "except when the previous price has been stable for less than 30 days"
      (let [previous-price (price 100 (days/to-ms 0))
            price-change-ts (days/to-ms 10)
            new-price (price 80 price-change-ts)
            good {:price new-price :previous-prices [previous-price]}
            query-ts (+ price-change-ts (days/to-ms 2))]
        (promotions-identification/on-promotion? good query-ts) => false))

    (fact
      "except when the price changed was more than 30 days"
      (let [previous-price (price 100 (days/to-ms 0))
            price-change-ts (days/to-ms 30)
            new-price (price 80 price-change-ts)
            good {:price new-price :previous-prices [previous-price]}
            query-ts (+ price-change-ts (days/to-ms 31))]
        (promotions-identification/on-promotion? good query-ts) => false))))
