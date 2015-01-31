#Mashup-DSL

Mashup-DSL is Clojure library designed for web mashup creation. Web mashups are web applications which are created by combining data from different sources and/or showing them in different/new ways.

##About Mashup-DSL

Among the other Clojure libraries (enlive, ring, moustache....) Mashup-DSL uses Highway - Clojure library for Apache Camel. Hereof the mashups are created using Camel Enterprise Integration Patterns.

In order to make a mashup, first thing to do is to find the data sources. Data sources are different web sites and applications which offer its data by different means (ie. API calls). Mashup-DSL works with data that come in XML like (tag value pairs) form, and Camel Jetty endpoint is used for defining data sources.

After the data is located next step is data mapping, ie. translating data to the internal mashup model. Internal mashup model in this library is xml based, and is represented through Clojure map, where tag names are keys, and values from corresponding tags are vals. Data mapping is done using Normalizer pattern. Afterwards the normalized data from different sources are "glued" together using Content Enricher pattern. The enriched data is combined with html template using Aggregator pattern, and this result is what the user can see in her browser.

##Getting started

First use or require mashup-dsl.dsl in your namespace, and you'll be able to call appropriate functions from the Mashup-DSL library.

    (ns example
    (:use [mashup-dsl.dsl]))

###Specifying data source

Camel Jetty endpoint is used for defining data source. jetty function expects data source url in form of a string (between two doubl quote characters). Different endpoints from Camel that are needed in this

    (defn jetty [data-url] (jetty-endpoint data-url))
    (defn jetty-google [] (jetty-endpoint "https://maps.googleapis.com/maps/api/place/search/json?location=40.446788,-79.950559&radius=500&types=food&sensor=false&key=AIzaSyCXQ-GOzJ3g1rdyVS4hAI4XJNGoCY9y1xQ"))

###Using Normalizer for data translation

Normalizer pattern is used for data translation. Function msh-contents is used for extracting and translating data from xml to clojure map.

    (ns mashup-dsl.normalizer
    (:use [clojure.test]
    [info.kovanovic.camelclojure.dsl]
    [mashup-dsl.datamodel]
    [mashup-dsl.test-utils]
    [midje.sweet])
    (:require [clojure.data.json :as json]))
    (defn jetty [](jetty-endpoint data-url))
    (defn end [] (mock "normalized"))
    (defn camel [](create (route (from (jetty))
        (process #(contents-extract  "events" data-url "/search/events/event" [:title :url])))))
        (fact "normalizer pattern"  
        (start-test (camel) (jetty) (end))
        (is-message-count (end) 1)
        (stop-test (camel)))

##Using Content Enricher for combining data

Content Enricher pattern is used for enriching initial data with data from other sources, the enrich-map-with-data function is used.

    (deftest content-enricher-pattern
    (let [mashed (mock "mash")
    camel (create (route (from data-url)
                  (process #(enrich-map-with-data (into [] (v2)) (v1) :title :name))
                  (to mashed)))]
    (start-test camel  mashed)
    (is-message-count mashed 1)
    (stop-test camel)))

##Using Aggregator for creating html page out of a template

Aggregator pattern is used to create html page for display, enriched data is combined with html template, and mshp function is used.

    (fact "aggregator pattern"  
      (let [end  (mock "end")
      f (fn []
      (mshp (create-contents ["url" "title"] "//event" data-url)))
      r (route (from data-url) 
      (aggregator f "type" :count 1)
      (to end))
      camel (create r)]
      (start-test (camel) (jetty) (end))
      (is-message-count (end) 1)
      (stop-test (camel))))

###Showing the page

For showing the page Clojure library moustache is used.

    (def routes 
    (app [""]  (fn [req] (render-to-response (mshp (zipmap [:data-content :title] [(create-contents ["url" "title"] "//event" data-url) "events mashup"]))))
    [&]   page-not-found))
    (defonce ^:dynamic *server* (run-server routes))

After this call in the http://localhost:8080/ the page should appear.

##License

Distributed under the Eclipse Public License, the same as Clojure.
