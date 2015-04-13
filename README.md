jopbox
========

jopbox is a Clojure library for working with the Dropbox API.

## Demo

[joplet](http://github.com/samrat/joplet) a small demo app using this
library. It is easily deployable to Heroku. You can find the
instructions to do that in the `joplet` repository.


## Installation

Add this to your `project.clj`'s dependencies:

```clojure
:dependencies [jopbox "0.2.0"]
```

## Usage

Require `jopbox` from the REPL:

```clojure
(require '[jopbox.client :refer :all])
```

And before doing anything, you'll want to create a new Dropbox app [here](https://www.dropbox.com/developers/apps).

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

If you're using this with a web app, you probably want to use a
callback URI to fetch the token response.

### API methods

See [Dropbox's API Reference][docs] for more information on these methods.

[docs]: https://www.dropbox.com/developers/reference/api

#### Account Info `/account/info`
```clojure
(account-info consumer access-token-response)
```

#### File Metadata `/metadata`
```clojure
(metadata consumer access-token-response :sandbox "video.flv")
```

#### Delta `/delta`
```clojure
(delta consumer access-token-response <cursor>)
```

#### Media `/media`
```clojure
(media consumer access-token-response :sandbox "video.flv")
```

#### Get File `/files(GET)`
```clojure
(get-file consumer access-token-response :sandbox "foo.txt")
;; This works fine with plaintext files, but if you're dealing with something else you probably want to use /media.
```

#### Upload file `/files_put`
```clojure
(upload-file consumer access-token-response :sandbox "foo.mp3" "/path/to/foo.mp3")
```

#### Move file `/fileops/move`
```clojure
(move consumer access-token-response :sandbox "foo.mp3" "/path/to/foo.mp3")
;; Moves foo.mp3 in the root dir to /path/to/foo.mp3. The destination path must exist.
```

## Running Tests

Create a file `test/jopbox/dropbox_keys.clj` with the following
content:

```clojure
(def dropbox-key "YOUR-API-KEY")
(def dropbox-secret "YOUR-API-SECRET")
```

Then, run `lein test`.

## License

Copyright © 2013 Samrat Man Singh

Distributed under the Eclipse Public License, the same as Clojure.
