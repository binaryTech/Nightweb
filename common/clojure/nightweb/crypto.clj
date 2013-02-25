(ns nightweb.crypto
  (:use [nightweb.constants :only [base-dir
                                   priv-key-file
                                   pub-key-file]]))

(def priv-key nil)
(def pub-key nil)

(defn gen-priv-key
  []
  (let [context (net.i2p.I2PAppContext/getGlobalContext)
        key-gen (.keyGenerator context)
        signing-keys (.generateSigningKeypair key-gen)]
    (aget signing-keys 1)))

(defn load-user-keys
  [priv-key-bytes]
  (def priv-key (if priv-key-bytes
                  (net.i2p.data.SigningPrivateKey. priv-key-bytes)
                  (gen-priv-key)))
  (def pub-key (.toPublic priv-key)))

(defn create-signature
  [message]
  (.getData (.sign (net.i2p.crypto.DSAEngine/getInstance) message priv-key)))

(defn verify-signature
  [pub-key-bytes sig-bytes message-bytes]
  (if (and pub-key-bytes sig-bytes message-bytes)
    (.verifySignature (net.i2p.crypto.DSAEngine/getInstance)
                      (net.i2p.data.Signature. sig-bytes)
                      message-bytes
                      0
                      (alength message-bytes)
                      (net.i2p.data.SigningPublicKey. pub-key-bytes))))
