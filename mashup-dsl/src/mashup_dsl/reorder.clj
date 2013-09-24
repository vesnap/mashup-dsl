(ns mashup-dsl.reorder
  (:use [mashup-dsl.utils :only (apply-options)]))

(defn- arg-map-ro []
  {:batch 
   {:batch         #(.batch (first %))
    :size          #(.size (first %) (second %))
    :timeout       #(.timeout (first %) (second %))
    :duplicates-ok #(.allowDuplicates (first %))
    :reverse       #(.reverse (first %))}
   :stream 
   {:stream        #(.stream (first %))
    :size          #(.capacity (first %) (second %))
    :timeout       #(.timeout (first %))}})

(defn reorder [r t h opts]
  (apply-options (.. r resequence (header h)) (cons t opts) (get (arg-map-ro) t)))
