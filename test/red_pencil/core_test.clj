(ns red-pencil.core-test
  (:require
    [midje.sweet :refer :all]
    [red-pencil.core :as red-pencil]))

(facts
  "about red pencil promotions"

  (fact
    "price rises don't activate any promotion"
    (let [good {:price 200 :on-promotion false}]
      (red-pencil/change-price good 250) => {:price 250 :on-promotion false}))

  (facts
    "price reductions activate promotions"
    (let [good {:price 100 :on-promotion false}]
      (red-pencil/change-price good 90) => {:price 90 :on-promotion true}

      (fact
        "except when the price is reduced less that 10%"
        (red-pencil/change-price good 95) => {:price 95 :on-promotion false}))))
