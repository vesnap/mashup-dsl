(ns mashup-dsl.normalizer
  (:use [clojure.test]
        [info.kovanovic.camelclojure.dsl]
        ;[net.cgrand.enlive-html :as en-html]
        [mashup-dsl.datamodel]
	      [mashup-dsl.test-utils]
       [midje.sweet]))

(defn jetty [](jetty-endpoint data-url))

(defn end [](mock "normalized"))

(defn camel [](create (route(from (jetty) :events)))
                            (process #(msh-contents2)))
(fact "normalizer pattern"  
                         (start-test (camel) (jetty) (end))
                         (is-message-count (end) 1)
                         (stop-test (camel)))