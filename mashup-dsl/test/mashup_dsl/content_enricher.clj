(ns mashup-dsl.content-enricher
  (:use [clojure.test]
        [info.kovanovic.camelclojure.dsl]
        [clojure.set]
        [mashup-dsl.test-utils]
        [mashup-dsl.datamodel]
        [midje.sweet]
        [mashup-dsl.sources]))
  

(defn enrich-with-artist []);ovde ideo onaj aggregationstrategy

(fact "content-enricher-pattern"
  (let [start (direct "normalized")
        enriched (mock "enriched")
        camel (create (route (from (timer-endpoint))
                      (process #(enrich-with-artist) 
                      (to enriched))))]
   (start-test camel  start enriched)
   (is-message-count enriched 1)
   (stop-test camel)))