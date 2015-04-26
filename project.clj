(defproject vuet "0.1.0-SNAPSHOT"
  :description "Tiny interactive zipper viewer"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [jline "0.9.94"]]
  :plugins [[cider/cider-nrepl "0.8.2"]]
  :main ^:skip-aot vuet.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
