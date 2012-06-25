from flask import Flask
from flask import request
import json
import dbmodule

app = Flask(__name__)

users = []
dbm = dbmodule.dbmodule('test.db')

@app.route('/')
def mainpage():
    return 'Naamakahvi server'   


@app.route('/register/',methods=['POST','GET'])
def register():
	global users
	global dbm
	if request.method == 'POST':
		
		user = request.form['username']
	
		if(not dbm.login(user)):
			dbm.register(user,'not','implemented')
			users.append(user)
			print users
			print ""			
			return json.dumps({'status':'ok','username':user})
		else:
			return json.dumps({'status':'UserAlreadyExistsError'})
	else:
		return json.dumps({'status':'Error'})
	
@app.route('/login/',methods=['POST','GET'])
def login():
	global users
	global dbm
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
	global users
	if request.method == 'POST':
		user = request.form['username']
		if(user in users):
			productid = request.form['pid']
			count = request.form['count']
			return json.dumps({'status':'TransactionCompleted','username':user})
		else:
			return json.dumps({'status':'NoSuchUserError'})
	else:
		return json.dumps({'status':'Error'})

if __name__ == '__main__':
    #app.debug = True

    app.run(host='0.0.0.0')
