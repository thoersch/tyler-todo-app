(ns tylertodoapp.middleware
  (:require [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.json :as middleware]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.reload :refer [wrap-reload]]))

(defn wrap-middleware [handler]
  (-> handler
      (wrap-defaults api-defaults)
      (middleware/wrap-json-body {:keywords? true})
      (middleware/wrap-json-response)
      wrap-exceptions
      wrap-reload))
