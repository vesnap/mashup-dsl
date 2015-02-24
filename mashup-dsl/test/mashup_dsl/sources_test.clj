(ns mashup-dsl.sources-test
(:use  [midje.sweet]
       [mashup-dsl.sources]
        [info.kovanovic.camelclojure.dsl]
        [mashup-dsl.test-utils]))


(def timer-end (timer "timer://foo?fixedRate=true&delay=0&period=10000" "timer"))
(def jetty-google(jetty-endpoint "http://www.google.com"))
(def file-google (file-comp "data\\outbox"))


(def camel2 (create (route (from timer-end)
               (to jetty-google)
            (to file-google)
              )))

(fact "google get"  
(start-test camel2 timer-end jetty-google file-google)
(stop-test camel2))

(def file-in (file-comp "data\\inbox\\test.txt"))
(def file-out (file-comp "data\\outbox\\"))

(def camel3 (create (route (from file-in)          
                (to  file-out))))

(fact "file test"  
      (start-test camel3 file-in file-out)
      (. Thread (sleep 60000))
      (stop-test camel3))




