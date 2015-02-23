(ns mashup-dsl.dsl
  (:require 
  [mashup-dsl.datamodel :as dm]
   [mashup-dsl.templating :as tmpl])
  [mashup-dsl.normalizer :as norm]
  [mashup-dsl.aggregate :as agg])

(defn contents-extract [title url root-tag tags]
  (dm/contents-extract title url root-tag tags))

(defn extract-contents-only  [url root-tag tags]
  (dm/contents-only url root-tag tags))

(defn merge-data [item1 item2 vec-of-names]
  (dm/merge-data item1 item2 vec-of-names))

(defnt left-join [mapoftags v1 v2]
  (dm/left-join mapoftags v1 v2))

(defn mashup [cont]
  (tmpl/mshp cont))

(defn normalize [data-url]
  (let [jetty (jetty-endpoint data-url)
         end (mock "normalized")
         camel (create (route (from (jetty))
                (process 
                  #(contents-extract
                     "events" data-url "/search/events/event" 
                     [:title :url]))))]

                         (start-test (camel) (jetty) (end))
                         (is-message-count (end) 1)
                         (stop-test (camel))))

(defn aggregate [data-url]  
        (let [end  (mock "end")
	      f (fn [data-url]
	      (tmpl/mshp (dm/contents-extract
                     "events" data-url "/search/events/event" 
                     [:title :url])))
                                                                     
	      r (route (from data-url) 
	       (aggregator f "type" :count 1)
	       (to end))
	      camel (create r)]
                         (start-test (camel) (jetty) (end))
                         (is-message-count (end) 1)
                         (stop-test (camel))))

(defn content-enrich [data-url1 data-url2]
 
  (let [mashed (mock "enriched")
        camel (create (route (from data-url)
                      (process #(enrich-map-with-data (into [] (v2)) (v1) :title :name))
                      (to mashed)))]
   (start-test camel  enriiched)
   (is-message-count mashed 1)
   (stop-test camel)))


