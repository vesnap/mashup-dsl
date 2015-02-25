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
    [net.cgrand.enlive-html :as html]
    [clojure.data.json :as json]
    [clojure.java.io]))

;;;;to do ;;;;
;;;add parsing json data
;;;add facebook api calls
;;;add twitter api calls


;example api url
(def data-url
  "http://api.eventful.com/rest/events/search?app_key=4H4Vff4PdrTGp3vV&keywords=music&location=New+York&date=Future")

(defn create-api-call [start-original-url condition rest-original-url]
  (str start-original-url  condition rest-original-url))

(defn debomify
  "for removing BOM"
     [^String line]
     (let [bom "\uFEFF"]
       (if (.startsWith line bom)
         (.substring line 1)
         line)))

(defn create-keys [tags]
  (into [] (map keyword tags)))

(defn tag-fn [tag] (partial $x:text tag))

(defn create-root-tag [tags] 
  (str "/"(apply str (interpose \/ tags))))

(defn create-xpath [tag]
  (str "./" tag))

(defn xml-data [url] (debomify (slurp url)))
  
(defn defxmldoc [url]
  (xml->doc (xml-data url)))

(defn item [url root-tag](take 5 ($x root-tag (defxmldoc url ))))

(defn items [root-tag url] 
  (take 5 ($x root-tag (defxmldoc url))))
  
(defn text-fn [tag item]
  ($x:text ((str tag) item)))
 
(defn build-path
  ([sep kys] (build-path nil sep kys))
  ([root sep kys]
   (->> kys (map name) (interpose sep) 
        (concat (when root (list root sep))) (apply str))))

(defn save-to-file[dir file-name url]
  (with-open [wrtr (clojure.java.io/writer (str dir file-name))]
  (.write wrtr (xml-data url))))


(defn path
  "build a path from a collection"
  [t]
  (build-path "." \/ t))

(defn path-key
  "Transform [:a :b :c] into :a-b-c"
  [t]
  (->> t (build-path \-) keyword))
  
(defn contents-extract [title url root-tag tags] 
  (zipmap [:data-content :title] 
          [ (vec (map(fn [item]
             (into {}
                   (map (fn [tag]
                          [(path-key tag) ($x:text (path tag) item)])
                        tags)))
           (take 5 ($x root-tag (defxmldoc url))))) title]))

(defn contents-only [url root-tag tags]
  (vec (map(fn [item]
             (into {}
                   (map (fn [tag]
                          [(path-key tag) ($x:text (path tag) item)])
                        tags)))
           (take 5 ($x root-tag (defxmldoc url))))))


(defn merge-data [item1 item2 map-of-names] 
  "for merging maps join from clojure.set is used"
 (vec (clojure.set/join (set item1)  (set item2) map-of-names)))


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
  
(defn row-join [m1 m2 key1 key2]
 "joining one row"
(when (= (m1 key1) (m2 key2)) (into  m2 m1)))


  ;example functions for joining and extracting maps of data

(defn parsing [url]
  "getting data from url, returns tag element struct map - :tag, :attrs, and :content"
  (xml/parse url))


(defn zipp [data] 
  "returns zipper from source, similiar to parse"
  (z/xml-zip data))

  (defn get-tag [tag url] 
  "helper function for fn map-tags-contents"
  (:content (first (filter #(= tag (:tag %)) 
    (:content (parsing url))))))



(defn get-content-from-tags [url & tags]
  "returns map of data, withhout the tag name "
  (mapcat (comp :content z/node)
          (apply zf/xml->
                 (-> url xml/parse z/xml-zip)
	                  (for [t tags]
	                     (zf/tag= t)))))

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


(defn merge-lists [& maps]
  (reduce (fn [m1 m2]
            (reduce 
  (fn [m pair] (let [[[k v]] (seq pair)]
                 (assoc m k (cons v (m k))))) {} maps))))

(defn update-map [m f] (reduce-kv (fn [m k v] (assoc m k (f v))) {} m))

(defn map-tags-contents [url & tags]
   "fn that gets map of tag and its content                 
only 1 tag with its content" 
   (map #(hash-map % (keyword (last tags)))
      (mapcat (comp :content z/node)
          (apply zf/xml->
                 (-> url xml/parse z/xml-zip)
                  (for [t tags]
                     (zf/tag= t)
                   )))))