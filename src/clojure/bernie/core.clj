
(ns bernie.core
  (:import com.pugh.bernie.UnserializeException)
  (:require [clojure.string :as str]
            [clojure.walk :refer [keywordize-keys]]))

(defmulti parse #(subs % 0 1))

(defn int? [value]
  (boolean
    (re-matches #"\d+" (str value))))

(defn index-of [c string]
  (.indexOf string c))

(def colon (partial index-of ":"))
(def semi (partial index-of ";"))

(defn value-of [part]
  (subs part 2 (semi part)))

(defn length-of [part]
  (let [data (subs part 2)]
    (Long/parseLong
      (subs data 0 (colon data)))))

(defn rest-of [f part]
  [(f part)
   (subs part (inc (semi part)))])

(defn content-of [data]
  (subs data (+ 2 (colon data))))

(defn ->vec [all]
  (apply vector (map second all)) )

(defn ->hashmap [keyvals]
  (keywordize-keys
    (apply hash-map keyvals)))

(defn ->vec-or-map [keyvals]
  (let [all (partition 2 keyvals)
        ks (map first all)]
    (if (every? int? ks)
      (->vec all)
      (->hashmap keyvals))))

(defn ->values [part]
  (let [data (subs part 2)]
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

(defn clean-nulls [value]
  (if (string? value)
    (str/replace value #"\x00.*\x00" "")
    value))

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
  (let [data (subs part 2)
        length (length-of part)]
    [(subs (content-of data) 0 length)
     (subs data (+ 4 (colon data) length))]))

(defn ->array [part]
  (let [[value more] (->values part)]
    [(->vec-or-map value)
     (if (> (count more) 0)
       (subs more 1))]))

(defn ->object [part]
  (let [length (length-of part)
        data (subs part (+ 4 length (colon (subs part 2))))
        [value more] (->values data)]
    [(->hashmap (map clean-nulls value))
     more]))

(defn ->custom [part]
  (let [data (subs part (+ 4 (length-of part) (colon (subs part 2))))
        length (length-of data)
        content (subs (content-of data) 3 (+ 3 length))]
    (parse content)))

;; Dispatching
;; -----------

(defmethod parse "N" [part] (rest-of ->nil part))
(defmethod parse "b" [part] (rest-of ->boolean part))
(defmethod parse "i" [part] (rest-of ->long part))
(defmethod parse "d" [part] (rest-of ->double part))
(defmethod parse "s" [part] (->string part))
(defmethod parse "a" [part] (->array part))
(defmethod parse "O" [part] (->object part))
(defmethod parse "C" [part] (->custom part))
(defmethod parse "R" [part] (throw (UnserializeException. "Custom serialization not supported")))

;; Public
;; ------

(defn unserialize [data]
  (first (parse data)))

