# Python Flask- File upload

#import packages
from flask import Flask
import os
from flask import Flask, jsonify, request, redirect, url_for, render_template, send_from_directory
from werkzeug.utils import secure_filename
import sys
from PIL import Image
from os.path import dirname, abspath
import json
import tensorflow as tf
# import dlib
import cv2
import os
import numpy as np
from PIL import Image, ImageChops, ImageEnhance
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing.image import img_to_array, load_img
from tensorflow.keras.preprocessing import image

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

    return render_template('index1.html')


@application.route("/deepfake/predict", methods=['GET'])
def predict():
    if request.method == 'GET':
        img_name = request.args.get('filename')
        img_path = UPLOAD_FOLDER + '/' + img_name
        img = image.load_img(img_path)

        data1 = image.img_to_array(img)

        data = cv2.resize(data1, (128, 128)).flatten() / 255.0

        data = data.reshape(-1, 128, 128, 3)

        preds = {
            'result': 'real' if model.predict_classes(data)[0] == 1 else 'fake'
        }
        return jsonify(preds), 200

    return {}, 500


if __name__ == "__main__":
    application.debug = False
    application.run(host='0.0.0.0', port=8001)
