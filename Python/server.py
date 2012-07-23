import os
from flask import Flask
from flask import request, redirect, url_for
from werkzeug import secure_filename
import json
import psqldb
import cvmodule

app = Flask(__name__)

dbm = psqldb.psqldb('naamakanta','sam','dbqueries.xml')
cvm = cvmodule.cvmodule()

def resp_ok(**kwargs):
    ans = {}
    ans['status'] = 'ok'
    for k, v in kwargs.iteritems():
        ans[k] = v
    return json.dumps(ans)

def resp_failure(status_msg, **kwargs):
    ans = {'status' : status_msg}
    for k, v in kwargs.iteritems():
        ans[k] = v
    return ans

@app.before_request
def before_request():
    dbm.dbconnect()
    
@app.teardown_request
def teardown_request(exception):
    dbm.dbclose()

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
        return resp_ok()

    else:
        return resp_failure()


#Identifies user in input image
#input: cropped image
#output: sorted array containing usernames from best match to worst
@app.route('/identify/',methods=['POST','GET'])
def identify():
    if request.method == 'POST':
        user = request.form['username']
        filename = request.form['filename']
        idlist = cvm.identify(filename)
        return resp_ok(idlist=idlist)
    else:
        return resp_failure('Error')

#Handles file (image) uploads.
@app.route('/upload/', methods=['GET', 'POST'])
def upload():
    if request.method == 'POST':
        file = request.files['file']
        user = request.form['username']
        if file:
            print file
            file.save(secure_filename(file.filename))
            cvm.train(secure_filename(file.filename),user)
            return resp_ok()
        else:
            return resp_failure('NoFileInRequestError')

    else:
        return resp_failure('Error')


#Creates new user, required fields are username,first name and surname.
@app.route('/register/',methods=['POST','GET'])
def register():
    if request.method == 'POST':
        user = request.form['username']
        given = request.form['given']
        family = request.form['family']
        if(not dbm.login(user)):
            dbm.register(user,given,family,5)
            return resp_ok(username=user)
        else:
            return resp_failure('UserAlreadyExistsError')
    else:
        return resp_failure('Error')


#Logs user in. Good for checking if user exists.
@app.route('/authenticate_text/',methods=['POST','GET'])
def login():
    if request.method == 'POST':
        user = request.form['username']
        if(dbm.login(user)):
            return resp_ok(username=user)
        else:
            return resp_failure('NoSuchUserError')
    else:
        return resp_failure('Error')

@app.route('/list_buyable_products/',methods=['POST','GET'])
def buyableProducts():

    ret = []
    rslt = dbm.selectFinProductNames()
    for x,y in enumerate(rslt):
        ret.append(({"product_name":y[2],"product_id":y[0],"product_price":1}))

    print ret
    return resp_ok(buyable_products=ret)


#Lists raw products
@app.route('/list_raw_products/',methods=['POST','GET'])
def bringableProducts():
    
    ret = []
    rslt = dbm.selectRawProductNames()
    for x,y in enumerate(rslt):
        ret.append(({"product_name":y[2],"product_id":y[0],"product_price":1}))


    return resp_ok(raw_products=ret)

#Lists all usernames
@app.route('/list_usernames/',methods=['POST','GET'])
def listUsernames():
	
    ret= []    
    rslt = dbm.listUsernames()

    for x,y in enumerate(rslt):
	ret.append(y[0]) 

    return resp_ok(usernames=ret)


@app.route('/list_product_prices/',methods=['POST','GET'])
def productPrices():
    rslt = dbm.getFinalproducts()
    return resp_ok(product_prices=rslt)


#List all balances of a user
#input: username
@app.route('/list_user_saldos/',methods=['POST','GET'])
def listUserBalances():
    if request.method == 'POST':
        user = request.form['username']
        rslt = dbm.selectUserBalances(user)
        return resp_ok(saldo_list=rslt)
    else:
        return resp_failure('Error')


#Allows the user to buy products.
#input : username,productid,amount
@app.route('/buy_product/',methods=['POST','GET'])
def buy():
    if request.method == 'POST':
        product = request.form['product_id']
        user = request.form['username']
        amount = request.form['amount']
        r = dbm.buy(product,amount,user)
        print product,amount,user
        
        if r:
            return resp_ok()
        else:
            return resp_failure('Error')
        
    else:
        return json.dumps({'status':'Error'})

@app.route('/bring_product/',methods=['POST','GET'])
def bring():
    return resp_failure('Not implemented')

@app.route('/list_stations/',methods=['POST','GET'])
def stations():
    stations = ["Station1","Station2"]
    return resp_ok(stations=stations)

if __name__ == '__main__':
    app.debug = True

    app.run(host='0.0.0.0')
