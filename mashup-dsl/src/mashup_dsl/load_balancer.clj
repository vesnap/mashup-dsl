(ns mashup-dsl.load-balancer
  (:use [mashup-dsl.utils :only (parse-options)]
	[mashup-dsl.to :only (to)]))

(defn load-balancer [r t options]
  (let [opts (parse-options options)
	applied-lb (let [lb (.loadBalance r)
			 weights (:weighted opts)]
		     (if weights
		       (.weighted lb (:round-robbin t) weights)
		       (cond
			(= t :round-robin) (.roundRobin lb)
			(= t :random) (.random lb)
			true (throw (IllegalArgumentException.
				     "invalid type: :round-robin and :random allowed")))))]
    (to applied-lb (:to opts))))
