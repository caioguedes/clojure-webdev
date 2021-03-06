(defproject webdev "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring "1.6.3"]
                 [compojure "1.6.0"]
                 [org.clojure/java.jdbc "0.7.5"]
                 [org.postgresql/postgresql "42.1.4"]
                 [hiccup "1.0.5"]]
  :main webdev.core
  :min-lein-version "2.8.0"
  :uberjar-name "webdev.jar"
  :profiles {:dev
             {:main webdev.core/-dev-main}})