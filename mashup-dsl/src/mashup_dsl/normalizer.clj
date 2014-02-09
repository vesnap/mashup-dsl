(ns mashup-dsl.normalizer

  (:use [clojure.test]
        [mashup-dsl.camel-dsl]
    [net.cgrand.enlive-html :as en-html]
   [mashup-dsl.datamodel]
	[mashup-dsl.test-utils]
)
(:import [org.apache.camel.component.mock MockEndpoint]
	  
	   [org.apache.camel ProducerTemplate]
    [org.apache.camel.component.file FileEndpoint]
    [org.apache.camel.component.file FileComponent]))
;to do define fns for transformation, these fns are in datamodel.clj


(defn- init [context endpoints]
  (if-not (empty? endpoints)
    (do (.setCamelContext (first endpoints) context)
    (recur context (rest endpoints)))))

;normalizer is combination of router and transformer
(deftest normalizer-pattern
  
  (let [start data-url
       end   (mock "end")			
    xml-processing 
				  (processor (map-tags-contents start :events :event :description))
				  
       routing (route (from start)
               (router  (xml-processing))
          (to end))

	camel (create (routing
                 xml-processing))]
    (start-test camel start end)
 
    (is-message-count end 1)
    (stop-test camel)))

