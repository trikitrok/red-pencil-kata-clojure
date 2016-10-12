(ns red-pencil.promotions-identification-tests
  (:require
    [midje.sweet :refer :all]
    [red-pencil.days :as days]
    [red-pencil.promotions-identification :as promotions-identification]
    [red-pencil.price :as price]))

(def price price/make)

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
          price-reduction-upper-limit 95
          price-change-ts (days/to-ms 55)
          price-reduced-5-percent (price price-reduction-upper-limit price-change-ts)
          good {:price price-reduced-5-percent :previous-prices [previous-price]}
          query-ts (+ price-change-ts (days/to-ms 5))]
      (promotions-identification/on-promotion? good query-ts) => true)

    (let [previous-price (price 100 (days/to-ms 0))
          price-reduction-lower-limit 70
          price-change-ts (days/to-ms 30)
          price-reduced-30-percent (price price-reduction-lower-limit price-change-ts)
          good {:price price-reduced-30-percent :previous-prices [previous-price]}
          query-ts (+ price-change-ts (days/to-ms 4))]
      (promotions-identification/on-promotion? good query-ts) => true)

    (let [previosu-price-ts (days/to-ms 0)
          previous-price (price 100 previosu-price-ts)
          previous-price-maximum-duration-in-days 30
          price-change-ts (+ previosu-price-ts (days/to-ms previous-price-maximum-duration-in-days))
          price-reduced-30-percent (price 90 price-change-ts)
          good {:price price-reduced-30-percent :previous-prices [previous-price]}
          query-ts (+ price-change-ts (days/to-ms 4))]
      (promotions-identification/on-promotion? good query-ts) => true)

    (let [previous-price (price 100 (days/to-ms 0))
          price-change-ts (days/to-ms 30)
          price-reduced-30-percent (price 80 price-change-ts)
          good {:price price-reduced-30-percent :previous-prices [previous-price]}
          promotion-maximum-duration-in-days 30
          query-ts (+ price-change-ts (days/to-ms promotion-maximum-duration-in-days))]
      (promotions-identification/on-promotion? good query-ts) => true))

  (facts
    "price reductions don't activate promotions"

    (fact
      "when the price is reduced less than 5%"
      (let [previous-price (price 100 (days/to-ms 0))
            price-change-ts (days/to-ms 45)
            new-price (price 98 price-change-ts)
            good {:price new-price :previous-prices [previous-price]}
            query-ts (+ price-change-ts (days/to-ms 2))]
        (promotions-identification/on-promotion? good query-ts) => false))

    (fact
      "when the price is reduced more than 30%"
      (let [previous-price (price 100 (days/to-ms 0))
            price-change-ts (days/to-ms 31)
            new-price (price 69 price-change-ts)
            good {:price new-price :previous-prices [previous-price]}
            query-ts (+ price-change-ts (days/to-ms 4))]
        (promotions-identification/on-promotion? good query-ts) => false))

    (fact
      "when the previous price has been stable for less than 30 days"
      (let [previous-price (price 100 (days/to-ms 0))
            price-change-ts (days/to-ms 10)
            new-price (price 80 price-change-ts)
            good {:price new-price :previous-prices [previous-price]}
            query-ts (+ price-change-ts (days/to-ms 2))]
        (promotions-identification/on-promotion? good query-ts) => false))

    (fact
      "when the price changed more than 30 days ago"
      (let [previous-price (price 100 (days/to-ms 0))
            price-change-ts (days/to-ms 30)
            new-price (price 80 price-change-ts)
            good {:price new-price :previous-prices [previous-price]}
            query-ts-before-promotion-expired (+ price-change-ts (days/to-ms 30))
            query-ts-after-promotion-expired (+ price-change-ts (days/to-ms 31))]
        (promotions-identification/on-promotion?
          good query-ts-before-promotion-expired) => true
        (promotions-identification/on-promotion?
          good query-ts-after-promotion-expired) => false)))

  (fact
    "a further price reduction during a promotion, will not prolong the promotion"
    (let [first-price (price 100 (days/to-ms 0))
          second-price-change-ts (days/to-ms 35)
          second-price (price 95 second-price-change-ts)
          third-price-change-ts (days/to-ms 45)
          third-price (price 85 third-price-change-ts)
          fourth-price-change-ts (days/to-ms 46)
          fourth-price (price 70 fourth-price-change-ts)
          good {:price fourth-price :previous-prices [first-price second-price third-price]}
          query-ts-before-first-promotion-expired (+ second-price-change-ts (days/to-ms 30))
          query-ts-after-first-promotion-expired (+ second-price-change-ts (days/to-ms 31))]
      (promotions-identification/on-promotion?
        good query-ts-before-first-promotion-expired) => true
      (promotions-identification/on-promotion?
        good query-ts-after-first-promotion-expired) => false))

  (fact
    "a price rise during a promotion, will end the promotion"
    (let [first-price (price 100 (days/to-ms 0))
          second-price-change-ts (days/to-ms 35)
          second-price (price 80 second-price-change-ts)
          third-price-change-ts (days/to-ms 45)
          third-price (price 90 third-price-change-ts)
          good {:price third-price :previous-prices [first-price second-price]}
          query-ts (+ second-price-change-ts (days/to-ms 10))]
      (promotions-identification/on-promotion?
        good query-ts) => false))

  (fact
    "a price reduction during a promotion that causes an overall reduction
    of more than 30% with regard to the original price, will end the promotion"
    (let [first-price (price 100 (days/to-ms 0))
          second-price-change-ts (days/to-ms 35)
          second-price (price 80 second-price-change-ts)
          third-price-change-ts (days/to-ms 45)
          third-price (price 60 third-price-change-ts)
          good {:price third-price :previous-prices [first-price second-price]}
          query-ts (+ second-price-change-ts (days/to-ms 10))]
      (promotions-identification/on-promotion?
        good query-ts) => false))

  (fact
    "after a promotion is ended additional promotions may follow
    as long as the price was stable for 30 days and
    these 30 days don’t intersect with a previous promotion"
    (let [first-price (price 100 (days/to-ms 0))
          second-price (price 80 (days/to-ms 35))
          third-price (price 60 (days/to-ms 66))
          good {:price third-price :previous-prices [first-price second-price]}
          query-ts (days/to-ms 67)]
      (promotions-identification/on-promotion?
        good query-ts) => false)

    (let [first-price (price 100 (days/to-ms 0))
          second-price (price 80 (days/to-ms 35))
          third-price (price 60 (days/to-ms 96))
          good {:price third-price :previous-prices [first-price second-price]}
          query-ts (days/to-ms 97)]
      (promotions-identification/on-promotion?
        good query-ts) => true)))
