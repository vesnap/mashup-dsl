(ns mashup-dsl.sources-test
(:use  [midje.sweet]
       [mashup-dsl.sources]
        [info.kovanovic.camelclojure.dsl]
        [mashup-dsl.test-utils]
        [mashup-dsl.datamodel]))


(def timer-end (timer "timer://foo?fixedRate=true&delay=0&period=10000" "timer"))
(def jetty-google(jetty-endpoint "http://www.google.com"))
;(def file-google (file-end  "D:\\outbox"))


;(def camel2 (create (route (from timer-end)
 ;              (to jetty-google)
  ;          (to file-google)
   ;           )))

;(fact "google get"  
;(start-test camel2 timer-end jetty-google file-google)
;(stop-test camel2))

(def file-in (file-end  "D:\\fajl.txt"))
(def file-out (file-end  "D:\\fajl.txt"))

(def camel3 (create (route (from file-in)          
              (to file-out))))

(fact "file test"  
      (start-test camel3 file-in file-out)
      (. Thread (sleep 60000))
      (stop-test camel3))

(fact "parsed test" 
  (let [start (direct "start")
	end   (mock "end")
	camel (create (route (from start :event)
			     (to end)
        (to file-out)))]
    (start-test camel start end file-out)
    (send-text-message2 camel start (slurp data-url))
    (is-message-count end 1)
    (stop-test camel)))



