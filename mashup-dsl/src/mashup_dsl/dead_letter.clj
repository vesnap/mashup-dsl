(ns mashup-dsl.dead-letter
  (:use [mashup-dsl.utils :only (apply-options)])
  (:import (org.apache.camel.builder DeadLetterChannelBuilder)))

(defn- arg-map []
     {:use-original-message #(.useOriginalMessage  (first %))
      :max-redeliveries     #(.maximumRedeliveries (first %) (second %))
      :redelivery-delay     #(.redeliverDelay      (first %) (second %))})

(defn dead-letter [r url opts]
  "TODO: write docstring"
  (let [dl (apply-options (DeadLetterChannelBuilder. url) opts (arg-map))]
    (.errorHandler r dl)))
