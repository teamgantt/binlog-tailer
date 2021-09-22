(ns dev
  (:require [mount.core :refer [defstate] :as mount]
            [teamgantt.binlog-tailer :as tailer]))

(def config {:user      "root"
             :password  "root"
             :host      "localhost"
             :port      3308
             :db        "tailer"
             :server-id 5
             :tables    #{:users}
             :callbacks {:on-disconnect #(println "disconnected")}})

(def handler (fn [[op table id content metadata]]
               (println (str "Operation: " op))
               (println (str "Table: " table))
               (println (str "Id: " id))
               (println (str "Content: " content))
               (println (str "Metadata: " metadata))))

(defn- create-stream! []
  (tailer/tail! config handler))

(defstate stream
  :start (create-stream!)
  :stop  (tailer/close! stream))

(defn start!
  ([config-map callback]
   (with-redefs [config config-map
                 handler callback]
     (mount/start)))
  ([]
   (mount/start)))

(defn stop! []
  (mount/stop))
