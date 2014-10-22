(ns mashup-dsl.normalizer

  (:use [clojure.test]
        [mashup-dsl.camel-dsl]
        [net.cgrand.enlive-html :as en-html]
        [mashup-dsl.datamodel]
	      [mashup-dsl.test-utils])
  (:import [org.apache.camel.component.mock MockEndpoint]
	         [org.apache.camel ProducerTemplate]
           [org.apache.camel.component.file FileEndpoint]
            org.apache.camel.component.file FileComponent]))
;to do define fns for transformation, these fns are in datamodel.clj


(defn- init [context endpoints]
  (if-not (empty? endpoints)
    (do (.setCamelContext (first endpoints) context)
    (recur context (rest endpoints)))))

;normalizer is combination of router and transformer
;for every transformation there's a processor
;processor as in arg gets the fn for transformation

(deftest normalizer-pattern
    (let [end (mock "normalized")			
          xml-processing 
				  (route (from data-url)
          (process #((create-contents ["url" "title"] "//event" data-url)))
          (to end))
	        camel (create xml-processing)]
    (start-test camel  end)
    (is-message-count end 1)
    (stop-test camel)))

