(ns tylertodoapp.handler
  (:require [compojure.core :refer [GET PUT POST DELETE defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [tylertodoapp.middleware :refer [wrap-middleware]]
            [tylertodoapp.db :as db]
            [config.core :refer [env]]))

(def mount-target
  [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
    (head)
    [:body {:class "body-container"}
     mount-target
     (include-js "/js/app.js")]))

(defn get-todos []
  (db/get-all-todos))

(defn save-todo [todo]
  {:status 200 
   :body  (if (nil? (:id todo))
            (->> todo :title db/insert-new-todo)
            (do
              (db/update-todo (:id todo) (:title todo) (:done todo))
              todo))})

(defn delete-todo [id]
  (db/delete-todo-by-id (Integer/parseInt id))
  {:status 200
   :body {:deleted (Integer/parseInt id)}})

(defn delete-done-todos []
  (db/delete-done-todos)
  (get-todos))

(defn mark-all-todos [done?]
  (db/mark-all-todos-as (boolean (Boolean/valueOf done?)))
  (get-todos))

(defroutes routes
  (GET "/" [] (loading-page))
  (GET "/api/todos" [] (get-todos))
  (POST "/api/todos" [req :as body] (save-todo (:body body)))
  (PUT "/api/todos/mark-all/:val" [val] (mark-all-todos val))
  (DELETE "/api/todos/:id" [id] (delete-todo id))
  (DELETE "/api/todos/rm-done/" [] (delete-done-todos))
  (resources "/")
  (not-found "Not Found"))

(def app (wrap-middleware #'routes))
