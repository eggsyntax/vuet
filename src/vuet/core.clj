(ns vuet.core
  (:use [clojure.pprint :only (pprint)]
        [clojure.string :only (join)])
  (:require [clojure.zip :as z])
  (:import jline.Terminal)
  (:gen-class))

(def char-range (seq "abcdefghijklmnopqrstuwvxyz")) ;TODO
(def index (atom 0))
(def history (atom '()))

(defn next-index []
  (swap! index inc))

(defn exit [& args]
  (println "Exiting")
  (System/exit 0))

(defn new-zipper [& args]
  (reset! index -1)
  (z/seq-zip (list (next-index))))

(defn contents [loc]
  (println)
  (println "Node contents:")
  (println "  " (z/node loc))
  loc)

(defn show-path [loc]
  (println)
  (println "Path to here:")
  (dorun (map println (z/path loc))))

(defn wrap
  "Wrap the current loc"
  [loc]
  (z/edit loc #(list %)))

(defn add-node
  "Add a new node, using the specified fn"
  [add-fn loc]
  (if (z/branch? loc)
    (add-fn loc (next-index))
    (println "\nCan't add a node from a leaf position")))

(defn insert [loc]
  (add-node z/insert-child loc))

(defn append [loc]
  (add-node z/append-child loc))

(defn z-next [loc]
  (if (z/end? (z/next loc))
    (println "Can't usefully continue.")
    (z/next loc)))

(defn print-history [& args]
  (println)
  (println "History:")
  (dorun (map println (reverse @history)))
  (println))

(defn -drop-and-return
  "Given an atom containing a list, strip the first item,
  store the shortened version, and return the stripped item"
  [list-atom]
  (let [drop-one (partial drop 1)
        item-to-strip (first @list-atom)]
    (swap! list-atom drop-one)
    item-to-strip))

(defn undo [& args]
    ; Drop the current state off the history
  (-drop-and-return history)
    ; Now drop the next-last state off the history and return it
    ; (It'll get added back on)
  (-drop-and-return history))

(defn print-from-root [loc]
  (println)
  (println "The view from root:")
  (print (z/root loc))
  (println))


(defn explain [loc]
  ;TODO
  ;(let [node ])
  )

(defn help [& args]
  (println)
  (println
   "(d)own, (u)p, (l)eft, (r)ight, show (c)ontents, (s)how path, e(x)plain, (!)reset, (q)uit, (i)nsert-child, (a)ppend-child, (w)rap"))

(def char-fn-map
  {\d z/down
   \u z/up
   \r z/right
   \l z/left

   \n z-next ; special handling for end
   \p z/prev

   \c contents
   \s show-path
   \x explain

   \i insert
   \a append
   \w wrap

   \h print-history
   \t print-from-root
   \! new-zipper
   \z undo
   \? help
   \q exit})

(defn no-op [& args]
  (println "Unrecognized command."))

(defn interpret
  "Map char input to function-to-call"
  [c]
  (let [f (get char-fn-map (char c) nil)]
    (or f no-op)))

(defn act-on
  "Determine and apply a function to the zipper based on input. Return the modified
 zipper (or return the original zipper if the called fn returns nil)."
  [zipper input]
  (let [next-fn (interpret input)
        next-zip (next-fn zipper)]
    (if next-zip
      (do
        ; TODO replace this complected swap with an add-watch elsewhere
        (swap! history conj next-zip)
        next-zip)
      zipper)))

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
            _ (println "zipper going in is " zipper)
            next-zip (act-on zipper input)
            _ (println "next-zip is now " next-zip)]
        (recur next-zip)))))
