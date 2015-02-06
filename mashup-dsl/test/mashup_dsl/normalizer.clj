(ns mashup-dsl.normalizer
  (:use [clojure.test]
        [info.kovanovic.camelclojure.dsl]
        [mashup-dsl.datamodel]
	      [mashup-dsl.test-utils]
       [midje.sweet]
       [mashup-dsl.sources])
       (:require [clojure.data.json :as json]))

(defn jetty [](jetty-endpoint data-url))

(defn jetty-flights [] 
  (jetty-endpoint "http://flightradar24.com/PlaneFeed.json"))

(defn http-google [] 
  (http-component ))

(defn timer-end [] 
  (timer 
    "timer://pollingTimer?fixedRate=true&delay=0&period=600000" "timer-contenrich"))

(defn end [](mock "normalized"))

(defn camel [](create (route (from (timer-end))
                (to (jetty-flights))
                (process 
                  #(contents-extract
                     "events" data-url "/search/events/event" 
                     [:title :url]))
                (to (end)))))

(fact "normalizer pattern"  
                         (start-test (camel) (timer-end) (jetty-flights)(end))
                         (is-message-count (end) 1)
                         (stop-test (camel)))
