(ns tylertodoapp.dbmigrations
    (:require [migratus.core :as migratus]
              [tylertodoapp.dbconfig :as dbconfig]))

(defn migrate [dev?]
    (migratus/migrate (dbconfig/get-db-mig-config)))