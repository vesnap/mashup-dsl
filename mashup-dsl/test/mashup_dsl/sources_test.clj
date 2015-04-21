(ns mashup-dsl.sources-test
(:use  [midje.sweet]
       [mashup-dsl.sources]
        [mashup-dsl.test-utils] 
        [info.kovanovic.camelclojure.dsl]))
       ; [mashup-dsl.datamodel]
       


;(def timer-end (timer "timer://foo?fixedRate=true&delay=0&period=10000" "timer"))
;(def jetty-google(jetty-endpoint "http://www.google.com"))
;(def file-googy4le (file-end  "D:\\outbox"))


;(def camel2 (create (route (from timer-end)
 ;              (to jetty-google)
  ;          (to file-google)
   ;           )))

;(fact "google get"  
;(start-test camel2 timer-end jetty-google file-google)
;(stop-test camel2))

;(def file-in (file-end  "D:\\fajl.txt"))
;(def file-out (file-end  "D:\\fajl.txt"))

;(def camel3 (create (route (from file-in)          
         ;     (to file-out))))

(fact "jms to file" 
  (let [start (direct "start")
	      fileend (file-end  "resources/out?fileName=test-$simple%7Bdate:now:yyyyMMdd%7D.txt")
	      camel (create (route (from start :event)
			                       (to fileend)))]
  (start-test camel start fileend)
  (send-text-message2 camel start "nesto")
 ; (fact (count-messages fileend)=> 1)
  (stop-test camel)))

(fact "parsed test" 
  (let [start (direct "start")
	      fileend (file-end  "/resources/file.txt")
	      camel (create (route (from start :event)
			                       (to fileend)))]
  (start-test camel start fileend)
  (send-text-message2 camel start "nesto")
 ; (fact (count-messages fileend)=> 1)
  (stop-test camel)))




;ovaj test radi
(fact "parsed mock" 
  (let [start (direct "start")
	end   (mock "end")
  camel (create (route (from start :event)
			  (to end)))]
  (start-test camel start end) 
  (send-text-message camel start "nesto")
  (fact (count-messages end)=> 1)
  (stop-test camel)))


