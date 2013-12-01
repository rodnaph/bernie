
(ns bernie.core)

(defmulti parse #(subs % 0 1))

(defn int? [value]
  (boolean
    (re-matches #"\d+" (str value))))

(defn index-of [c string]
  (.indexOf string c))

(def colon (partial index-of ":"))
(def semi (partial index-of ";"))

(defn to-str [f]
  (comp (partial apply str) f))

(def stake (to-str take))
(def sdrop (to-str drop))

(defn value-of [part]
  (subs part 2 (semi part)))

(defn length-of [part]
  (let [data (subs part 2)]
    (Long/parseLong
      (stake (colon data) data))))

(defn rest-of [f part]
  [(f part)
   (sdrop (inc (semi part)) part)])

(defn data-of [part]
  (sdrop 2 part))

(defn content-of [data]
  (sdrop (+ 2 (colon data)) data))

(defn ->vec-or-map [keyvals]
  (let [all (partition 2 keyvals)
        ks (map first all)]
    (if (every? int? ks)
      (apply vector (map second all))
      (apply hash-map keyvals))))

(defn ->values [part]
  (let [data (data-of part)]
    (loop [results []
           content (content-of data)
           remaining (* 2 (length-of part))]
      (if (> remaining 0)
        (let [[value more] (parse content)]
          (recur
            (conj results value)
            more
            (dec remaining)))
        [results content]))))

;; Parsing
;; -------

(defn ->nil [part])

(defn ->boolean [part]
  (if (= "1" (value-of part)) true false))

(defn ->long [part]
  (Long/parseLong (value-of part)))

(defn ->double [part]
  (Double/parseDouble (value-of part)))

(defn ->string [part]
  (let [data (data-of part)
        length (length-of part)]
    [(stake length (content-of data))
     (sdrop (+ 5 length) data)]))

(defn ->array [part]
  (let [[value more] (->values part)]
    [(->vec-or-map value) (sdrop 1 more)]))

;; Dispatching
;; -----------

(defmethod parse "N" [part] (rest-of ->nil part))
(defmethod parse "b" [part] (rest-of ->boolean part))
(defmethod parse "i" [part] (rest-of ->long part))
(defmethod parse "d" [part] (rest-of ->double part))
(defmethod parse "s" [part] (->string part))
(defmethod parse "a" [part] (->array part))

;; Public
;; ------

(defn unserialize [data]
  (first (parse data)))

