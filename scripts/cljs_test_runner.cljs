;; cljs CI runner: run the same deftests under nbb, exit nonzero on failure.
;; Under nbb, cljs.test's run-all-tests returns nil and its internal
;; counters aren't readable afterward, so the runner installs a custom
;; :nbb-ci env (report methods dispatch on it) via set-env!, flips an atom
;; on :fail/:error, and exits on the atom.
(require '[cljs.test :as ct :refer-macros [run-all-tests]]
         '[kotoba.nio.bytebuffer-test])

(def ^:private failures (atom 0))

(defmethod ct/report [:nbb-ci :fail] [m]
  (swap! failures inc)
  (ct/inc-report-counter! m :fail)
  (println "\nFAIL in" (ct/testing-vars-str m))
  (when-let [msg (:message m)] (println msg))
  (println "expected:" (pr-str (:expected m)))
  (println "  actual:" (pr-str (:actual m))))

(defmethod ct/report [:nbb-ci :error] [m]
  (swap! failures inc)
  (ct/inc-report-counter! m :error)
  (println "\nERROR in" (ct/testing-vars-str m))
  (when-let [msg (:message m)] (println msg))
  (println "  actual:" (pr-str (:actual m))))

(ct/set-env! (assoc (ct/empty-env) :reporter :nbb-ci))

(run-all-tests)

(js/process.exit (if (zero? @failures) 0 1))
