(ns mashup-dsl.test-utils
  (:require [clojure.java.io :as io])
  (:use [clojure.test]
        [info.kovanovic.camelclojure.dsl]
        [ring.util.response :only [response file-response]]
        [ring.util.response :only [response file-response]]
        [ring.middleware.reload :only [wrap-reload]]
        [ring.middleware.file :only [wrap-file]]
        [ring.middleware.stacktrace :only [wrap-stacktrace]]
        [net.cgrand.enlive-html :as en-html]
        [ring.adapter.jetty :only [run-jetty]]))




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


(def ^:dynamic *webdir* (str (.getCanonicalFile (io/file ".")) "/src/mshp/"))

(defn serve-file [filename]
  (file-response
   {:root *webdir*
    :index-files? true
    :html-files? true}))


(defn run-server* [app & {:keys [port] :or {port 8080}}]
  (let [nses (if-let [m (meta app)]
               [(-> (:ns (meta app)) str symbol)]
               [])]
    (println "run-server*" nses)
    (run-jetty
     (-> app
         (wrap-file *webdir*)
         (wrap-reload nses)
         (wrap-stacktrace))
     {:port port :join? false})))

(defn page-not-found [req]
  {:status 404
   :headers {"Content-type" "text/html"}
   :body "Page Not Found"})


(defn render [t]
  (apply str t))

(defn render-snippet [s]
  (apply str (en-html/emit* s)))

(def render-to-response
     (comp response render))

(defn render-request [afn & args]
  (fn [req] (render-to-response (apply afn args))))

