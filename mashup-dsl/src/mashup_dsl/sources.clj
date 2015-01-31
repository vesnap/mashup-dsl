(ns mashup-dsl.sources
(:use [clojure.test]
      [mashup-dsl.test-utils]
      [info.kovanovic.camelclojure.dsl]
      ;[net.cgrand.enlive-html :as en-html]
     )
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
         [org.apache.camel.component.timer TimerComponent]
         ))



(defn timer[] (TimerComponent. ))

(defn jetty-comp []
  (JettyHttpComponent. ))

(defn jetty-endpoint[url]
(JettyHttpEndpoint. (jetty-comp) url (URI. (str "jetty:" url))))

(defn file-comp[file-name]
  (FileEndpoint. file-name (FileComponent. ))) ;+fileName=thefilename.

(defn mock [url]
  (MockEndpoint. (str "mock://" url)))

(defn directComponent []
  (DirectComponent. ))

(defn direct [url]
(DirectEndpoint. url (directComponent)))

(defn http-component []
(HttpComponent.  ))

(defn set-header[name val]
  (.setHeader (name,val)))

