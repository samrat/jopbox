(ns jopbox.client-test
  (:use clojure.test)
  (:require [jopbox.client :as jopbox])
  (:load "dropbox_keys"))

(def consumer (make-consumer dropbox-key dropbox-secret))

(deftest
  request-token
  (jopbox/fetch-request-token consumer) )