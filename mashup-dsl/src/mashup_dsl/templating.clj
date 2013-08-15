(ns mashup-dsl.templating (:use [net.cgrand.enlive-html :as html] 
        [net.cgrand.moustache ]
        [mashup-dsl.utils]
        [mashup-dsl.datamodel :as dm]
  [clojure.pprint]))
 

(def dummy-content
 {:title "Events Mashup"
   :event-data [{ :event-name "event name 1"
                        :performer "performer 1"
                      :date "date 1"
                           :start-time "start time 1"
                           :end-time "end time 1"}
      {:event-name "event name 2"
                           :performer "performer 2"
                          :date "date 2"
                           :start-time "start time 2"
                           :end-time "end time 2"}]})


;define snippets for "cell" and "row"

(def div-wrapper  (wrap :div {:class "psdg-right"}) )
(def title-wrapper (wrap :div {:class "psdg-left"}))

(defn make-div [elements ] (map div-wrapper elements))

(defn make-a-row [title values]
  (merge [ (title-wrapper title) (make-div values)]))


(def template-div (html-resource "index.html"))

(def cell-selector (select template-div  [:div.psdg-right]))
 
(defsnippet cell-model "index.html" cell-selector
  [data]
  [:div.psdg-right] 
        (content data ))
(def tmpl-html
  (html/html-snippet
   "<div id=\"psdg-top\">
    <div class=\"psdg-top-cell\" style=\"width:129px; text-align:left; padding- left:24px;\">Summary</div>
    
    </div>
    <div class=\"psdg-right\">10 000</div>"))

;; define a snippet based on some divs in the template
(html/defsnippet header-cell tmpl-html [[:.psdg-top-cell (html/nth-of-type 1)] ][value]
  (html/content value))

(html/defsnippet value-cell tmpl-html [[:.psdg-right (html/nth-of-type 1)]] [value]
  (html/content value))

;; define a template
(html/deftemplate mshp tmpl-html [content]
      [:#psdg-top] (html/append (for [c (keys (first content))] (header-cell (name c))))
      [:.psdg-right] (html/append (for [c (mapcat vals content)] (value-cell c))))

(defn map-of-data [](into [] (map #(into [](vals %)) (:event-data dummy-content))))

(deftemplate t2 "index.html" [title data] 
  [:div.psdg-left]  (substitute (make-a-row title data)))


(def table-template (html/html-resource "index2.html"))

(def ^:dynamic *section-sel* {[:title][[:tbody (attr= :title "events")]]})


(html/defsnippet row-snippet table-template [[:tr (attr= :title "event")]]
  [{:keys [event-name performers date start-time end-time]}]

  [[:td (attr= :title "event-title")]] (html/content event-name)
  [[:td (attr= :title "performer")]] (html/content performers)
  [[:td (attr= :title "date")]] (html/content date)
  [[:td (attr= :title "start-time")]] (html/content start-time)
  [[:td (attr= :title "end-time")]] (html/content end-time))

;(deftemplate indeks table-template
 ;[{:keys  [title event-data]}]
;[:title] (html/content title)
;[:tbody]  (html/content (map #(row-snippet %) (create-map-of-events)
 ;                          )))
(def mapping-templates
  {"event-title" :event-name
   "performer" :performers
   "date" :date
   "start-time" :start-time
   "end-time" :end-time
   "mbid" :mbid
   "url" :url})

(deftemplate indeks table-template [{:keys  [title data-content]}]
  [:title] (html/content title)
  [[:tr (nth-child 2)]] (html/clone-for [event data-content]
                        [:td] (fn [td] (assoc td :content [(-> td :attrs :title mapping-templates event)]))));show the page
;ovde bi trebalo map [:td] na contents
(def routes 
     (app
      [""]  (fn [req] (render-to-response (indeks (data-for-mashup-stack "events mashup" (xx)))))
      ;(fn [req] render-to-response (indeks content-t))
      [&]   page-not-found))

;; ========================================
;; The App
;; ========================================

(defonce ^:dynamic *server* (run-server routes))
