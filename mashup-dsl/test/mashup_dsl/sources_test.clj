(ns mashup-dsl.sources-test
(:use  [midje.sweet]
       [mashup-dsl.sources]
        [info.kovanovic.camelclojure.dsl]
        [mashup-dsl.test-utils]))

;(defn camel2 [](create (route (from (timer "timer://foo?fixedRate=true&delay=0&period=10000" "timer"))
 ;               (to (jetty-endpoint "http://www.google.com"))
  ;              (to  (file-comp "target/google")))))

;(fact "google get"  
;(start-test (camel2))
;(stop-test (camel2)))
(def file-in (file-comp "data/inbox/test.txt?noop=true"))
(def file-out (file-comp "data/outbox/test.txt"))

(def camel3 (create (route (from file-in)
                (to  file-out))))

(fact "file test"  
(start-test camel3 file-in file-out)
(stop-test camel3))


;from("timer://foo?fixedRate=true&delay=0&period=10000")
;.to("http://www.google.com")
;.setHeader(FileComponent.HEADER_FILE_NAME, "message.html").to("file:target/
;google");


