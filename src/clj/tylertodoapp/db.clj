(ns tylertodoapp.db
    (:require [tylertodoapp.dbconfig :as dbconfig]))

(use 'korma.db)
(use 'korma.core)

(defn init! []
    (dbconfig/init!))

(defentity todos
  (pk :id)
  (table :todos))

(defn get-all-todos []
    (select todos
        (order :created :ASC)))

(defn delete-todo-by-id [id]
    (delete todos
        (where {:id id})))

(defn delete-done-todos []
    (delete todos
        (where {:done true})))

(defn mark-all-todos-as [done?]
    (update todos
        (set-fields {:done done?})))

(defn insert-new-todo [title]
    (insert todos
        (values {:title title :done false})))

(defn update-todo [id title done?]
    (update todos
        (set-fields {:title title
                     :done done?})
        (where {:id id})))