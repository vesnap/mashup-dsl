(ns mashup-dsl.sources
(:use [clojure.test]
      [mashup-dsl.test-utils]
      [info.kovanovic.camelclojure.dsl])
      ;[net.cgrand.enlive-html :as en-html])
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
         [org.apache.camel.component.timer TimerEndpoint]
         [org.apache.camel.impl DefaultCamelContext]
         [javax.jms ConnectionFactory]
         [org.apache.activemq ActiveMQConnectionFactory]
         [org.apache.camel CamelContext]
         [org.apache.camel ProducerTemplate]
         [org.apache.camel.builder RouteBuilder]
         [org.apache.camel.component.jms JmsComponent]
         [java.lang Thread]
         [java.io File]))



(defn timer[uri name] 
  (TimerEndpoint. uri (TimerComponent.) name))


(defn jetty-comp []
  (JettyHttpComponent.))

(defn jetty-endpoint[url]
(JettyHttpEndpoint. (jetty-comp) url (URI. (str "jetty:" url))))

(defn file-comp []
 (FileComponent. ) )

(defn file-end [file-name]
  (FileEndpoint. (str "file:" file-name)(file-comp)))

(defn mock [url]
  (MockEndpoint. (str "mock:/" url)))

(defn directComponent []
  (DirectComponent. ))

(defn direct [url]
(DirectEndpoint. url (directComponent)))

(defn http-component []
(HttpComponent.  ))

(defn set-header[name val]
  (.setHeader name val))
;context.addComponent("test-jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

(defn jms-component[camel name]
(let [con-fact (ActiveMQConnectionFactory. "vm:/localhost?broker.persistent=false")]
  ( .addComponent camel name (.jmsComponentAutoAcknowledge JmsComponent con-fact))))
