'''
title           : blockchain_client.py
description     : A blockchain client implemenation, with the following features
                  - Wallets generation using Public/Private key encryption (based on RSA algorithm)
                  - Generation of transactions with RSA encryption      
author          : Adil Moujahid
date_created    : 20180212
date_modified   : 20180309
version         : 0.3
usage           : python blockchain_client.py
                  python blockchain_client.py -p 8080
                  python blockchain_client.py --port 8080
python_version  : 3.6.1
Comments        : Wallet generation and transaction signature is based on [1]
References      : [1] https://github.com/julienr/ipynb_playground/blob/master/bitcoin/dumbcoin/dumbcoin.ipynb
'''

from collections import OrderedDict

import binascii

import Crypto
import Crypto.Random
from Crypto.Hash import SHA
from Crypto.PublicKey import RSA
from Crypto.Signature import PKCS1_v1_5
import json
import requests
from flask import Flask, jsonify, request, render_template
from flask_ngrok import run_with_ngrok
# pip install git+git://github.com/benamreview/flask-ngrok.git@blockchain-client

class Transaction:

    def __init__(self, sender_address, sender_private_key, recipient_address, value):
        self.sender_address = sender_address
        self.sender_private_key = sender_private_key
        self.recipient_address = recipient_address
        self.value = value


    def __getattr__(self, attr):
        return self.data[attr]

    def to_dict(self):
        return OrderedDict({'sender_address': self.sender_address,
                            'recipient_address': self.recipient_address,
                            'value': self.value})

    def sign_transaction(self):
        """
        Sign transaction with private key
        """
        private_key = RSA.importKey(binascii.unhexlify(self.sender_private_key))
        signer = PKCS1_v1_5.new(private_key)
        h = SHA.new(str(self.to_dict()).encode('utf8'))
        return binascii.hexlify(signer.sign(h)).decode('ascii')



app = Flask(__name__)
run_with_ngrok(app)
key_storage = {}
@app.route('/')
def index():
	return render_template('./index.html')

@app.route('/make/transaction')
def make_transaction():
    return render_template('./make_transaction.html')

@app.route('/make/transaction/message')
def make_transaction_message():
    return render_template('./make_transaction_message.html')

@app.route('/view/transactions')
def view_transaction():
    return render_template('./view_transactions.html')

@app.route('/wallet/new', methods=['GET'])
def new_wallet():
    name = request.args.get('name')
    if name is "" or name is None:
        name = 'anonymous'
    random_gen = Crypto.Random.new().read
    private_key = RSA.generate(1024, random_gen)
    public_key = private_key.publickey()
    response = {
        'private_key': binascii.hexlify(private_key.exportKey(format='DER')).decode('ascii'),
        'public_key': binascii.hexlify(public_key.exportKey(format='DER')).decode('ascii')
    }
    key_storage[name] = response
    with open('keys.json', 'w') as f:
        f.write(json.dumps(key_storage, indent = 4))
    return jsonify(response), 200

@app.route('/wallet/get', methods=['GET'])
def retrieve_wallet():
    response = {
        "data": []
    }
    global key_storage
    if len(key_storage) <= 0:
        with open('keys.json') as f:
            key_storage = json.loads(f.read())
    for key, val in key_storage.items():
        credential = {
            "name": key,
            'public_key': val['public_key'],
            'private_key': val['private_key']
        }
        response['data'].append(credential)

    return jsonify(response), 200

@app.route('/generate/transaction', methods=['POST'])
def generate_transaction():
	
	sender_address = request.form['sender_address']
	sender_private_key = request.form['sender_private_key']
	recipient_address = request.form['recipient_address']
	value = request.form['amount']

	transaction = Transaction(sender_address, sender_private_key, recipient_address, value)

	response = {'transaction': transaction.to_dict(), 'signature': transaction.sign_transaction()}

	return jsonify(response), 200


@app.route('/generate/transaction/custom', methods=['POST', 'GET'])
def generate_transaction_custom():
    if request.method == 'POST':
        sender_username = request.form['sender_username']
        recipient_username = request.form['recipient_username']
        value = request.form['amount']
        currency = request.form['currency'] if 'currency' in request.form else 'coin'
    else:
        sender_username = request.args.get('sender_username') if request.args.get('sender_username') is not None else "DuyHo"
        recipient_username = request.args.get('recipient_username') if request.args.get('recipient_username') is not None else "Srichakradhar"
        value = request.args.get('amount') if request.args.get('amount') is not None else '1'
        currency = request.args.get('currency') if request.args.get('currency') is not None else 'coin'
    if sender_username == "":
        sender_username = "DuyHo"
    if recipient_username == "":
        recipient_username = "Srichakradhar"
    if value == "":
        value = "1"
    if currency == "":
        currency = 'gold'
    value = "{} {}(s)".format(value, currency)
    sender_address = key_storage[sender_username]['public_key']
    sender_private_key =  key_storage[sender_username]['private_key']
    recipient_address = key_storage[recipient_username]['public_key']

    transaction = Transaction(sender_address, sender_private_key, recipient_address, value)

    response = {'transaction': transaction.to_dict(), 'signature': transaction.sign_transaction()}

    return jsonify(response), 200
@app.route('/generate/transaction/message', methods=['POST', 'GET'])
def generate_transaction_message():
    if request.method == 'POST':
        sender_username = request.form['sender_username']
        recipient_username = request.form['recipient_username']
        value = request.form['amount']
    else:
        sender_username = request.args.get('sender_username') if request.args.get('sender_username') is not None else "DuyHo"
        recipient_username = request.args.get('recipient_username') if request.args.get('recipient_username') is not None else "Srichakradhar"
        value = request.args.get('amount') if request.args.get('amount') is not None else 'Hi! How are you doing?'
    if sender_username == "":
        sender_username = "DuyHo"
    if recipient_username == "":
        recipient_username = "Srichakradhar"
    if value == "":
        value = "Default message"
    sender_address = key_storage[sender_username]['public_key']
    sender_private_key =  key_storage[sender_username]['private_key']
    recipient_address = key_storage[recipient_username]['public_key']


    transaction = Transaction(sender_address, sender_private_key, recipient_address, value)

    response = {'transaction': transaction.to_dict(), 'signature': transaction.sign_transaction()}

    return jsonify(response), 200

if __name__ == '__main__':
    from argparse import ArgumentParser

    parser = ArgumentParser()
    parser.add_argument('-p', '--port', default=8080, type=int, help='port to listen on')
    args = parser.parse_args()
    port = args.port

    app.run(host='127.0.0.1', port=port, debug=True)