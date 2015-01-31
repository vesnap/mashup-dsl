(ns mashup-dsl.sources-test
(:use  [midje.sweet]
       [mashup-dsl.sources]))

(defn camel2 [](create (route (from (timer))
                (to (jetty-endpoint "http://www.google.com"))
                (to  (file-comp "target/google")))))

(fact "google get"  
(start-test (camel2))
(stop-test (camel2)))

;from("timer://foo?fixedRate=true&delay=0&period=10000")
;.to("http://www.google.com")
;.setHeader(FileComponent.HEADER_FILE_NAME, "message.html").to("file:target/
;google");


