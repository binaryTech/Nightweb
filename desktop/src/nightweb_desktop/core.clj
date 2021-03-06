(ns nightweb-desktop.core
  (:gen-class)
  (:require [clojure.java.io :as java.io]
            [nightweb.router :as router]
            [nightweb-desktop.server :as server]
            [nightweb-desktop.utils :as utils]
            [nightweb-desktop.window :as window]))

(defn get-data-dir
  []
  (let [home-dir (System/getProperty "user.home")
        osx-dir (java.io/file home-dir "Library" "Application Support")
        win-dir (java.io/file home-dir "AppData" "Roaming")
        app-name (utils/get-string :app_name)]
    (.getCanonicalPath
      (cond
        (.exists osx-dir) (java.io/file osx-dir app-name)
        (.exists win-dir) (java.io/file win-dir app-name)
        :else (java.io/file home-dir
                            (str "." (clojure.string/lower-case app-name)))))))

(defn -main
  []
  (let [dir (get-data-dir)]
    (utils/check-update-periodically)
    (router/start-router dir)
    (server/start-server dir)
    (window/start-window)))
