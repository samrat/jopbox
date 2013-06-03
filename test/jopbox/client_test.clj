(ns jopbox.client-test
  (:use clojure.test)
  (:require [jopbox.client :as jopbox])
  (:load "dropbox_keys"))

(def consumer (jopbox/make-consumer dropbox-key dropbox-secret))

(deftest
  test-request-token
  (jopbox/fetch-request-token consumer))

(deftest
  test-user-authorization-url
  (is (instance? String (jopbox/authorization-url
                         consumer
                         (jopbox/fetch-request-token consumer)))))