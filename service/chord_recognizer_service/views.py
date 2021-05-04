import threading
import librosa
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
from fastai.vision import *

from chord_recognizer_service.forms import UploadSoundForm

learn = load_learner('./chord_recognizer_service/resnet-models')
SR = 16000
N_FFT = 1000
HOP_LENGHT = 250
N_MELS = 64
F_MIN = 20
F_MAX = 2000
IMG_SIZE = 64


@csrf_exempt
def upload(request):
    if request.method == 'POST':
        form = UploadSoundForm(request.POST, request.FILES)
        if form.is_valid():
            pred_chord = handle_file_upload(request.FILES['sound'])
            return HttpResponse(pred_chord, status=200)
        else:
            return HttpResponse(status=422)
    return HttpResponse(status=400)


def handle_file_upload(in_mem_file):
    global learn
    file_name = in_mem_file.name.split('.', 1)[0]

    with open("files/" + in_mem_file.name, 'wb+') as destination:
        for chunk in in_mem_file.chunks():
            destination.write(chunk)

    signal = librosa.load("files/" + in_mem_file.name, sr=SR)[0]
    mel_spec = signal_to_log_mel_spec(signal)
    save_mel_spec(mel_spec, Path("files"), file_name)
    im = open_image('files/%s.png' % file_name)
    pred = learn.predict(im)[0]

    clear_thread = threading.Thread(target=clear, args=(in_mem_file.name,file_name+".png"))
    clear_thread.start()

    print(file_name, pred, sep='\n')
    return pred


def signal_to_log_mel_spec(signal, sr=SR):
    mel_spec = librosa.feature.melspectrogram(signal, sr=sr, n_fft=N_FFT,
                                              hop_length=HOP_LENGHT,
                                              n_mels=N_MELS, power=1.0,
                                              fmin=F_MIN, fmax=F_MAX)

    return librosa.amplitude_to_db(mel_spec, ref=np.max)


def save_mel_spec(mel_spec, dst_path, fname):
    dst_fname = dst_path / (fname + '.png')
    plt.imsave(dst_fname, mel_spec)


def clear(chord_path,spec_path):
    os.remove("files/"+chord_path)
    os.remove("files/"+spec_path)
