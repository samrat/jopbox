(ns jopbox.client
  (:require [oauth.client :as oauth]
            [clj-http.client :as http]
            [cheshire.core :refer :all])
  (:use clojure.java.io))

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
  (let [request-url "https://api.dropbox.com/1/account/info"
        credentials (make-credentials consumer
                                      access-token-response
                                      :GET
                                      request-url
                                      nil)]
    (parse-string (:body (http/get request-url
                                   {:query-params credentials}))
                  true)))

(defn get-file
  "Downloads a file."
  [consumer access-token-response root path]
  (let [request-url (format "https://api-content.dropbox.com/1/files/%s/%s"
                            (name root)
                            path)
        credentials (make-credentials consumer
                                      access-token-response
                                      :GET
                                      request-url
                                      nil)]
    (:body (http/get request-url
                     {:query-params credentials}))))

(defn media
  "Returns a link directly to a file."
  [consumer access-token-response root path]
  (let [request-url (format "https://api.dropbox.com/1/media/%s/%s"
                            (name root)
                            path)
        (make-credentials consumer
                          access-token-response
                          :POST
                          request-url
                          nil)]
    (:url (parse-string (:body (http/post request-url
                                          {:query-params credentials}))
                        true))))

(defn delta
  ([consumer access-token-response cursor]
     (let [request-url "https://api.dropbox.com/1/delta"
           credentials (make-credentials consumer
                                         access-token-response
                                         :POST
                                         request-url
                                         nil)]
       (parse-string (:body (http/post request-url
                                       {:query-params credentials}))
                     true)))
  ([consumer access-token-response]
     (delta consumer access-token-response nil)))

(defn upload-file
  "Upload file to Dropbox using PUT.
     root: this can be either :dropbox or :sandbox
     remote-path: this is the path where the file will be uploaded to"
  [consumer access-token-response root remote-path local-path]
  (let [request-url (format "https://api-content.dropbox.com/1/files_put/%s/%s"
                            (name root)
                            remote-path)
        credentials (make-credentials consumer
                                      access-token-response
                                      :PUT
                                      request-url
                                      nil)]
    (parse-string (:body (http/put request-url
                                   {:query-params credentials
                                    :body (clojure.java.io/file local-path)}))
                  true)))

(defn metadata
  "Retrieve file and folder metadata."
  [consumer access-token-response root path]
  (let [request-url (format "https://api.dropbox.com/1/metadata/%s/%s"
                            (name root)
                            path)
        credentials (make-credentials consumer
                                      access-token-response
                                      :GET
                                      request-url
                                      nil)]
    (parse-string (:body (http/get request-url
                                   {:query-params credentials})) true)))

(defn create-folder
  [consumer access-token-response root path]
  (let [request-url "https://api.dropbox.com/1/fileops/create_folder"
        credentials (make-credentials consumer
                                      access-token-response
                                      :POST
                                      request-url
                                      nil)]
    (http/post request-url
               {:query-params credentials
                :body {:root (name root) :path path}})))
