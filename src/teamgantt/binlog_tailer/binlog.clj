(ns teamgantt.binlog-tailer.binlog
  (:require [dumpa.core :as dumpa]
            [manifold.stream :as s])
  (:import [com.github.shyiko.mysql.binlog BinaryLogClient]))

(defn- get-binlog-position
  "Get the bin log position that will be used to begin tailing from"
  [table-stream]
  (dumpa/next-position table-stream))

(defn- make-table-stream!
  "Create a table stream and start it"
  [conf tables]
  (let [table-stream (dumpa/create-table-stream conf tables)
        source       (dumpa/source table-stream)]
    (s/consume constantly source) ; setup a no-op consumer to avoid backpressure
    (dumpa/start-stream! table-stream)
    table-stream))

(defn- make-binlog-stream
  "Create a binlog stream"
  [conf table-stream]
  (let [binlog-pos (get-binlog-position table-stream)]
    (if (dumpa/valid-binlog-pos? conf binlog-pos)
      (dumpa/create-binlog-stream conf binlog-pos)
      (throw
        (ex-info "Invalid binlog position"
          {:position binlog-pos
           :config   conf})))))

(defn- on-failure
  "Forces a disconnect on a communication failure"
  [^BinaryLogClient client _]
  (.disconnect client))

(defn- on-disconnect
  "Since this bad brother is supposed to be running 100%, a disconnect exits the system with an error"
  [_]
  (System/exit 1))

(defn- event-callbacks
  "Provides some default callbacks to handle communication failure or disconnect."
  [config-map]
  (merge
   (get config-map :callbacks {}) 
   {:on-communication-failure (get-in config-map [:callbacks :on-communication-failure] on-failure)
    :on-disconnect            (get-in config-map [:callbacks :on-disconnect] on-disconnect)}))

(defn tail!
  [config-map callback]
  (let [conf          (dumpa/create-conf config-map (event-callbacks config-map))
        table-stream  (make-table-stream! conf (get config-map :tables []))
        binlog-stream (make-binlog-stream conf table-stream)
        source        (dumpa/source binlog-stream)]
    (s/consume callback source)
    (dumpa/start-stream! binlog-stream)
    binlog-stream))

(defn close!
  "Close streams in a stream map returned by tail!"
  [stream]
  (dumpa/stop-stream! stream))
