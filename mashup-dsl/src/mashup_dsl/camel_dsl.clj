(ns mashup-dsl.camel-dsl
  (:require [mashup-dsl.aggregator     :as ag]
	    [mashup-dsl.dead-letter    :as dl]
	    [mashup-dsl.extract        :as ex]
	    [mashup-dsl.from           :as fr]
	    [mashup-dsl.loop           :as lo]
	    [mashup-dsl.pause          :as pa]
	    [mashup-dsl.process        :as pc]
	    [mashup-dsl.predicate      :as pr]
	    [mashup-dsl.router         :as rt]
	    [mashup-dsl.reorder        :as re]
	    [mashup-dsl.recipient-list :as rl]
	    [mashup-dsl.routing-slip   :as rs]
	    [mashup-dsl.throttle       :as th]
	    [mashup-dsl.load-balancer  :as lb]
	    [mashup-dsl.split          :as sp]
	    [mashup-dsl.sample         :as sa]
	    [mashup-dsl.to             :as t2]
	    [mashup-dsl.wire-tap       :as wt]
	    [mashup-dsl.camelclojure-core :as co])
  (:import (org.apache.camel.builder Builder)
	   (org.apache.camel.model RouteDefinition)))

(defmacro route [& stmnts]
  `(let [rd# (RouteDefinition.)]
     (-> rd# ~@stmnts)
     rd#))

(defn start [context]
  (co/start-camel context))

(defn stop [context]
  (co/stop-camel context))

(defn create [& routes]
  (co/create-camel routes))

;(defmacro with-message [f]
  ;(co/with-message f))

(defn aggregator [r fn h & opts]
  (ag/aggregator r fn h opts))

(defn dead-letter [r url & opts]
  (dl/dead-letter r url opts))

(defn extract [r f]
  (ex/extract r f))

(defn from [r url & opts]
  (fr/from r url opts))

(defn pause [r milis]
  (pa/pause r milis))

(defn predicate [f]
  (pr/predicate f))

(defn router [r & args]
 (rt/router r args))

(defn throttle [r c & opts]
  (th/throttle r c opts))

(defn process [r f]
  (pc/process r f))

(defn processor [f]
  (pc/processor f))

(defn split [r f]
  (sp/split r f))

(defn sample [r & opts]
  (sa/sample r opts))

(defn doloop [r & opts]
  (lo/doloop r opts))

(defn to [r & args]
  (t2/to r args))

(defn routing-slip [r h & args]
  (rs/routing-slip r h args))

(defn reorder [r t h & opts]
  (re/reorder r t h opts))

(defn recipient-list [r h & opts]
  (rl/recipient-list r h opts))

(defn wire-tap [r url]
  (wt/wire-tap r url))

(defn load-balancer [r t & opts]
  (lb/load-balancer r t opts))

(defn header [hdr next]
  (.. Builder (header hdr) next))

(defn body [hdr next]
  (.. Builder (header hdr) next))
