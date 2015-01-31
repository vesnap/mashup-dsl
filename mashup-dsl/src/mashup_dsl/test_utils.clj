(ns mashup-dsl.test-utils
  
  (:use [clojure.test]
        [info.kovanovic.camelclojure.dsl]
    [net.cgrand.enlive-html :as en-html])
 )




(defn- init [context endpoints]
  (if-not (empty? endpoints)
    (do (.setCamelContext (first endpoints) context)
    (recur context (rest endpoints)))))

(defn start-test [camel & endpoints]
  (init camel endpoints)
  (start camel))
(defn add-header[endpoint header-name]
  (.setHeaderName header-name) endpoint)
(defn stop-test [camel]
  (stop camel))
(defn data2 [url](en-html/select(en-html/xml-resource url) [:calendar]))
(defn get-received-messages [endpoint]
  (map #(.. % getIn getBody) (.getReceivedExchanges endpoint)))

(defn get-received-message [endpoint]
  (first (get-received-messages endpoint)))

(defn count-messages [endpoint]
  (.getReceivedCounter endpoint))

(defn publish [camel endpoint processor-fn]
  (.. camel createProducerTemplate (send endpoint (processor processor-fn))))

(defn send-text-message [camel endpoint message & headers]
  (publish camel endpoint (fn [[m-body m-headers]]
			    (if-not (nil? headers)
			      [message {(first headers)
					(second headers)}]
			      [message m-headers]))))

(defn get-filename[fileendpoint]
  (.getDoneFileName fileendpoint))

(defn count-messages [endpoint]
  (.getReceivedCounter endpoint))

(defn is-message-count [endpoint count]
  (is (= (count-messages endpoint) count)))





