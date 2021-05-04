from pathlib import Path

import numpy as np
import librosa
from PIL import Image
from fastai.vision import open_image
from pydub import AudioSegment

from chord_recognizer_service.views import signal_to_log_mel_spec, save_mel_spec, SR, learn

src = "1577208656097.mp4"
# sound = AudioSegment.from_file(src, format="mp4")
# samples = np.array(sound.get_array_of_samples(), dtype=float)
# print(len(samples))
# signal = librosa.resample(samples, 44100, 16000)
# librosa.output.write_wav("test.wav", signal, sr=16000)
# print(len(signal))
