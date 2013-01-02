jopbox
========

**NOTE: You probably don't want to use this right now.**

jopbox is a Clojure library for working with the Dropbox API.

## Installation

```clojure
[jopbox "0.1.0-SNAPSHOT"]
```

## Usage

Require `jopbox` from the REPL:

```clojure
(use 'jopbox.client)
```

### Authorization and Authentication

```clojure
(def consumer (make-consumer API-KEY API-SECRET))
(def request-token (fetch-request-token consumer <callback-uri>))
```

Now, get the authorization URL:

```clojure
(authorization-url consumer request-token)
;=> Visit the URL that this returns on your browser.
```

Then, get the access token response

```clojure
(def access-token-response (fetch-access-token-response consumer request-token))
```

If you're using this with a web app, you probably want to use a callback URI to fetch token response.

### API methods

See [Dropbox's API Reference][docs] for more information on these methods.

[docs]: https://www.dropbox.com/developers/reference/api

#### Account Info `/account/info`
```clojure
(account-info consumer access-token-response)
```

#### File Metadata `/metadata`
```clojure
(metadata consumer access-token-response "sandbox" "video.flv")
```

#### Delta `/delta`
```clojure
(delta consumer access-token-response <cursor>)
```

#### Media `/media`
```clojure
(media consumer access-token-response "sandbox" "video.flv")
```

#### Get File `/files(GET)`
```clojure
(get-file consumer access-token-response "sandbox" "foo.txt")
;; This works fine with plaintext files, but if you're dealing with something else you probably want to use /media.
```

#### Upload file `/files_put`
```clojure
(upload-file consumer access-token-response "sandbox" "foo.mp3" "/path/to/foo.mp3")
```

## License

Copyright © 2012 Samrat Man Singh

Distributed under the Eclipse Public License, the same as Clojure.
