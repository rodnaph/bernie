
(ns bernie.core)

;; Utils
;; -----

(defn value-of [part]
  (subs part 2 (.indexOf part ";")))

(defn length-of [part]
  (let [data (subs part 2)]
    (Long/parseLong
      (subs data 0 (.indexOf data ":")))))

(defn with-rest [f part]
  [(f part) (subs part (.indexOf part ";"))])

;; Parsing
;; -------

(defn ->nil [part])

(defn ->long [part]
  (Long/parseLong (value-of part)))

(defn ->double [part]
  (Double/parseDouble (value-of part)))

(defn ->string [part]
  (let [length (length-of part)
        data (subs part 2)
        value (take length (subs data (+ 2 (.indexOf data ":"))))]
    [(apply str value) (subs data (+ 3 length))]))

(defn ->boolean [part]
  (if (= "1" (value-of part)) true false))

;; Dispatching
;; -----------

(defmulti ^{:doc "Return a vector of the matched value and the rest of the data."}
  parse #(subs % 0 1))

(defmethod parse "N" [part] (with-rest ->nil part))
(defmethod parse "b" [part] (with-rest ->boolean part))
(defmethod parse "i" [part] (with-rest ->long part))
(defmethod parse "d" [part] (with-rest ->double part))
(defmethod parse "s" [part] (->string part))

;; Public
;; ------

(defn unserialize [data]
  (first (parse data)))

