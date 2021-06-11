(ns teamgantt.binlog-tailer
  (:require [clojure.spec.alpha :as s]
            [teamgantt.binlog-tailer.binlog :as binlog]
            [teamgantt.binlog-tailer.spec :as binlog.spec]))

(defn tail!
  "Initialize a binlog stream and execute a callback when it
  receives data

  config-map is a map as fed to dumpa/create-conf:
  https://github.com/teamgantt/dumpa/blob/master/src/dumpa/core.clj#L36
   
  config-map supports a :callbacks key for all callbacks supported by dumpa/create-conf, as well as
  a :tables key that optionally supports which tables to listen for changes on

  callback will be a function that is called when binlog changes are received:
  https://github.com/teamgantt/dumpa#row-format"
  [config-map callback]
  (binlog/tail! config-map callback))

(s/fdef tail!
  :args (s/cat :config-map ::binlog.spec/config :callback ::binlog.spec/callback)
  :ret  ::binlog.spec/stream)

(def close! binlog/close!)

(s/fdef close!
  :args (s/cat :stream ::binlog.spec/stream)
  :ret any?)
