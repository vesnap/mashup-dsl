(ns mashup-dsl.pause)

(defn pause [r milis]
  "TODO: write docstring"
  (.delay r (long milis)))
