(ns mashup-dsl.templating 
  (:use [net.cgrand.enlive-html :as html] 
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

(def div-wrapper  (html/wrap :div {:class "psdg-right"}) )
(def title-wrapper (html/wrap :div {:class "psdg-left"}))

(defn make-div [elements ] (map div-wrapper elements))

(defn make-a-row [title values]
  (merge [ (title-wrapper title) (make-div values)]))


;(defn template-div[] (html/html-resource "index.html"))


(defn template-div []
  (h/html [:style {:type "text/css"} 
            ".Table
    {
        display: table;
    }
    .Title
    {
        display: table-caption;
        text-align: center;
        font-weight: bold;
        font-size: larger;
    }
    .Heading
    {
        display: table-row;
        font-weight: bold;
        text-align: center;
    }
    .Row
    {
        display: table-row;
    }
    .Cell
    {
        display: table-cell;
        border: solid;
        border-width: thin;
        padding-left: 5px;
        padding-right: 5px;
    }"]
           [:div {:class "Table"}
                [:div {:class "Title"}
                      [:p "This is a Table"]]
                [:div {:class "Heading"}
                      [:div {:class "Cell"}
                            [:p "Heading 1"]]]
                [:div {:class "Row"}
                      [:div {:class "Cell"}
                            [:p "Row 1 Column 1"]]]]))

(defn index []
  (h/html [:html 
           [:style {:type "text/css"} 
            ".Table
    {
        display: table;
    }
    .Title
    {
        display: table-caption;
        text-align: center;
        font-weight: bold;
        font-size: larger;
    }
    .Heading
    {
        display: table-row;
        font-weight: bold;
        text-align: center;
    }
    .Row
    {
        display: table-row;
    }
    .Cell
    {
        display: table-cell;
        border: solid;
        border-width: thin;
        padding-left: 5px;
        padding-right: 5px;
    }"]
           [:div {:class "Table"}
                       [:div {:class "Title"}
                             [:p "This is a Table"]]
                       [:div {:class "Heading"}
                             [:div {:class "Cell"}
                                   [:p "Heading 1"]]]
                       [:div {:class "Row"}
                             [:div {:class "Cell"}
                                   [:p "Row 1 Column 1"]]]]]))

(def cell-selector (html/select (template-div)  [:div.psdg-right]))
 
(html/defsnippet cell-model "index.html" cell-selector
  [data]
  [:div.psdg-right] 
        (html/content data ))

;(defn tmpl-html []
 ; (html/html-resource
  ; "<html> <body><div id=\"psdg-top\">
   ; <div class=\"psdg-top-cell\" style=\"width:129px; text-align:left; padding- left:24px;\">Summary</div>
    ;
    ;</div>
    ;<div class=\"psdg-right\">10 000</div> </body> </html>"))

;; define a snippet based on some divs in the template
(html/defsnippet header-cell (template-div) [:div.Heading :div.Cell] [value]
              (html/content value))

(html/defsnippet value-cell (template-div) [:div.Row :div.Cell] [value]
              (html/content value))

(html/deftemplate mshp (index) [cont]
               [:div.Title] (html/content (:title cont))
               [:div.Heading] 
               (html/content (for [c (keys (first (:data-content cont)))] (header-cell (name c))))
               [:div.Row] 
               (html/content (map #(value-cell %) (for[e (:data-content cont)] (vals e)))))

;call to my lovely template (mshp (:data-content(data-for-mashup-stack "events" (xx))))
;ovaj xx nesto nece tako da ovo (mshp (:event-data dummy-content)) radi

;(defn map-of-data [](into [] (map #(into [](vals %)) (:event-data dummy-content))))

;(deftemplate t2 "index.html" [title data] 
 ; [:div.psdg-left]  (substitute (make-a-row title data)))


;(def table-template (html/html-resource "index2.html"))

;(def ^:dynamic *section-sel* {[:title][[:tbody (attr= :title "events")]]})



;(deftemplate indeks table-template
 ;[{:keys  [title event-data]}]
;[:title] (html/content title)
;[:tbody]  (html/content (map #(row-snippet %) (create-map-of-events)
 ;                          )))
;(def mapping-templates
 ; {"event-title" :event-name
 ;  "performer" :performers
 ;  "date" :date
;   "start-time" :start-time
  ; "end-time" :end-time
   ;"mbid" :mbid
  ; "url" :url})

;(deftemplate indeks table-template [{:keys  [title data-content]}]
 ; [:title] (html/content title)
  ;[[:tr (nth-child 2)]] (html/clone-for [event data-content]
   ;                     [:td] (fn [td] (assoc td :content [(-> td :attrs :title mapping-templates event)]))));show the page
;ovde bi trebalo map [:td] na contents
(def routes 
     (app
      [""]  (fn [req] (render-to-response (mshp (msh-contents))))
      ;(fn [req] render-to-response (indeks content-t))
      [&]   page-not-found))

;; ========================================
;; The App
;; ========================================

(defonce ^:dynamic *server* (run-server routes))
