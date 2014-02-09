(ns mashup-dsl.content-enricher
  (:use [clojure.test]
        [mashup-dsl.camel-dsl]
        [clojure.set]

    ;[net.cgrand.enlive-html :as en-html]
    [mashup-dsl.test-utils]
    [mashup-dsl.datamodel]
)
   (:import (org.apache.camel Exchange)))

;;


(defn enrich [uri aggStrat]
  (
    ))
;(defn- aggregation-strategy [agg-fn]
 ; (proxy [AggregationStrategy] []
  ;  (aggregate [old-exchange res-exchange]
	 ;      (let [[n-body n-headers] ((generate-aggregator agg-fn) old-exchange res-exchange)]
		; (.. new-exchange getIn (setBody n-body))
		 ;(.. new-exchange getIn (setHeaders n-headers))
		 ;new-exchange))))


(defn enrich[ex message url &key-data]
;(message is map with starting data, data is list of attributes for data that we want to be added)
((if not (nil? ex)
(let [mess (.getIn (.getBody ex))]
merge-with union mess (data url))
)))


(deftest content-enricher-pattern
  (let [
        process-body (fn [proc-fn]
 (fn [[body headers]]
 [(proc-fn body) headers]))
  start (direct data-url)
	end   (mock "end")
  enriching-with-data (map-tags-contents data-url :event)
  p1-route (route (from start)
 (process (process-body enriching-with-data))
 (to end))
  camel (create p1-route)]
    (start-test camel)
    (stop-test camel)))


