(ns mashup-dsl.loop
  (:use [mashup-dsl.utils :only (apply-options)]))

(defn arg-map-loop []
  {:times  #(.constant (first %) (second %))
   :header #(.header   (first %) (second %))})

(defn doloop [r opts]
  (apply-options (.loop r) opts (arg-map-loop)))
