# Python Flask- File upload

#import packages
from flask import Flask
import os
from flask import Flask, Response, jsonify, request, redirect, url_for, render_template, send_from_directory
from werkzeug.utils import secure_filename
import sys
from PIL import Image
from os.path import dirname, abspath
import json
import requests
# # import dlib
import cv2
import numpy as np
import os
from PIL import Image, ImageChops, ImageEnhance

import tensorflow as tf

from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing.image import img_to_array, load_img
from tensorflow.keras.preprocessing import image


gpus = tf.config.experimental.list_physical_devices('GPU')
if gpus:
    try:
        # Currently, memory growth needs to be the same across GPUs
        for gpu in gpus:
            tf.config.experimental.set_memory_growth(gpu, True)
        logical_gpus = tf.config.experimental.list_logical_devices('GPU')
        print(len(gpus), "Physical GPUs,", len(logical_gpus), "Logical GPUs")
    except RuntimeError as e:
        # Memory growth must be set before GPUs have been initialized
        print(e)

model = load_model('deepfake-detection-model.h5')

# UPLOAD_FOLDER = os.path.dirname(os.path.abspath(__file__)) + '/uploads'
UPLOAD_FOLDER = dirname(dirname(dirname(abspath(__file__)))) + \
    "/Java/TT-Server/GGFSKGQYAJ/plugins/Images"

ALLOWED_EXTENSIONS = {'jpg', 'jpeg', 'png', 'JPG', 'JPEG', 'PNG'}

application = Flask(__name__, static_url_path="/static")
DIR_PATH = os.path.dirname(os.path.realpath(__file__))
application.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

# limit upload size upto 8mb
application.config['MAX_CONTENT_LENGTH'] = 8 * 1024 * 1024


def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


#application = Flask(__name__)
port = 8888
addr = 'http://localhost:{}'.format(port)
test_url = addr + '/api/test'
test_url = addr + '/deepfake/predict'


@application.route("/", methods=['GET', 'POST'])
def index():
    if request.method == 'POST':
        if 'file' not in request.files:
            print('No file attached in request')
            return redirect(request.url)
        file = request.files['file']
        if file.filename == '':
            print('No file selected')
            return redirect(request.url)
        if file and allowed_file(file.filename):
            filename = secure_filename(file.filename)
            print(filename)

        file.save(os.path.join(application.config['UPLOAD_FOLDER'], filename))

        path = (os.path.join(application.config['UPLOAD_FOLDER'], filename))
        print("path :", path)

        result = path.split("/")
        filename2 = result[-1:]
        print("fname :", filename2)
        filename1 = " ".join(filename2)

        # prepare headers for http request
        content_type = 'image/jpeg'
        headers = {'content-type': content_type}

        img = cv2.imread(path)
        # encode image as jpeg
        _, img_encoded = cv2.imencode('.jpg', img)
        # send http request with image and receive response
        response = requests.post(
            test_url, data=img_encoded.tobytes(), headers=headers)

        # decode response
        print(json.loads(response.text))

    return render_template('index1.html')


@application.route("/deepfake/predict", methods=['GET', 'POST'])
def predict():
    if request.method == 'GET':
        img_name = request.args.get('filename')
        img_path = UPLOAD_FOLDER + '/' + img_name
        img = image.load_img(img_path)
        print(img)
        data1 = image.img_to_array(img)

        data = cv2.resize(data1, (128, 128)).flatten() / 255.0

        data = data.reshape(-1, 128, 128, 3)

        preds = {
            'result': 'real' if model.predict_classes(data)[0] == 1 else 'fake'
        }
        return jsonify(preds), 200
    else:
        r = request
        # convert string of image data to uint8
        nparr = np.frombuffer(r.data, np.uint8)
        # decode image
        img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
        cv2.imwrite('latest.png', img)
        img = image.load_img('latest.png')
        print(img)
        # do some fancy processing here....
        data1 = image.img_to_array(img)

        data = cv2.resize(data1, (128, 128)).flatten() / 255.0

        data = data.reshape(-1, 128, 128, 3)

        preds = {
            'result': 'real' if model.predict_classes(data)[0] == 1 else 'fake'
        }
        return jsonify(preds), 200

    return {}, 500

# route http posts to this method


@application.route('/api/test', methods=['POST'])
def test():
    r = request
    # convert string of image data to uint8
    nparr = np.frombuffer(r.data, np.uint8)
    # decode image
    img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

    # do some fancy processing here....

    # do some fancy processing here....
    # data1 = image.img_to_array(img)
    data = cv2.resize(img, (128, 128)).flatten() / 255.0

    # encode response using jsonpickle
    data = data.reshape(-1, 128, 128, 3)
    # build a response dict to send back to client
    response = {'message': 'image received. size={}x{}'.format(img.shape[1], img.shape[0])
                }
    # encode response using jsonpickle
    response_pickled = jsonify(response)
    print(data)
    cv2.imwrite('latest.png', img)

    return response_pickled, 200


if __name__ == "__main__":
    application.debug = True
    application.run(host='0.0.0.0', port=port)
