(ns mashup-dsl.content-enricher
  (:use [clojure.test]
        [mashup-dsl.camel-dsl]
        [clojure.set]

    ;[net.cgrand.enlive-html :as en-html]
    [mashup-dsl.test-utils]
    [mashup-dsl.datamodel]
))
   

(deftest content-enricher-pattern
  (let [ 
 
 mashed (mock "mash")

  camel (create (route (from data-url)
                       (process (map-tags-contents data-url :events))
                       (to mashed)))]
    (start-test camel  mashed)
     (is-message-count mashed 1)
    (stop-test camel)
    ))