(ns tylertodoapp.dbconfig)

(use 'korma.db)
(use 'korma.core)

(defn init! []
    (defdb conn (postgres {:db "todoapp_dev"
                         :user "todoapp"
                         :password "secret"
                         :host "127.0.0.1"
                         :port "5433"})))
                        
(defn get-db-mig-config []
    {:store :database
     :migration-dir "migrations/"
     :init-in-transaction? false
     :db {:classname "org.postgresql.Driver"
          :subprotocol "postgresql"
          :subname "//127.0.0.1:5433/todoapp_dev"
          :user "todoapp"
          :password "secret"}})