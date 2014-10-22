(ns mashup-dsl.aggregation
  (:use [clojure.test]
        [mashup-dsl.camel-dsl]
        [mashup-dsl.datamodel]
        [mashup-dsl.templating]
      	[mashup-dsl.test-utils]))
  

(deftest aggregator-pattern
  (let [end  (mock "end")
	      f (fn []
	       (mshp (create-contents ["url" "title"] "//event" data-url)));here, the mshp fn should be called, 
                                                                     ;and its output should be called by render-response
	      r (route (from data-url)
	       (aggregator f "type" :count 1)
	       (to end))
	      camel (create r)]
   (start-test camel  end)
   (let [messages (get-received-messages end)]
   (is (= (count messages) 1)))
   (stop-test camel)))

