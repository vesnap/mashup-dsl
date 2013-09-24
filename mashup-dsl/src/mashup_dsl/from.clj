(ns mashup-dsl.from
  (:use [mashup-dsl.utils :only (apply-options)]))

(defn- arg-map []
     {:event         #(.inOnly (first %))
      :request-reply #(.inOut  (first %))})

(defn from [r url opts]
  "TODO: write docstring"
  (apply-options (.from r url) opts (arg-map)))
