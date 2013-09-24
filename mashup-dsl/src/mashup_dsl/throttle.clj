(ns mashup-dsl.throttle
  (:use [mashup-dsl.utils :only (apply-options)]))

(defn arg-map-throttle []
  {:period-millis #(.timePeriodMillis (first %) (second %))
   :async #(.asyncDelayed (first %))})

(defn throttle [r c opts]
  (apply-options (.throttle r c) opts (arg-map-throttle)))
