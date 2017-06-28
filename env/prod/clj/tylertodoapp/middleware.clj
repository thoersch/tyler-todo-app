(ns tylertodoapp.middleware
  (:require [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.json :as middleware]))

(defn wrap-middleware [handler]
  (-> handler
    wrap-keyword-params
    (wrap-defaults api-defaults)
    (middleware/wrap-json-body {:keywords? true})
    (middleware/wrap-json-response)))
