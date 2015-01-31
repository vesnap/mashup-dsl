(ns mashup-dsl.content-enricher
  (:use [clojure.test]
        [info.kovanovic.camelclojure.dsl]
        [clojure.set]
        [mashup-dsl.test-utils]
        [mashup-dsl.datamodel]))
   

(fact "content-enricher-pattern"
  (let [mashed (mock "mash")
        camel (create (route (from data-url)
                      (process (enrich-map-with-data (into [] (v2)) (v1) :title :name))
                      (to mashed)))]
   (start-test camel  mashed)
   (is-message-count mashed 1)
   (stop-test camel)))