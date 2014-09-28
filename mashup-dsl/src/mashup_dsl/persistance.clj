(ns mashup-dsl.persistance
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:import [com.mongodb MongoOptions ServerAddress]))


;create database
;config file?
;insert document into collection
;mashup has a name and contents
;find document
;some kind of authentication, user table
;user-mashup collection

(let [conn (mg/connect)])

;; given host, default port
(let [conn (mg/connect {:host "db.mashups"})])


;; given host, given port
(let [conn (mg/connect {:host "db.megacorp.internal" :port 7878})])

 (mc/insert-and-return db "documents" 