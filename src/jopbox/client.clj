(ns jopbox.client
  (:require [oauth.client :as oauth]
            [clj-http.client :as http]
            [cheshire.core :refer :all]))

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
  (let [request-url "https://api.dropbox.com/1/account/info"]
    (http/get request-url
              {:query-params (make-credentials consumer
                                               access-token-response
                                               :GET
                                               request-url
                                               nil)})))

(defn get-file
  "Downloads a file."
  [consumer access-token-response root path]
  (let [request-url (format "https://api-content.dropbox.com/1/files/%s/%s" root path)]
    (:body (http/get request-url
                     {:query-params (make-credentials consumer
                                                      access-token-response
                                                      :GET
                                                      request-url
                                                      nil)}))))

(defn media
  "Returns a link directly to a file."
  [consumer access-token-response root path]
  (let [request-url (format "https://api.dropbox.com/1/media/%s/%s" root path)]
    (:url (parse-string (:body (http/post request-url
                                          {:query-params (make-credentials consumer
                                                                           access-token-response
                                                                           :POST
                                                                           request-url
                                                                           nil)}))
                        true
                                                                           ))))

(defn delta
  ([consumer access-token-response cursor]
     (let [request-url "https://api.dropbox.com/1/delta"]
       (http/post request-url
                  {:query-params (make-credentials consumer
                                                   access-token-response
                                                   :POST
                                                   request-url
                                                   nil)})))
  ([consumer access-token-response]
     (delta consumer access-token-response nil)))

(defn upload-file
  "Upload file to Dropbox using PUT.
     root can be either `dropbox` or `sandbox`"
  [consumer access-token-response root remote-path local-path]
  (http/put (str "https://api-content.dropbox.com/1/files_put/" root "/" remote-path)
            {:query-params (make-credentials consumer
                                             access-token-response
                                             :PUT
                                             (str "https://api-content.dropbox.com/1/files_put/" root "/" remote-path)
                                             nil)
             :body (clojure.java.io/file local-path)
             }))

(defn metadata
  "Retrieve file and folder metadata."
  [consumer access-token-response root path]
  (let [request-url (format "https://api.dropbox.com/1/metadata/%s/%s" root path)]
    (parse-string (:body (http/get request-url
                                   {:query-params (make-credentials consumer
                                                                    access-token-response
                                                                    :GET
                                                                    request-url
                                                                    nil)})) true)))
