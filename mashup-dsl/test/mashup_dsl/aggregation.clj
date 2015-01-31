(ns mashup-dsl.aggregation
(:use [clojure.test]
      [mashup-dsl.datamodel]
      [mashup-dsl.templating]
      [info.kovanovic.camelclojure.dsl]
      [midje.sweet]
      [mashup-dsl.test-utils]
      [mashup-dsl.sources]))
  


(fact "aggregator pattern"  
        (let [f (fn []
                 (mshp (create-contents ["url" "title"] "//event" data-url)))                                                           
	            r (route (from (jetty-endpoint data-url)) 
	                     (aggregator f "type" :count 1))
	            camel (create r)]
         (start-test (camel) (jetty-endpoint data-url))
         (stop-test (camel))))