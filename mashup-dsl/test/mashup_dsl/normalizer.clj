(ns mashup-dsl.normalizer
  (:use [clojure.test]
        [info.kovanovic.camelclojure.dsl]
        [mashup-dsl.datamodel]
	      [mashup-dsl.test-utils]
       [midje.sweet]
       [mashup-dsl.sources])
       (:require [clojure.data.json :as json]))

(def jetty (jetty-endpoint data-url))

(def jetty-events
  (jetty-endpoint data-url))

(def http-google
  (http-component ))

(def timer-end 
  (timer 
    "timer://pollingTimer?fixedRate=true&delay=0&period=600000" "timer-contenrich"))

(def end (mock "normalized"))

(def camel (create (route (from timer-end)
                (to jetty-events)
                (process 
                  #(contents-extract
                     "events" data-url "/search/events/event" 
                     [:title :url]))
                (to end))))

(fact "normalizer pattern"  
                         (start-test camel timer-end jetty-events end)
                         (is-message-count end 1)
                         (stop-test camel))
