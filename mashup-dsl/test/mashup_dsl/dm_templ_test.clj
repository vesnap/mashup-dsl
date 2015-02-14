(ns mashup-dsl.dm-templ-test
  [:use [mashup-dsl.datamodel]
   ;[mashup-dsl.templating]
   [midje.sweet]])

(def source1 
  #{{:title "title 1" :url "url1"} {:title "title 2" :url "url2"}})

(def source2 
  #{{:name "title 1" :something "sss"} {:name "title 2" :something "ssss2222"}})


(def lastfmartist "http://ws.audioscrobbler.com/2.0/?method=artist.search&artist=cher&api_key=00c6b7abec24599649bfcecf19c08cf1")

;testing merging of 2 contents
(fact "merge-test"
     (merge-data source1 source2 {:title :name}) )

;extractiong data with title of mashup
(fact "contents-extract-test"
      (contents-extract "events" data-url "/search/events/event" [:title :url] ))


;extractiong data without title
(fact "contents only"
      (contents-only data-url "/search/events/event" [:title :url]))

;testing create set out of contents
(fact "set-test"
      (set (:data-content
          (contents-extract "title" 
                            data-url "/search/events/event" [:title :url]))))

(fact "contents-artist"
      ((contents-only 
           lastfmartist "/lfm/results/artistmatches/artist" [:name :mbid]))
      
      