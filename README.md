jopbox
========

*j*opbox is a Clojure library for working with the Dropbox API.

## Usage

**NOTE: You probably don't want to use this right now.**

    (use 'jopbox.client)

### Authorization and Authentication

    (def consumer (make-consumer API-KEY API-SECRET))
    (def request-token (fetch-request-token consumer <callback-uri>))

Now, get the authorization URL:

    (authorization-url consumer request-token)
    ;=> Visit the URL that this returns on your browser.

Then, get the access token response

    (def access-token-response (fetch-access-token-response consumer request-token))

If you're using this with a web app, you probably want to use a callback URI to fetch token response.

### API methods

See [Dropbox's API Reference][docs] for more information on these methods.

[docs]: https://www.dropbox.com/developers/reference/api

#### Account Info (`/account/info`)
    (account-info consumer access-token-response)

#### File Metadata (`/metadata`)
    (metadata consumer access-token-response "sandbox" "video.flv")

#### Delta (`/delta`)
    (delta consumer access-token-response <cursor>)

#### Media (`/media`)
    (media consumer access-token-response "sandbox" "video.flv")

#### Get File (`/files(GET)`)
    (get-file consumer access-token-response "sandbox" "foo.txt")
    ;; This works fine with plaintext files, but if you're dealing with something else you probably want to use /media.

#### Upload file (`/files_put`)
    (upload -file consumer access-token-response "sandbox" "foo.mp3" "/path/to/foo.mp3")

## License

Copyright © 2012 Samrat Man Singh

Distributed under the Eclipse Public License, the same as Clojure.
