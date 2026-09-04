;; cljs CI runner: run the same deftests under nbb, exit nonzero on failure.
(require '[cljs.test :as ct :refer-macros [run-all-tests]]
         '[kotoba.nio.bytebuffer-test])
(def ^:private results (run-all-tests))
;; cljs.test's run-all-tests returns the summary map synchronously under nbb.
(js/process.exit (if (and (zero? (:fail (:summary results) 1))
                          (zero? (:error (:summary results) 1)))
                   0
                   1))
