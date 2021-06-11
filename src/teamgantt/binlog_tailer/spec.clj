(ns teamgantt.binlog-tailer.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [dumpa.stream]))

(s/def ::string (s/and (complement string/blank?) string?))

(s/def ::user ::string)

(s/def ::password ::string)

(s/def ::host ::string)

(s/def ::port int?)

(s/def ::db ::string)

(s/def ::server-id int?)

(s/def ::event #{:on-connect :on-communication-failure :on-event-deserialization-failure :on-disconnect :on-event})

(s/def ::table keyword?)

(s/def ::tables (s/coll-of ::table :kind vector?))

(s/def ::callbacks (s/map-of ::event fn?))

(s/def ::config (s/keys :req-un [::user ::password ::host ::port ::db ::server-id] :opt-un [::callbacks ::tables]))

(s/def ::op-type #{:upsert :delete})

(s/def ::id any?)

(s/def ::content map?)

(s/def ::metadata map?)

(s/def ::row (s/tuple ::op-type ::table ::id ::content ::metadata))

(s/def ::callback (s/fspec :args (s/cat :row ::row)
                           :ret  any?))

(s/def ::stream #(instance? dumpa.stream.BinlogStream %))
