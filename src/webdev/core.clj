(ns webdev.core
  (:require [webdev.item.model :as items]
            [webdev.item.handler :refer [handle-index-items
                                         handle-create-item
                                         handle-delete-item
                                         handle-update-item]])
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [compojure.core :refer [defroutes ANY GET POST PUT DELETE]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]]))

(def db (or
          (System/getenv "DATABASE_URL")
          {:dbtype "postgresql"
           :dbname "webdev"
           :user "postgres"}))

(defn greet [req]
  {:status 200
   :body "Hello World!"
   :headers {}})

(defn goodbye [req]
  {:status 200
   :body "Goodbye, cruel world!"
   :headers {}})

(defn about [req]
  {:status 200
   :body "My name is Caio Guedes! I created this site! Isn't it lovely?"
   :headers {}})

(defn yo
  [req]
  (let [name (get-in req [:route-params :name])]
    {:status 200
     :body (str "Yo! " name "!")
     :headers {}}))

(def ops {"+" +
          "-" -
          "*" *
          ":" /})

(defn calc
  [req]
  (let [op (get-in req [:route-params :op])
        f (get ops op)
        a (Integer. (get-in req [:route-params :a]))
        b (Integer. (get-in req [:route-params :b]))]
    (if f
      {:status 200
        :body (str (f a b))
        :headers {}}
      {:status 404
        :body "Unknown operator"
        :headers {}})))

(defroutes routes
           (GET "/" [] greet)
           (GET "/goodbye" [] goodbye)
           (GET "/about" [] about)
           (ANY "/request" [] handle-dump)
           (GET "/yo/:name" [] yo)
           (GET "/calc/:a/:op/:b" [] calc)
           (GET "/items" [] handle-index-items)
           (POST "/items" [] handle-create-item)
           (DELETE "/items/:item-id" [] handle-delete-item)
           (PUT "/items/:item-id" [] handle-update-item)
           (not-found "Page not found."))
(defn wrap-db
  [hdlr]
  (fn [req]
    (hdlr (assoc req :webdev/db db))))

(defn wrap-server
  [hdlr]
  (fn [req]
    (assoc-in (hdlr req) [:headers "Server"] "Listronica 9000")))

(def sim-methods {"PUT" :put
                 "DELETE" :delete})

(defn wrap-simulated-method [hdlr]
  (fn [req]
    (if-let [method (and (= :post (:require-method req))
                         (sim-methods (get-in req [:params "_method"])))]
      (hdlr (assoc req :request-method method))
      (hdlr req))))

(def app
  (-> routes
      (wrap-simulated-method)
      (wrap-params)
      (wrap-db)
      (wrap-resource "static")
      (wrap-file-info)
      (wrap-server)))

(defn -main [port]
  (items/create-table db)
  (jetty/run-jetty app {:port (Integer. port)}))

(defn -dev-main [port]
  (items/create-table db)
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)}))
