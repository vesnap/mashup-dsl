(ns mashup-dsl.test-utils
  
  (:use [clojure.test]
        [info.kovanovic.camelclojure.dsl]
    [net.cgrand.enlive-html :as en-html])
  (:import [org.apache.camel.component.mock MockEndpoint]
	  [org.apache.camel.component.direct DirectEndpoint]
	  [org.apache.camel ProducerTemplate]
    [org.apache.camel.component.file FileEndpoint]
    [org.apache.camel.component.file FileComponent]
    [org.apache.camel.component.direct DirectComponent]
    [org.apache.camel.component.http HttpComponent]
    [java.net URI]
    [org.apache.camel.component.jetty JettyHttpEndpoint]
    [org.apache.camel.component.jetty JettyHttpComponent]
    [org.apache.camel.component.timer TimerComponent]))



(defn timer[] (TimerComponent. ))

(defn jetty-comp []
  (JettyHttpComponent. ))

(defn jetty-endpoint[url]
(JettyHttpEndpoint. (jetty-comp) url (URI. (str "jetty:" url))))

(defn file-comp[file-name]
  (FileEndpoint. (str "file://inbox[filename=" file-name "]") (FileComponent. ))) ;+fileName=thefilename.

(defn mock [url]
  (MockEndpoint. (str "mock://" url)))

(defn directComponent []
  (DirectComponent. ))

(defn direct [url]
(DirectEndpoint. url (directComponent)))

(defn http-component []
(HttpComponent.  ))

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





