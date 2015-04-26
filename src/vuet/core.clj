(ns vuet.core
  (:use [clojure.pprint :only (pprint)]
        [clojure.string :only (join)])
  (:require [clojure.zip :as z]
            [clojure.tools.namespace.repl :as repl])
  (:import jline.Terminal)
  (:gen-class))

(def char-range (seq "abcdefghijklmnopqrstuwvxyz")) ;TODO

(defn exit [& args]
  (println "Exiting")
  (System/exit 0))

(defn new-zipper [& args]
  (z/seq-zip '(1 (2 (3 4) 5))))

(defn contents [loc]
  (println "Node contents:")
  (println "  "(z/node loc))
  loc)

(defn show-path [loc]
  (println)
  (println "Path to here:")
  (dorun (map println (z/path loc))))

(defn help [& args]
  (println
    "(d)own, (u)p, (l)eft, (r)ight, show (c)ontents, (s)how path, (!)reset, (q)uit"))

(def char-fn-map
  {\d z/down
   \u z/up
   \r z/right
   \l z/left

   \n z/next
   \p z/prev

   \c contents
   \s show-path
   \! new-zipper
   \? help
   \q exit
   }
  )

(defn no-op [& args]
  (println "Unrecognized command."))

(defn interpret
  "Map char input to function-to-call"
  [c]
  (let [f (get char-fn-map (char c) nil)]
    (or f no-op)))

(defn -main
  "Loop indefinitely over user input, interpreting each char as a
  zipper command"
  [& args]
  (println "Walking zipper. '?' for help.")
  (let [init-zip (new-zipper)
        term (Terminal/getTerminal)]
    (loop [zipper init-zip]
      (println)
      (println zipper)
      (let [input (char (.readCharacter term System/in))
            next-fn (interpret input)
            next-zip (next-fn zipper)]
        (recur
          (or         ; action fns either
            next-zip  ; return the modified zipper or return nil,
            zipper))  ; in which case act again on current zipper
        ))))

