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
  )
