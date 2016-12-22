(ns ensemble-analyzer.core
  (:gen-class))

(import '(java.io File)
        '(javax.sound.sampled AudioSystem)
        '(java.awt Dimension BorderLayout)
        '(javax.swing JFrame JPanel))

(require 'ensemble-analyzer.fft)
(alias 'fft 'ensemble-analyzer.fft)

(defn read-file []
  (let [stream (AudioSystem/getAudioInputStream
                (File. "../data/pascal_20161217.wav"))
        len (.available stream)
        buf (byte-array len)]
    (.read stream buf 0 len)
    (map (fn [[lsb msb]]
           (let [l (bit-and (int lsb) 0xFF)
                 m (bit-and (int msb) 0xFF)
                 x (bit-or (bit-shift-left m 8) l)]
             (if (= 0 (bit-and x 0x8000)) ; if plus
               x
               (- x 0x10000))))
         (partition 2 buf))))

(def sampling-rate 44.1e3) ; s^(-1)
(def nfft 4096)
(def fa 442) ; Hz

(defn make-panel []
  (proxy [JPanel] []
    (paintComponent [g]
      (proxy-super paintComponent g))
    (getPreferredSize [] 
      (Dimension. 200 200)
      )))

(defn make-frame []
  (let [frame (JFrame. "Ensemble Analyzer")
        panel (make-panel)]
    (.. frame getContentPane (add panel BorderLayout/CENTER))
    (.pack frame)
    (.setVisible frame true)
    frame))

(defn -main [& args]
  (let [frame (make-frame)]
    :done))