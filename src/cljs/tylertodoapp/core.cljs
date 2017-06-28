(ns tylertodoapp.core
    (:require [reagent.core :as r :refer [atom]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [cljs.core.async :refer [<! >! chan]] 
              [cljs-http.client :as http])
    (:require-macros [cljs.core.async.macros :refer [go]]))

(defonce todos (r/atom (sorted-map)))

(defn api-call [method endpoint payload]
  (go (let [resp (<! (method endpoint {:headers {"Content-Type" "application/json"} :json-params payload}))
            json (.parse js/JSON (:body resp))
            conv (js->clj json :keywordize-keys true)]
    conv)))

(defn add-todo [text]
  (go (let [todo {:id nil :title text :done false}
            ret (<! (api-call http/post "/api/todos" todo))]
    (swap! todos assoc (:id ret) ret))))

(defn toggle [id]
  (go (let [todo (get @todos id)
            done? (->> todo :done not)
            updated (assoc-in todo [:done] done?)
            ret (<! (api-call http/post "/api/todos" updated))]
      (swap! todos assoc-in [id :done] done?))))

(defn save [id title] 
  (go (let [todo (get @todos id)
            updated (assoc-in todo [:title] title)
            ret (<! (api-call http/post "/api/todos" updated))]
    (swap! todos assoc-in [id :title] title))))

(defn delete [id]
  (go (let [ret (<! (api-call http/delete (str "/api/todos/" id) nil))]
    (swap! todos dissoc id))))

(defn clear-done [] 
  (go (let [ret (<! (api-call http/delete "/api/todos/rm-done/" nil))]
    (reset! todos (sorted-map))
    (doall (map #(swap! todos assoc (:id %) %) ret)))))

(defn mark-all [val]
  (go (let [ret (<! (api-call http/put (str "/api/todos/mark-all/" val) nil))]
    (reset! todos (sorted-map))
    (doall (map #(swap! todos assoc (:id %) %) ret)))))

(defonce init
  (go (let [ret (<! (api-call http/get "/api/todos" nil))]
    (doall (map #(swap! todos assoc (:id %) %) ret)))))

;; -------------------------
;; Views

(defn todo-input [{:keys [title on-save on-stop]}]
  (let [val (r/atom title)
        stop #(do (reset! val "")
                  (if on-stop (on-stop)))
        save #(let [v (-> @val str clojure.string/trim)]
                (if-not (empty? v) (on-save v))
                (stop))]
    (fn [{:keys [id class placeholder]}]
      [:input {:type "text" :value @val
               :id id :class class :placeholder placeholder
               :on-blur save
               :on-change #(reset! val (-> % .-target .-value))
               :on-key-down #(case (.-which %)
                               13 (save)
                               27 (stop)
                               nil)}])))

(def todo-edit (with-meta todo-input
                 {:component-did-mount #(.focus (r/dom-node %))}))

(defn todo-stats [{:keys [filt active done]}]
  (let [props-for (fn [name]
                    {:class (if (= name @filt) "selected")
                     :on-click #(reset! filt name)})]
    [:div
     [:span#todo-count
      [:strong active] " " (case active 1 "item" "items") " left"]
     [:ul#filters
      [:li [:a (props-for :all) "All"]]
      [:li [:a (props-for :active) "Active"]]
      [:li [:a (props-for :done) "Completed"]]]
     (when (pos? done)
       [:button#clear-completed {:on-click clear-done}
        "Clear completed " done])]))

(defn todo-item []
  (let [editing (r/atom false)]
    (fn [{:keys [id done title]}]
      [:li {:class (str (if done "completed ")
                        (if @editing "editing"))}
       [:div.view
        [:input.toggle {:type "checkbox" :checked done
                        :on-change #(toggle id)}]
        [:label {:on-double-click #(reset! editing true)} title]
        [:button.destroy {:on-click #(delete id)}]]
       (when @editing
         [todo-edit {:class "edit" :title title
                     :on-save #(save id %)
                     :on-stop #(reset! editing false)}])])))

(defn todo-app [props]
  (let [filt (r/atom :all)]
    (fn []
      (let [items (vals @todos)
            done (->> items (filter :done) count)
            active (- (count items) done)]
        [:div
         [:section#todoapp
          [:header#header
           [:h1 "Tyler's Todos"]
           [todo-input {:id "new-todo"
                        :placeholder "What needs to be done?"
                        :on-save add-todo}]]
          (when (-> items count pos?)
            [:div
             [:section#main
              [:input#toggle-all {:type "checkbox" :checked (zero? active)
                                  :on-change #(mark-all (pos? active))}]
              [:label {:for "toggle-all"} "Mark all as complete"]
              [:ul#todo-list
               (for [todo (filter (case @filt
                                    :active (complement :done)
                                    :done :done
                                    :all identity) items)]
                 ^{:key (:id todo)} [todo-item todo])]]
             [:footer#footer
              [todo-stats {:active active :done done :filt filt}]]])]
         [:footer#info
          [:p "Double-click to edit a todo"]]]))))

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [todo-app]
            (js/document.getElementById "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
