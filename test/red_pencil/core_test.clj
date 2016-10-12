(ns red-pencil.core-test
  (:require
    [midje.sweet :refer :all]
    [red-pencil.core :as red-pencil]))

(facts
  "about red pencil promotions"

  (fact
    "price rises don't generate any promotion"
    (let [good {:price 200 :on-promotion false}]
      (red-pencil/change-price good 250) => {:price 250 :on-promotion false})))
