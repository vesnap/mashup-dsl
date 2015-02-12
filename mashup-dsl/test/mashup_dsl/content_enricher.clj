(ns mashup-dsl.content-enricher
  (:use [clojure.test]
        [info.kovanovic.camelclojure.dsl]
        [clojure.set]
        [mashup-dsl.test-utils]
        [mashup-dsl.datamodel]
        [midje.sweet]
        [mashup-dsl.sources]))
  
        
        

(fact "content-enricher-pattern"
  (let [start (direct "normalized")
        enriched (mock "enriched")
        f (fn [[body1 headers1] [body2 headers2]]
	    (identity [(merge-data body1 body2 [:artist :name])
		       headers1])
         enrich-with-artist (aggregator f "type" :count 2))
        camel (create (route (from (timer-endpoint))
                      (process #(enrich-with-artist) 
                      (to enriched))))]
   (start-test camel  start enriched)
   (is-message-count enriched 1)
   (stop-test camel)))