(ns red-pencil.core-test
  (:require
    [midje.sweet :refer :all]
    [red-pencil.core :as red-pencil]))

(defn- price [figure & {:keys [change-ts] :or {change-ts 0}}]
  {:figure figure
   :change-ts change-ts})

(facts
  "about red pencil promotions"

  (fact
    "price rises don't activate any promotion"
    (let [good {:price (price 200) :on-promotion false}]
      (red-pencil/change-price good 250) => {:price (price 250) :on-promotion false}))

  (facts
    "price reductions activate promotions"
    (let [good {:price (price 100) :on-promotion false}]
      (red-pencil/change-price good 95) => {:price (price 95) :on-promotion true}
      (red-pencil/change-price good 70) => {:price (price 70) :on-promotion true}

      (fact
        "except when the price is reduced less than 5%"
        (red-pencil/change-price good 98) => {:price (price 98) :on-promotion false})

      (fact
        "except when the price is reduced more than 30%"
        (red-pencil/change-price good 69) => {:price (price 69) :on-promotion false}))))
