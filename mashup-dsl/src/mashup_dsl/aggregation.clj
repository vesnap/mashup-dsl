(ns mashup-dsl.aggregation
  (:use [clojure.test]
        [mashup-dsl.templating]
        [mashup-dsl.camel-dsl]
        [mashup-dsl.datamodel]
     
	[mashup-dsl.test-utils]
 ))
  
;(deftest aggregator-pattern
  ;(let [start data-url  
	;end  (mock "mashups")


	;r (route (from start)
		; (aggregator merge-lists "type" :count 2)
	;	 (to end))
;	camel (create r)]
    ;(start-test camel start end)
     
    ;(let [messages (get-received-messages end)]
    ;  (is (= (count messages) 2)))
   ; (stop-test camel)))

;mash operator
  ;html template + data
(deftest aggregator-pattern
  (let [start (direct data-url)
	end  (mock "end")
	f (fn []
	    (mshp (:data-content(data-for-mashup-stack "events" (xx)))))

	r (route (from start)
		 (aggregator f "type" :count 2)
		 (to end))
	camel (create r)]
    (start-test camel start end)
    (send-text-message camel start "Java" "type" "t1")
    (send-text-message camel start "Clojure" "type" "t2")
    (send-text-message camel start "Eclipse" "type" "t1")
    (send-text-message camel start "Emacs" "type" "t2")
    
    (let [messages (get-received-messages end)]
      (is (= (count messages) 2))
      (is (= (first messages) "JavaEclipse"))
      (is (= (second messages) "ClojureEmacs")))
    (stop-test camel)))

