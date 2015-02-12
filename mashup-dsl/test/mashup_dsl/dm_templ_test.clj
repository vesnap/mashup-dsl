(ns mashup-dsl.dm-templ-test
  [:use [mashup.dsl.datamodel]
   [mashup-dsl.templating]
   [midje.sweet]])

;items for testing
(def item1 '({:title "title 1", :url "url1"} {:title "title 2", :url "url2"}))
(def item2 '({:name "title 1", :something "sss"} {:name "title 2", :something "ssss2222"}))


(fact merge-test
     (merge-data item1 item2 [:title :name]) )

(fact contents-extract-test 
      (contents-extract "events mashup" data-url "/search/events/event" [:title :url] ))

