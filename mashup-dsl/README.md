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

In sources.clj, there are different functions used for specifying data sources. 
TimerEndpoint is endpoint that can generate periodic inbound exchanges triggered by a timer.

(defn timer [uri name] (TimerEndpoint. uri (TimerComponent.) name))


Camel Jetty endpoint is used for defining data source. jetty function expects data source URL in form of a string (between two double quote characters). Different end points from Camel that are needed in this

    (defn jetty [data-url] (jetty-endpoint data-url))
    (defn jetty-google [] (jetty-endpoint "https://maps.googleapis.com/maps/api/place/search/json?location=40.446788,-79.950559&radius=500&types=food&sensor=false&key=AIzaSyCXQ-GOzJ3g1rdyVS4hAI4XJNGoCY9y1xQ"))

###Using Normalizer for data translation

Normalizer pattern is used for data translation. Function msh-contents is used for extracting and translating data from xml to clojure map.
Route is defined as specified bellow

    (defn camel [](create (route (from (timer-end))
                (to (jetty-flights))
                (process 
                  #(contents-extract
                     "events" data-url "/search/events/event" 
                     [:title :url]))
                (to (end)))))
    

##Using Content Enricher pattern for combining data

Content Enricher pattern is used for enriching initial data with data from other sources, the enrich-with-artist function is used, in this function aggregation strategy is defined.
    (fact "content-enricher-pattern"
  (let [start (direct "normalized")
        enriched (mock "enriched")
        camel (create (route (from (timer-endpoint))
                      (process #(enrich-with-artist) 
                      (to enriched))))]
   (start-test camel  start enriched)
   (is-message-count enriched 1)
   (stop-test camel)))

##Using Aggregator pattern for creating html page out of a template

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
