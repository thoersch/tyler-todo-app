(ns tylertodoapp.dbconfig)

(use 'korma.db)
(use 'korma.core)

(defn init! []
    (defdb conn (postgres {:db "todoapp_prod"
                         :user "todoapp"
                         :password "secret"
                         :host "postgres"
                         :port "5432"})))

(defn get-db-mig-config []
    {:store :database
     :migration-dir "migrations/"
     :init-in-transaction? false
     :db {:classname "org.postgresql.Driver"
          :subprotocol "postgresql"
          :subname "//postgres:5432/todoapp_prod"
          :user "todoapp"
          :password "secret"}})