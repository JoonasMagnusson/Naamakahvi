import os
from flask import Flask
from flask import request, redirect, url_for
from werkzeug import secure_filename
import json
import dbmodule
import cvmodule



app = Flask(__name__)

dbm = dbmodule.dbmodule('test.db','dbqueries.xml')
cvm = cvmodule.cvmodule()

#Returns available products and other useful stuff. (not yet implemented,obviously)
@app.route('/')
def mainpage():
    return 'Naamakahvi server'   

#Trains the opencv-plugin.
#Input: name of uploaded file.
@app.route('/train/',methods=['POST','GET'])
def train():
	if request.method == 'POST':
		user = request.form['username']
		filename = request.form['filename']
		cvm.train(filename,user)
		return json.dumps({'status':'ok'})

	else:
		return json.dumps({'status':'Error'})


#Identifies user in input image
#input: cropped image
#output: sorted array containing usernames from best match to worst
@app.route('/identify/',methods=['POST','GET'])
def identify():
	if request.method == 'POST':
		user = request.form['username']
		filename = request.form['filename']
		idlist = cvm.identify(filename)
		return json.dumps({'status':'ok','idlist':idlist})
	else:
		return json.dumps({'status':'Error'})

#Handles file (image) uploads.
@app.route('/upload/', methods=['GET', 'POST'])
def upload():
	if request.method == 'POST':
	        file = request.files['file']
		if file:
			print file
			file.save(secure_filename(file.filename))
			return json.dumps({'status':'ok'})
		else:
			return json.dumps({'status':'NoFileInRequestError'})

	else:
		return json.dumps({'status':'Error'})		


#Creates new user, required fields are username,first name and surname.
@app.route('/register/',methods=['POST','GET'])
def register():
	if request.method == 'POST':
		
		user = request.form['username']
	
		if(not dbm.login(user)):
			dbm.register(user,'not','implemented')		
			return json.dumps({'status':'ok','username':user})
		else:
			return json.dumps({'status':'UserAlreadyExistsError'})
	else:
		return json.dumps({'status':'Error'})


#Logs user in. Good for checking if user exists.	
@app.route('/login/',methods=['POST','GET'])
def login():
	if request.method == 'POST':
		user = request.form['username']
		if(dbm.login(user)):		
			return json.dumps({'status':'ok','username':user})
		else:
			return json.dumps({'status':'NoSuchUserError'})
	else:
		return json.dumps({'status':'Error'})

@app.route('/buy/',methods=['POST','GET'])
def buy():
	return json.dumps({'status':'Error'})

if __name__ == '__main__':
    app.debug = True

    app.run(host='0.0.0.0')
