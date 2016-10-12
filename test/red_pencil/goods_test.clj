(ns red-pencil.goods-test
  (:require
    [midje.sweet :refer :all]
    [red-pencil.goods :as goods]
    [red-pencil.days :as days]))

(defn- price [figure change-ts]
  {:figure figure
   :change-ts change-ts})

(facts
  "about changing good prices"

  (fact
    "the price is changed and the old price is saved"
    (let [previous-price (price 200 (days/to-ms 1))
          good {:price previous-price :previous-prices [:some-price]}
          new-price (price 250 (days/to-ms 35))]
      (goods/change-price
        good new-price) => {:price new-price :previous-prices [:some-price previous-price]})))
