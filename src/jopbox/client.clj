(ns jopbox.client
  (:require [oauth.client :as oauth]
            [clj-http.client :as http]
            [cheshire.core :refer :all])
  (:use clojure.java.io))

;; Authorization & Authentication
(defn make-consumer
  "Takes an API key and secret and returns a Dropbox OAuth consumer.
  The next step is to call fetch-request-token with the consumer
  returned here(optionally with a callback-url)."
  [app-key app-secret]
  (oauth/make-consumer app-key
                       app-secret
                       "https://api.dropbox.com/1/oauth/request_token"
                       "https://api.dropbox.com/1/oauth/access_token"
                       "https://www.dropbox.com/1/oauth/authorize"
                       :hmac-sha1))

(defn fetch-request-token
  "Takes a consumer and optionally a callback-uri and returns a request
  token that user will need to authorize."
  ([consumer callback-uri]
     (oauth/request-token consumer callback-uri))
  ([consumer]
     (fetch-request-token consumer nil)))

(defn authorization-url
  "Takes a consumer and request-token and returns a URL to send the
  user to in order to authenticate."
  [consumer request-token]
  (oauth/user-approval-uri consumer
                           (:oauth_token request-token)))

(defn fetch-access-token-response
  "Takes a consumer and request-token and returns a map with
  :oauth_token and :oauth_token_secret."
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
  "Retrieves information about the user's account."
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
        credentials (make-credentials consumer
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
  "Uploads file to Dropbox using PUT. `root` can be either :dropbox or
     :sandbox. `remote-path` is the path where the file will be
     uploaded to."
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
  "Retrieves file or folder metadata. `root` can be either :dropbox or
  :sandbox. `path` is the path of a folder or file."
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
  "Creates a folder at `path`. Root can be either :sandbox or :dropbox."
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
