;; cljs CI runner: run the same deftests under nbb, exit nonzero on failure.
;; Under nbb, cljs.test's run-all-tests returns a PROMISE resolving to the
;; summary map — the reporter prints "0 failures" while the summary value
;; hasn't resolved, so the exit code must be decided inside .then.
(require '[cljs.test :refer-macros [run-all-tests]]
         '[kotoba.nio.bytebuffer-test])
(-> (run-all-tests)
    (.then (fn [summary]
             (if (and (zero? (:fail summary 1))
                      (zero? (:error summary 1)))
               0
               (do (println "cljs tests failed:" summary) 1)))
           (fn [e]
             (println "cljs test runner error:" e)
             1))
    (.then js/process.exit))
