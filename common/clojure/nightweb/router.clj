(ns nightweb.router
  (:use [nightweb.crypto :only [create-keys]]
        [nightweb.io :only [base32-encode]]
        [nightweb.torrent :only [start-download-manager create-download]]))

(def base-dir nil)
(def user-hash nil)

(defn start-router
  [dir]
  (def base-dir dir)
  (java.lang.System/setProperty "i2p.dir.base" dir)
  (java.lang.System/setProperty "i2p.dir.config" dir)
  (java.lang.System/setProperty "wrapper.logfile" (str dir "/wrapper.log"))
  (net.i2p.router.RouterLaunch/main nil)
  (start-download-manager)
  (let [pub-key-path (create-keys dir)
        info-hash (create-download pub-key-path)]
    (def user-hash (base32-encode info-hash))))

(defn stop-router
  []
  (if-let [contexts (net.i2p.router.RouterContext/listContexts)]
    (if-not (.isEmpty contexts)
      (if-let [context (.get contexts 0)]
        (.shutdown (.router context) net.i2p.router.Router/EXIT_HARD)))))
