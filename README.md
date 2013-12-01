
# Bernie, PHP Serialization Parser

A library to parse data serialized by PHP into Clojure data structures.

## Usage

```clojure
(require [bernie.core :refer [unserialize]])

(unserialize (slurp "/path/to/session.data"))

