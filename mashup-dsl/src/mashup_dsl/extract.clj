(ns mashup-dsl.extract
  (:use [mashup-dsl.predicate :only (predicate)]
	[mashup-dsl.camelclojure-core :only (with-message)]))

(defn extract [r f]
  "Filtering component. TODO: write docstring"
  (.filter r (predicate f)))
