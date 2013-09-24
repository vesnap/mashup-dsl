(ns mashup-dsl.routing-slip
  (:use [mashup-dsl.utils :only (apply-options)]))

(defn arg-map-rs []
  {:ignore-invalid #(.ignoreInvalidEndpoints (first %))
   :delimeter #(.uriDelimiter (first %) (second %))})

(defn routing-slip [r h opts]
  (apply-options (.. r routingSlip (header h)) opts (arg-map-rs)))
