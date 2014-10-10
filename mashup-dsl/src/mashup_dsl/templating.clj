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

(defn template-div []
  (html/html [:style {:type "text/css"} 
            ".Table
    {
        display: table;
    }
    .Title
    {
        display: table-caption;
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
  (html/html [:html 
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


(html/defsnippet header-cell (template-div) [:div.Heading :div.Cell] [value]
              (html/content value))

(html/defsnippet value-cell (template-div) [:div.Row :div.Cell] [values]
             (html/clone-for [value values]
                     (html/content value)))


(html/defsnippet  value-row (template-div) 
                 [:div.Row] [event-data] (html/clone-for [vs event-data] [:div.Cell]
                                                         (html/clone-for [v vs] (html/content v))))

(defn update-map [m f] (reduce-kv (fn [m k v] (assoc m k (f v))) {} m))

(html/deftemplate mshp (index) [cont]
               [:div.Title] (html/content (:title cont))
               [:div.Heading] 
               (html/content (for [c (keys (first (:data-content cont)))] (header-cell (name c))))
               [:div.Row] 
              (html/content (value-row (mapv vals (:data-content cont)))))


(def routes 
     (app
      [""]  (fn [req] (render-to-response (mshp (msh-contents2))))
      ;(fn [req] render-to-response (indeks content-t))
      [&]   page-not-found))

;; ========================================
;; The App
;; ========================================

(defonce ^:dynamic *server* (run-server routes))
