(ns mashup-dsl.aggregation
  (:use [clojure.test]
        ;[mashup-dsl.templating]
        [mashup-dsl.camel-dsl]
        [mashup-dsl.datamodel]
     
	[mashup-dsl.test-utils]
 ))
  

(deftest aggregator-pattern
 
 (let [
	end  (mock "end")
	f (fn []
	 (msh-contents  "name" "events" "title" "date"))

	r (route (from data-url)
	 (aggregator f "type" :count 1)
	(to end))
	camel (create r)]
   (start-test camel  end)
     
   (let [messages (get-received-messages end)]
 (is (= (count messages) 1))
      )
    (stop-test camel)))

