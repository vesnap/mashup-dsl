(ns mashup-dsl.datamodel
  (:use
   ; [net.cgrand.enlive-html :as en-html]
     [clj-xpath.core]
   [clojure.string :as string]
     )
  (:require
    [clojure.zip :as z] 
    [clojure.xml :as xml ]
    [clojure.data.zip.xml :as zf]
     [clojure.java.io :as io]
     [clojure.pprint :as pp]
    ))
;
;
;schema matching
;
;

(def data-url "http://api.eventful.com/rest/events/search?app_key=4H4Vff4PdrTGp3vV&keywords=music&location=Belgrade&date=Future")


(defn parsing [url](xml/parse url))

;ovo je pomocna za map-tags-contents
(defn get-tag [tag url] (:content (first (filter #(= tag (:tag %)) (:content (parsing url))))))


;(defn f [] (map #(list (get-tag :events %) (get-tag :event (get-tag :title %)))
 ;               (:content (parsing data-url))))

(defn zipp [data] (z/xml-zip data))

(defn get-content-from-tags [url & tags]
  ;example call (get-content-from-tags data-url :events :event :title)
  ;ovo izvlaci bez imena taga
  (mapcat (comp :content z/node)
          (apply zf/xml->
                 (-> url xml/parse z/xml-zip)
	                  (for [t tags]
	                     (zf/tag= t)
                   ))))

(defn map-tags-contents [url & tags];map of tag and its content
  ;ovo izvlaci samo jedan tag, poslednji sa contentom,(map-tags-contents data-url :events :event :title)
  (map #(hash-map % (keyword (last tags)))
      (mapcat (comp :content z/node)
          (apply zf/xml->
                 (-> url xml/parse z/xml-zip)
                  (for [t tags]
                     (zf/tag= t)
                   )))))

(defn merge-disjoint
  "Like merge, but throws with any key overlap between maps"
  
  ([] {})
  ([m] m)
  ([m1 m2]
     (doseq [k (keys m1)]
       (when (contains? m2 k) (throw (RuntimeException. (str "Duplicate key " k)))))
     (into m2 m1))
  ([m1 m2 & maps]
     (reduce merge-disjoint m1 (cons m2 maps))))


(defn row-join ;ovaj join radi za 1 red, sad treba da posaljem ceo vektor sa mapama
           [m1 m2 key1 key2]
            (when (= (m1 key1) (m2 key2)) (into  m2 m1)))

;;;;ne znam za ovo (clojure.set/join test-left test-right {:name :owner})
;(join  {"Steve Bug" :title} {"Usce Zero" :title} :title :title)
;{"Usce Zero" :title, "Steve Bug" :title}
;=> (join  {"Steve Bug" :title} {"Usce Zero" :name} :title :name)
;{"Usce Zero" :name, "Steve Bug" :title}
  ;and i  or za join

  (defn left-join [key-map xs ys]
     (let [kes (seq key-map)
           lks (mapv key kes)
           rks (mapv val kes)
           gxs (group-by #(mapv (fn [k] (get % k)) lks) xs);prvi vektor sa izvucenim svim mapama koje imaju istu vrednost
           gys (dissoc (group-by #(mapv (fn [v] (get % v)) rks) ys) nil);drugi vektor sa izvucenim svim mapama koje imaju istu vrednost
           kvs (keys gxs)]
       (persistent!
        (reduce (fn [out kv]
                  (let [l (get gxs kv)
                        r (get gys kv)]
                    (if (seq r)
                      (reduce (fn [out m1]
                                (reduce (fn [out m2]
                                          (conj! out (merge m1 m2)))
                                        out
                                        r))
                              out
                              l)
                      (reduce conj! out l))))
                (transient [])
                kvs))))
  
;za spajanje redova
  ;(def data (vector (into  '[] (map-tags-contents data-url :events :event :title))
;(into  '[] (map-tags-contents data-url :events :event :venue_name))))
;ovo iznad ne radi dobro
;;(def merged-data (map (partial apply merge) 
 ;       (apply map vector data)))
;ovo moze i sa mapv, ali je on od 1.4 a ovaj projekat je 1.2
;(apply mapv merge data)
;mada je malo bzvz, najbolje da se izvuce nekoliko tagova u mapu


;merging two maps
;(defn merge-missing-keys [
 ;                          a-set 
  ;                         some-keys
   ;                      ]
    ;      (merge-with 
     ;                    #(or %1 %2) 
      ;                   a-set  
       ;                  (into {} (map (fn[x] {x nil}) some-keys))))

;(merge-missing-keys {:a 1 :b 20} '(:a :b :c :d :e ))



(defn merge-lists [& maps]
  (reduce (fn [m1 m2]
            (reduce 
  (fn [m pair] (let [[[k v]] (seq pair)]
                 (assoc m k (cons v (m k))))) 
  {} 
        maps))))


;(defmacro data-snippet [name url &tags];ovo sredi
 ; `(def ~name (map-tags-contents ~url ~tags)))

(def titles (map-tags-contents data-url :events :event :title));macro out of this

(def descriptions (map-tags-contents data-url :events :event :description))

(defn create-map [seq](map conj titles descriptions ));macro out of this

;(defn data [url] (en-html/xml-resource url)) 

(defn add-content [url coll]
  (map :contents (z/xml-zip (xml/parse url)) coll))

;import data source - connect to data source, 

;parsing and data mapping

;----------!!!!!!!!!!!!!---------------
;----------!G!L!A!V!N!O!---------------
;----------!!!!!!!!!!!!!---------------

;for defining entities in the model of the mashup
;all structs to records
(defmacro defentity [name & values]
  `(defrecord ~name [~@values]))
;this fn call has to go in some kind of mapping on vector made of xml data

(def apis (defentity api-name api-url api-format))

;(defrecord event [event-name performers start-time stop-time])
(def events-url "http://api.eventful.com/rest/events/search?app_key=4H4Vff4PdrTGp3vV&keywords=music&location=Belgrade&date=Future")


;for mapping data from the apis to the model
;we need a macro that will do this
;for now mapping will be manual
;(def a_mapping {"att1" :att1 "att2" :att2 "att3" :att3...})
;using fns - keys
;and (into {} (map (juxt identity name) [:a :b :c :d]))
(defn static? [field]
  (java.lang.reflect.Modifier/isStatic
   (.getModifiers field)))

(defn get-record-field-names [record]
    (->> record
        .getDeclaredFieldsto
       (remove static?)
     (map #(.getName %))
     (remove #{"__meta" "__extmap"})))

(defmacro empty-record [record]
  (let [klass (Class/forName (name record))
        field-count (count (get-record-field-names klass))]
    `(new ~klass ~@(repeat field-count nil))))


;dodavanje podataka u mapu
;http://jakemccrary.com/blog/2010/06/06/inserting-values-into-a-nested-map-in-clojure/
;user> (-> (add-to-cache {} :chicago :lakeview :jake)
 ;         (add-to-cache :sf :mission :dan)
  ;        (add-to-cache :chicago :wickerpark :alex))
;{:sf {:mission :dan}, :chicago {:wickerpark :alex, :lakeview :jake}}

(defn add-to-cache [cache key1 key2 data]
  (assoc-in cache [key1 key2] data))



;model to html - enlive in templating

(defn to-keys [& args]
  (for [k args] (map #(keyword %) k)))


;;;selecting multiple tags from source


(defn selector [tag]
  (if (sequential? tag)
    #(apply zf/xml1-> % (concat tag [zf/text]))
    #(zf/xml1-> % tag zf/text)))

(defn create-tag [tag]
 (if (sequential? tag) (last tag) tag))

(defn get-contents [url] )

(defn get-events
  [xml & tags]
  (let [events (zf/xml-> xml :events :event)
        fs     (map selector tags)]
    (map (apply juxt fs) events)))
     
(defn testing2[]
(-> data-url 
  parsing 
 (get-events :title :start_time [:performers :performer :name] :stop_time)
 ; first 
  prn))



(defn get-text-from-tag [zipp tag] (zf/xml1-> zipp tag zf/text))

(defn zipped-data [xz &tags] (zf/xml-> xz &tags))
;prvo treba ovo (def root :events :event)
;hocu da mogu ovo da napisem (get-events url root source :title :name start_time :stop_time)
;i iz njega da izadje ovo dole



;ovo probaj da prebacis da bude sa obicnom mapom
; (defn create-map-of-events [event]
 ;  (map #(apply struct event %)(get-events2 (z/xml-zip (xml/parse "http://api.eventful.com/rest/events/search?app_key=4H4Vff4PdrTGp3vV&keywords=music&location=Belgrade&date=Future")))))

; (defn create-map-of-artists-lastfm  []
 ; (map #(apply struct artist-lastfm %) (lastFmToArtist (z/xml-zip (xml/parse "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=Cher&api_key=b75950afc5678fba4e962def9105c1ee")))))
 
; (defn create-map-of-artists-musicbrainz  []
  
 ;  (map #(apply struct artist-musicbrainz %) (musicBrainzToArtist (z/xml-zip (xml/parse "http://www.musicbrainz.org/ws/2/artist/?query=artist:cher")))))
 
;(defn events-for-mashup2 []
 ; (let [title "Events mashup" event-data (vector (create-map-of-events event-map ))]
  ;  (struct event-map title event-data)))

(def ^:dynamic *map-for-mashups* {:title :content})




(def tags [:title :start_time [:performers :performer :name] :stop_time])

(defn xx []  (map #(zipmap (map create-tag tags) %) (get-events (parsing data-url) :title :start_time [:performers :performer :name] :stop_time)))

(defn events-for-mashup []
  (let [title "Events mashup" 
        event-data (xx)]
     (map {} {:title :content} {"Events mashup" (vec (xx))})))

;(defn proba[] (map {} {:title :content} {"Events mashup" (xx)}))
;this should be macro's output
(defmacro data-for-mashup [mashup-name func]  
        `(zipmap [:title :content] [~mashup-name '(~func)]))
;nearly there (map {} {:title :content} {"Events mashup" (xx)})

(defn data-for-mashup-stack [mashup-name val] (zipmap [:title :data-content] [mashup-name (vec val)]));this is it

 
;_exchange.getOut().setBody(createEarthquake(title.substring(7), date, title.substring(2,5), latitude, longitude, depth, area))





;;;;;;;;;;;;;;;;;;;;;;
;;;using clj-xpath;;;;
;;;and it works perfectly;;;;;
;;;;;;;;;;;;;;;;;;;;;;

(def events-xml
     (memoize (fn [] (slurp data-url))))



(defn get-tags [maintag] ($x:text  maintag 
                            (xmldoc)));main tag je "/events/event/title"

(defn all-tags [doc]
  (map
   ;; turn the keywords into strings
   name
   (seq
    ;; reduce the stream of nodes into a distinct list
    (reduce
     (fn [acc node]
       (conj acc (:tag node)))
     #{}
     ;; tree-seq flattens the document into a one-dimensional stream
     ;; of nodes:
     (tree-seq (fn [n] (:node n))
               (fn [n] ($x "./*" n))
               (first ($x "./*" doc)))))))


(defn visit-nodes
  ([path nodes f]
     (vec
      (mapcat
       #(vec
         (cons
          ;; invoke the callback on the each of the nodes
          (f (conj path (:tag %1)) %1)
          ;; visit each of the children of this node
          (visit-nodes
           (conj path (:tag %1))
           ($x "./*" %1) f)))
       nodes))))


(comment (visit-nodes []
               ($x "./*" (xmldoc))
               (fn [p n]
                 (printf "%s tag:%s\n"
                         (apply str (interpose "/" (map name p)))
                         (name (:tag n))))))

(defn all-paths [doc]
  (map
   #(str "/" (string/join "/" (map name %1)))
   (first
    (reduce
     (fn [[acc set] p]
       (if (contains? set p)
         [acc set]
         [(conj acc p) (conj set p)]))
     [[] #{}]
     (visit-nodes []
                  ($x "./*" doc)
                  (fn [p n]
                    p))))))

(map
             (fn [item]
               {:title ($x:text "./title" item)
                :url  ($x:text "./url" item)})
             (take 5
                   ($x "/search/events/event" (xmldoc))))


(def item (take 5 ($x "/search/events/event" (xmldoc))))

(def xmldoc
     (memoize (fn [] (xml->doc (events-xml)))))
(defn create-xpath [tag] (str "./" tag))

(def tags ["title" "url"])

(defn parse2 [item]
        (doseq [tag tags](into {} (keyword tag) ($x:text (create-xpath tag) item)) ))

(def ks [:url :title])

(defn parse[]
(map #(zipmap ks %) (map (juxt (tag-fn "url") (tag-fn "title")) (take 2 ($x "//event" (xmldoc))))))



;;;;;merging;;;;
;;;;;using sets;;;;
(def item1 '({:title "title 1", :url "url1"} {:title "title 2", :url "url2"}))
(def item2 '({:name "title 1", :something "sss"} {:name "title 2", :something "ssss2222"}))
(clojure.set/join (vec item1) (vec item2) {:name :title})
;;;and then everything back to vector
((vec '#{:something "sss" :name "name1"}))