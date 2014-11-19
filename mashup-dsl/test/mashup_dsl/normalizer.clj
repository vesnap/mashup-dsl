(ns mashup-dsl.normalizer

  (:use [clojure.test]
        [info.kovanovic.camelclojure.dsl]
        [net.cgrand.enlive-html :as en-html]
        [mashup-dsl.datamodel]
	      [mashup-dsl.test-utils])
 )



(defn- init [context endpoints]
  (if-not (empty? endpoints)
    (do (.setCamelContext (first endpoints) context)
    (recur context (rest endpoints)))))



(deftest normalizer-pattern
    (let [end (mock "normalized")			
         
          route ((from data-url)
          (process #((create-contents ["url" "title"] "//event" data-url)))
          (to end))
	        camel (create route)]
    (start-test camel  end)
    (is-message-count end 1)
    (stop-test camel)))

