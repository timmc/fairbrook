(ns fairbrook.util)

(defmacro fn3->fn2
  "Expands to a function form taking three arguments x y z, drops x and calls
  the result of the form f with y and z. If g is specified, will insert a
  modified version of g at the end of f, such that calls on the form (g y z)
  within f is treated as (g x y z). Both forms must return functions, and f must
  expect a function taking two arguments as last parameter if g is specified."
  ([f-form]
     `(let [f# ~f-form]
        (fn [a# b# c#] (f# b# c#))))
  ([f-form g-form]
     `(let [orig-g# ~g-form]
        (fn [x# y# z#]
          (let [g# (fn [*y# *z#] (orig-g# x# *y# *z#))]
            ((~@f-form g#) y# z#))))))

(defmacro prep-args
  "Prepares the parameters given to the function returned by f-form as if called
   by (fn [params*] (res-of-f-form param-mods*)). With g-form, appends a
   function based upon g-form taking any amount of arguments, where any call to
   it will be equivalent of calling (g-form param*). f-form must take as many
   arguments as the amount of of param-mods, and g-form must take as many
   parameters as the size of params. It is okay to have different amount of
   params and param-mods." {:arglists '([[params*] [param-mods*] f-form]
   [[params*] [param-mods*] f-form])}
  ([params param-mods f-form]
     `(let [f# ~f-form]
        (fn ~params (f# ~@param-mods))))
  ([params param-mods f-form g-form]
     `(let [orig-g# ~g-form]
        (fn ~params
          (let [g# (fn [& ignore#] (orig-g# ~@params))
                f# (~@f-form g#)]
            (f# ~@param-mods))))))

(defmacro <<-
  "Performs ->> in reverse: Inserts the last element as the last item in the
  second last form, making a list of it if it is not a list already. If there
  are more forms, inserts the second last form as the last item in the third
  last form, etc."
  [& forms]
  `(->> ~@(reverse forms)))

(defn right
  "Returns the second value."
  ([a b] b)
  ([k a b] b))

(defn left
  "Returns the first value."
  ([a b] a)
  ([k a b] a))

(defn _
  "Returns true regardless of its input parameters."
  [& ignore] true)

(defn and-fn
  "Returns a function of n (2 or 3) arguments. Calls f1 on the first argument,
  f2 on the second argument, etc. Will return true if all of the calls yields
  true, otherwise false. Short-circuits, so f_i+1 will not be called if the call
  by f_i is false."
  ([f1 f2]
     (fn [v1 v2]
       (and (f1 v1) (f2 v2))))
  ([f1 f2 f3]
     (fn [v1 v2 v3]
       (and (f1 v1) (f2 v2) (f3 v3)))))

(defn or-fn
  "Returns a function of n (2 or 3) arguments. Calls f1 on the first argument,
  f2 on the second argument, etc. Will return true if any of the calls yields
  true, otherwise false. Short-circuits, so f_i+1 will not be called if the call
  by f_i is true."
  ([f1 f2]
      (fn [v1 v2]
        (or (f1 v1) (f2 v2))))
  ([f1 f2 f3]
     (fn [v1 v2 v3]
       (or (f1 v1) (f2 v2) (f3 v3)))))

(defn err-fn
  [& args]
  (throw (Exception. (apply str "Couldn't merge based on values: "
                            (interpose ", " args)))))
