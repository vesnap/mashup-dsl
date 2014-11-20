(ns mashup-dsl.normalizer
  (:use [clojure.test]
        [info.kovanovic.camelclojure.dsl]
        [net.cgrand.enlive-html :as en-html]
        [mashup-dsl.datamodel]
	      [mashup-dsl.test-utils]))



(defn- init [context endpoints]
  (if-not (empty? endpoints)
    (do (.setCamelContext (first endpoints) context)
    (recur context (rest endpoints)))))



(deftest normalizer-pattern
    (let [start (jetty-endpoint data-url)
          end (mock "normalized")			
	        camel (create (route 
                       (from start)
                        (process #((create-contents ["url" "title"] "//event" data-url)))
                       (to end)))]
    (start-test camel start end)
    (is-message-count end 1)
    (stop-test camel)))

