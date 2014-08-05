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


;(defn enrich[ex message url &key-data]
;(message is map with starting data, data is list of attributes for data that we want to be added)
;((if not (nil? message)
;(
;merge-with union message (data url)
 ;ex .getOut() .setBody(merge-with union message (data url)))
;)))

;enrich(excgange, podaci)
;newmessage=message.replace(poslednji red, sa novimpodacima)
;
(deftest content-enricher-pattern
  (let [ 
  start (direct data-url)
 mashed (mock "mash")

  camel (create (route (from start)
                       (process (map-tags-contents data-url :events))
                       (to mashed)))]
    (start-test camel start mashed)
    (stop-test camel)
    ))

;merge operator
