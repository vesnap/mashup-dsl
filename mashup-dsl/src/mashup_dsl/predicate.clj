(ns mashup-dsl.predicate
  (:use [mashup-dsl.camelclojure-core :only (with-message)])
  (:import (org.apache.camel Predicate)))

(defn predicate [f]
  (proxy [Predicate] []
    (matches [exchange]
	     ((with-message f) exchange))))

