(ns mashup-dsl.normalizer
  (:use [clojure.test]
        [info.kovanovic.camelclojure.dsl]
        [mashup-dsl.datamodel]
	      [mashup-dsl.test-utils]
       [midje.sweet]
       [mashup-dsl.sources])
       (:require [clojure.data.json :as json]))

(defn jetty [](jetty-endpoint data-url))

(defn jetty-google [] (jetty-endpoint "https://maps.googleapis.com/maps/api/place/search/json?location=40.446788,-79.950559&radius=500&types=food&sensor=false&key=AIzaSyCXQ-GOzJ3g1rdyVS4hAI4XJNGoCY9y1xQ"))

(defn end [](mock "normalized"))

(defn camel [](create (route (from (jetty))
                (process 
                  #(contents-extract
                     "events" data-url "/search/events/event" 
                     [:title :url])))))
(fact "normalizer pattern"  
                         (start-test (camel) (jetty) (end))
                         (is-message-count (end) 1)
                         (stop-test (camel)))
