(ns tylertodoapp.server
  (:require [tylertodoapp.handler :refer [app]]
            [tylertodoapp.dbmigrations :as dbmig]
            [tylertodoapp.db :as db]
            [config.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

 (defn -main [& args]
    (dbmig/migrate (env :dev))
    (db/init!)
    (let [port (Integer/parseInt (or (env :port) "3000"))]
    (run-jetty app {:port port :join? false})))
