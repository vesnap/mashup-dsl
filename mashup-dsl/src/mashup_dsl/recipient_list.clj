(ns mashup-dsl.recipient-list
  (:use [mashup-dsl.utils :only (apply-options)])
  (:import (org.apache.camel.builder Builder)))


(defn arg-map-rl []
  {:paralel #(.parallelProcessing (first %))
   :timeout #(.timeout (first %) (second %))
   :stop-on-exception #(.stopOnException (first %))
   :ignore-invalid-endpoints #(.ignoreInvalidEndpoints (first %))});ovo je sve multicast http://camel.apache.org/multicast.html

(defn recipient-list [r h opts]
  (apply-options (.. r (recipientList (Builder/header h))) opts (arg-map-rl)))
