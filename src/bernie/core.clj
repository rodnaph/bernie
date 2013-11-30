
(ns bernie.core)

(defn value-of [data]
  (subs data 2 (dec (count data))))

(defmulti parse #(subs % 0 1))

(defmethod parse "N"
  [_]
  nil)

(defmethod parse "b"
  [data]
  (if (= "1" (value-of data))
    true
    false))

(defmethod parse "i"
  [data]
  (Long/parseLong (value-of data)))

(defmethod parse "d"
  [data]
  (Double/parseDouble (value-of data)))

(defmethod parse "s"
  [data]
  (let [re #"^\d+:\"(.*)\"$"]
    (println (value-of data))
    (second (re-matches re (value-of data)))))

(defn unserialize [data]
  (parse data))

