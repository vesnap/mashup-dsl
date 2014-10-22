(ns mashup-dsl.datamodel
  (:use
    [clj-xpath.core])
  (:require
    [clojure.zip :as z] 
    [clojure.xml :as xml ]
    [clojure.data.zip.xml :as zf]
    [clojure.java.io :as io]
    [clojure.pprint :as pp]
    [clojure.string :as s]
    [net.cgrand.enlive-html :as html]))

;;;;to do ;;;;
;;;add parsing json data
;;;add facebook api calls
;;;add twitter api calls


;example api url
(def data-url
  "http://api.eventful.com/rest/events/search?app_key=4H4Vff4PdrTGp3vV&keywords=music&location=New+York&date=Future")

(defn create-api-call [start-original-url condition rest-original-url]
  (str start-original-url  condition rest-original-url))


;;;;;;;;;;;;;;;;;;;;;;
;;;using clj-xpath;;;;
;;;;;;;;;;;;;;;;;;;;;;


;map of tags and its contents
(def events-xml2 
     (memoize (fn [] (slurp data-url))))
(def xmldoc2 
     (memoize (fn [] (xml->doc (events-xml2 )))))


(defn msh-contents2 [](zipmap [:data-content :title] [ (vec (map
                         (fn [item]
                           {:title ($x:text "./title" item)
                            :url  ($x:text "./url" item)})
                         (take 5
                               ($x "/search/events/event" (xmldoc2) )))) "Events mashup"]))


(defn item [url root-tag](take 5 ($x root-tag (xmldoc2 ))))

(defn create-root-tag [tags] 
  (str "/"(apply str (interpose \/ tags))))

(defn create-xpath [tag]
  (str "./" tag))

(def tags ["title" "url"])


  (defn xml-data [url] (slurp url))
  
  (defn defxmldoc [url]
    (xml->doc (xml-data url)))
  
;this is generalized version of msh-contents2
(defn msh-contents-try [url root-tag mshpname tags];probaj kod kuce
    (let [ items (take 5 ($x root-tag (defxmldoc url)))
        f (fn[item] (zipmap tags (map #($x:text (str %)) item))) ]
    (zipmap [:data-content :title] [(vec (f items)) mshpname])))

;;;;;bit differnt

(defn create-keys [tags]
  (into [] (map keyword tags)))

(defn tag-fn [tag] (partial $x:text tag))

(defn func-contents [tags root-tag data-url] 
  (map (apply juxt (map tag-fn tags)) (take 2 ($x root-tag (xml->doc (slurp data-url ))))))

(defn create-contents [tags root-tag data-url] 
  ( vec(map #(zipmap (create-keys tags) %) (func-contents tags root-tag data-url))))
  
;;;;;;;;contents for mashup;;;;;;

(defn contents-for-mashup [func mshpname]
  ;;this 
  ;;;;(zipmap [:data-content :title] [(create-contents ["url" "title"] "//event" data-url) "events mashup"])
  ;;;;works  
  (zipmap [:data-content :title] [func mshpname]));;doesn't work properly

  


;;;;;merging;;;;;;;
;;;;;using sets;;;; 
;;;;;this is used in content enrich;;;


;items for testing
(def item1 '({:title "title 1", :url "url1"} {:title "title 2", :url "url2"}))
(def item2 '({:name "title 1", :something "sss"} {:name "title 2", :something "ssss2222"}))

;for merging maps join from clojure.set is used
(defn merge-data [item1 item2 vec-of-names] 
 (vec (clojure.set/join (vec item1) (vec item2) vec-of-names)))

;enriching map, adding values;desn't work properly....
(defn enrich-map-with-data [v m t1 t2]
 (mapv (fn [x] (if (= (:t1 x) (:t2 m)) (merge m x) x)) v))






;;parsing source data;;;

;getting data from url, returns tag element struct map - :tag, :attrs, and :content
(defn parsing [url]
  (xml/parse url))

;returns zipper from source, similiar to parse
(defn zipp [data] 
  (z/xml-zip data))

;;examples of functions for getting data out of xml source
;helper function for fn map-tags-contents
(defn get-tag [tag url] 
  (:content (first (filter #(= tag (:tag %)) 
    (:content (parsing url))))))

;returns map of data, withhout the tag name 
;example call (get-content-from-tags data-url :events :event :title)
(defn get-content-from-tags [url & tags]
  (mapcat (comp :content z/node)
          (apply zf/xml->
                 (-> url xml/parse z/xml-zip)
	                  (for [t tags]
	                     (zf/tag= t)))))

;fn that gets map of tag and its content                 
;only 1 tag with its content
  ;example call (map-tags-contents data-url :events :event :title)

(defn map-tags-contents [url & tags]
    (map #(hash-map % (keyword (last tags)))
      (mapcat (comp :content z/node)
          (apply zf/xml->
                 (-> url xml/parse z/xml-zip)
                  (for [t tags]
                     (zf/tag= t)
                   )))))


;example functions for joining maps of data
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
  
(defn merge-lists [& maps]
  (reduce (fn [m1 m2]
            (reduce 
  (fn [m pair] (let [[[k v]] (seq pair)]
                 (assoc m k (cons v (m k))))) {} maps))))

(defn add-content [url coll]
  (map :contents (z/xml-zip (xml/parse url)) coll))

(defn update-map [m f] (reduce-kv (fn [m k v] (assoc m k (f v))) {} m))
