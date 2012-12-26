(ns jopbox.client
  (:require [oauth.client :as oauth]
            [clj-http.client :as http]))

;; Authorization & Authentication
(defn make-consumer
  "Return Dropbox OAuth consumer."
  [app-key app-secret]
  (oauth/make-consumer app-key
                       app-secret
                       "https://api.dropbox.com/1/oauth/request_token"
                       "https://api.dropbox.com/1/oauth/access_token"
                       "https://www.dropbox.com/1/oauth/authorize"
                       :hmac-sha1))

(defn fetch-request-token
  "Return a request token that user may authorize."
  ([consumer callback-uri]
     (oauth/request-token consumer callback-uri))
  ([consumer]
     (fetch-request-token consumer nil)))

(defn authorization-url
  "Return authorization endpoint."
  [consumer request-token]
  (oauth/user-approval-uri consumer
                           (:oauth_token request-token)))

(defn fetch-access-token-response
  "Return a map with :oauth_token and :oauth_token_secret."
  [consumer request-token]
  (oauth/access-token consumer
                      request-token))

;; API calls

(defn make-credentials
  [consumer access-token-response method url body]
  (oauth/credentials consumer
                     (:oauth_token access-token-response)
                     (:oauth_token_secret access-token-response)
                     method
                     url
                     body))

(defn account-info
  "Retrieve information about the user's account."
  [consumer access-token-response]
  (http/get "https://api.dropbox.com/1/account/info"
            {:query-params (make-credentials consumer
                                             access-token-response
                                             :GET
                                             "https://api.dropbox.com/1/account/info"
                                             nil)}))

(defn delta
  ([consumer access-token-response cursor]
     (http/post "https://api.dropbox.com/1/delta"
            {:query-params (make-credentials consumer
                                             access-token-response
                                             :POST
                                             "https://api.dropbox.com/1/delta"
                                             nil)}))
  ([consumer access-token-response]
     (delta consumer access-token-response nil)))

(defn upload-file
  "Upload file to Dropbox using PUT.
     root can be either `dropbox` or `sandbox`"
  [consumer access-token-response root remote-path local-path]
  (http/post (str "https://api-content.dropbox.com/1/files/" root "/" remote-path)
            {:query-params (make-credentials consumer
                                             access-token-response
                                             :POST
                                             "https://api-content.dropbox.com/1/files_put/"
                                             nil)}
            {:body (clojure.java.io/file local-path)}))

(defn metadata
  "Retrieve file and folder metadata."
  [consumer access-token-response root path]
  (http/get (str "https://api.dropbox.com/1/metadata/" root "/" path)
            {:query-params (make-credentials consumer
                                             access-token-response
                                             :GET
                                             "https://api.dropbox.com/1/metadata/"
                                             nil)}))
