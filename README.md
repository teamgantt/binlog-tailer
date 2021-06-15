# binlog-tailer

A higher level abstraction over the [dumpa](https://github.com/teamgantt/dumpa) lib. Aims to provide
a simple interface for calling a function in response to a MySQL binlog event.

[![Clojars Project](https://img.shields.io/clojars/v/com.teamgantt/binlog-tailer.svg)](https://clojars.org/com.teamgantt/binlog-tailer)

## Usage

```clojure
(ns cool.biz
  (:require [teamgantt.binlog-tailer :as binlog]))

(def config {:user      "root"
             :password  "root"
             :host      "localhost"
             :port      3308
             :db        "tailer"
             :server-id 5
             :tables    [:users]})

(defn handler
  [[op table id content metadata]]
  (println (str "Operation: " op)) ;; :upsert or :delete
  (println (str "Table: " table)) ;; the database table of the row as a keyword -->
  (println (str "Id: " id)) ;; id of the row
  (println (str "Content: " content)) ;; full content of the row as a clojure map
  (println (str "Metadata: " metadata))) ;; info about the binlog event

(def stream (binlog/tail! config handler))
```

See `teamgantt.binlog-tailer.spec` for the full configuration spec. See the [dumpa](https://github.com/teamgantt/dumpa) repository for an in depth explanation of all configuration values.

## Development

`dev/src/dev.clj` provides a namespace for testing out binglog tailing.

```
clj:user:> (in-ns 'dev)
clj:dev:> (start!) ;; start listening for binlog events
clj:dev:> (stop!) ;; close the binlog stream and stop listening for events
```

Before using the dev repl, it is important to start the development mysql environment (requires docker):

```
$ bin/mysql
```

By default, the dev environment expects to connect to a database named `tailer` and watch a tabler called `users`.
