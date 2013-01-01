# jopbox

Jopbox is a Clojure library for working with the Dropbox API

## Usage

*You probably don't want to use this right now.*

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

#### Account Info
    (account-info consumer access-token-response)

#### File Metadata
    (metadata consumer access-token-response "sandbox" "video.flv")

## License

Copyright © 2012 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
