(ns mashup-dsl.utils(:require [net.cgrand.enlive-html :as html])
  (:use [ring.adapter.jetty :only [run-jetty]]
        [ring.util.response :only [response file-response]]
        [ring.middleware.reload :only [wrap-reload]]
        [ring.middleware.file :only [wrap-file]]
        [ring.middleware.stacktrace :only [wrap-stacktrace]]
        )
  (:import (java.util.concurrent TimeUnit)
  [org.apache.camel.component.mock MockEndpoint]
	   [org.apache.camel.component.direct DirectEndpoint]
	   [org.apache.camel ProducerTemplate])
)

;;; Utilities

(defn parse-options
  "create hashmap out of list of keywords and symbols.
from (:a 1 :b 2 3 :c 3 4 5) create {:a (1) :b (2 3) :c (3 4 5)"
  ([args]
     (parse-options (rest args) () (first args) ()))
  
  ([args-left parsed current-arg current-arg-value]
     (if-let [current (first args-left)]        
       (if (keyword? current)
	 (recur (rest args-left)
		(cons current-arg (cons current-arg-value parsed))
		current
		())
	 (recur (rest args-left)
		parsed
		current-arg
		(cons current current-arg-value)))
       (apply sorted-map (cons current-arg (cons current-arg-value parsed))))))

(defn- create-options-applier [keyword-option-map]
  (fn [route keyword-args-map]
    (let [option-keyword (first keyword-args-map)
	  ;;add route object in the argument list as first element
	  option-args (cons route (second keyword-args-map)) 
	  option-function (get keyword-option-map option-keyword)]
      (if-not (nil? option-function)
	(option-function option-args)
	route))))

(defn apply-options [route options options-map]
  (let [options-applier (create-options-applier options-map)]
    (reduce options-applier route (parse-options options))))

(defn third [col]
  (second (rest col)))

(defn fourth [col]
  (second (rest (rest col))))


(defn arg-count [f]
  "count the number of arguments of function"
  (let [m (first (.getDeclaredMethods (class f)))
	p (.getParameterTypes m)]
    (alength p)))

(def unit-enums
     #{:microseconds TimeUnit/MICROSECONDS
       :milliseconds TimeUnit/MILLISECONDS
       :nanoseconds TimeUnit/NANOSECONDS
       :seconds TimeUnit/SECONDS})

(def unit-converters
     #{:minutes (partial * 60)
       :hours (partial * 60 60)
       :days (partial * 60 60 24)})

(defn convert-unit-to-seconds [amount timeunit]
  (if-let [convert-fn (get unit-converters timeunit)]
    (convert-fn amount)))



(defn pwd
  "Returns current working directory as a String.  (Like UNIX 'pwd'.)
  Note: In Java, you cannot change the current working directory."
  []
  (System/getProperty "user.dir"))

(def webdir (str (pwd) "/src"))

(defn render [t]
  (apply str t))

(defn render-snippet [s]
  (apply str (html/emit* s)))

(def render-to-response
     (comp response render))

(defn page-not-found [req]
  {:status 404
   :headers {"Content-type" "text/html"}
   :body "Page Not Found"})

(defn render-request [afn & args]
  (fn [req] (render-to-response (apply afn args))))

(defn serve-file [filename]
  (file-response
   {:root webdir
    :index-files? true
    :html-files? true}))

(defn run-server* [app & {:keys [port] :or {port 8080}}]
  (let [nses (if-let [m (meta app)]
               [(-> (:ns (meta app)) str symbol)]
               [])]
    (println "run-server*" nses)
    (run-jetty
     (-> app
         (wrap-file webdir)
         (wrap-reload nses)
         (wrap-stacktrace))
     {:port port :join? false})))

(defmacro run-server [app]
  `(run-server* (var ~app)))

(defmulti parse-int type)
(defmethod parse-int java.lang.Integer [n] n)
(defmethod parse-int java.lang.String [s] (Integer/parseInt s))

(defmacro maybe-substitute
  ([expr] `(if-let [x# ~expr] (html/substitute x#) identity))
  ([expr & exprs] `(maybe-substitute (or ~expr ~@exprs))))

(defmacro maybe-content
  ([expr] `(if-let [x# ~expr] (html/content x#) identity))
  ([expr & exprs] `(maybe-content (or ~expr ~@exprs))))

(defn pluralize [astr n]
  (if (= n 1)
    (str astr)
    (str astr "s")))




