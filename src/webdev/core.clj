(ns webdev.core
  (:require [webdev.item.model :as items])
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]]))

(def db {:dbtype "postgresql"
         :dbname "webdev"
         :user "postgres"})

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

(defroutes app
           (GET "/" [] greet)
           (GET "/goodbye" [] goodbye)
           (GET "/about" [] about)
           (GET "/request" [] handle-dump)
           (GET "/yo/:name" [] yo)
           (GET "/calc/:a/:op/:b" [] calc)
           (not-found "Page not found."))

(defn -main [port]
  (items/create-table db)
  (jetty/run-jetty app {:port (Integer. port)}))

(defn -dev-main [port]
  (items/create-table db)
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)}))
