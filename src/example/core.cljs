(ns example.core)

(defn on-reload []
  (.reload (.-location js/window)))

(def renderer (js/THREE.WebGLRenderer. (clj->js {:antialias true})))
(.setClearColor renderer 0xffffff)
(.setSize renderer (.-innerWidth js/window) (.-innerHeight js/window))

(let [canvas (js/document.getElementById "canvas")
      new-canvas (.-domElement renderer)]
  (set! (.-id new-canvas) "canvas")
  (.replaceChild js/document.body new-canvas canvas))

(def scene (js/physijs.Scene. "physijs-worker.min.js"))
(def camera (js/THREE.PerspectiveCamera. 50 (/ (.-innerWidth js/window) (.-innerHeight js/window)) 1 100))
(set! (.-z (.-position camera)) 10)

(def sphere-geometry (js/THREE.SphereGeometry. 1 12 12))
(def normal-material (js/THREE.MeshNormalMaterial.))

(def sphere (js/physijs.Sphere. sphere-geometry normal-material (clj->js {:mass 0 :type "RIGID"})))
(.add scene sphere)

(def box (js/physijs.Box. (js/THREE.BoxGeometry. 1 1 1) normal-material (clj->js {:mass 2})))
(.set (.-position box) 0.13 3 0)
(set! (-> box .-physics .-linear_velocity .-y) -5)
(.add scene box)

(def physics-framerate (/ 1000 60))

(defn one-step []
  (.render renderer scene camera)
  (js/setTimeout (.bind (.-step scene) scene (/ physics-framerate 1000) nil one-step)
                 physics-framerate))

(one-step)

