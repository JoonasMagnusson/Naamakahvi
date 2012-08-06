import os
from flask import Flask
from flask import request, redirect, url_for
from werkzeug import secure_filename
import json
import psqldb
import neuralmodule

app = Flask(__name__)

dbm = psqldb.psqldb('naamakanta','sam','dbqueries.xml')
cvm = neuralmodule.neuralmodule()

def resp_ok(**kwargs):
    ans = {}
    ans['status'] = 'ok'
    for k, v in kwargs.iteritems():
        ans[k] = v
    return json.dumps(ans)

def resp_failure(status_msg, **kwargs):
    ans = {}
    ans['status'] = status_msg
    for k, v in kwargs.iteritems():
        ans[k] = v
    return json.dumps(ans)

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
        #user = request.form['username']
        file = request.files['file']
        if file:
            print secure_filename(file.filename)
            file.save(secure_filename(file.filename))
            id = cvm.identify(secure_filename(file.filename))
            print id
            return resp_ok(idlist=id)
        else:
            return resp_failure('Error')
            
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
            dbm.register(user,given,family)
            return resp_ok(username=user)
        else:
            return resp_failure('UserAlreadyExistsError')
    else:
        return resp_failure('Error')


#Logs user in. Good for checking if user exists.
@app.route('/get_user/',methods=['POST','GET'])
def login():
    if request.method == 'POST':
        user = request.form['username']
        if(dbm.login(user)):
            resp = {}
            
            udata = dbm.selectUserData(user)
            print "############"
            print udata
            bal = getBalance(user)
            resp["username"] = user
            resp["given"] = udata[1]
            resp["family"] = udata[2]
            resp["balance"] = bal
            
            return resp_ok(data=resp)
        else:
            return resp_failure('NoSuchUserError')
    else:
        return resp_failure('Error')

@app.route('/list_buyable_products/',methods=['POST','GET'])
def buyableProducts():

    ret = []
    rslt = dbm.selectFinProductNames()
    for x,y in enumerate(rslt):
        ret.append(({"product_name":y[2],"product_id":y[0],"group_id":y[4],"product_price":1}))

    print ret
    return resp_ok(buyable_products=ret)


#Lists raw products
@app.route('/list_raw_products/',methods=['POST','GET'])
def bringableProducts():
    
    ret = []
    rslt = dbm.selectRawProductNames()
    for x,y in enumerate(rslt):
        ret.append(({"product_name":y[2],"product_id":y[0],"group_id":y[5],"product_price":1}))


    return resp_ok(raw_products=ret)

@app.route('/list_productsizes/',methods=['POST','GET'])
def rawsizes():
    
    ret = []
    rslt = dbm.selectProductsizes()
    print rslt
    for x,y in enumerate(rslt):
        z = y[1]*y[8]
        n = str(y[2]) +" "+ str(y[10]) + " " + y[12]
        ret.append(({"product_name":n,"size_id":y[0],"rawproduct_id":y[3],"group_id":y[6],"product_price":z}))

    
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



#Allows the user to buy products.
#input : username,productid,amount
@app.route('/buy_product/',methods=['POST','GET'])
def buy():
    if request.method == 'POST':
        product = request.form['product_id']
        user = request.form['username']
        amount = request.form['amount']
        
        r = dbm.buy(product,amount,user)
        #print product,amount,user
        
        if r:
            return resp_ok()
        else:
            return resp_failure('Error')
        
    else:
        return json.dumps({'status':'Error'})

@app.route('/bring_product/',methods=['POST','GET'])
def bring():
    if request.method == 'POST':
        productname = request.form['product_name']
        rawproductid = request.form['product_id']
        stationname = request.form['station']
        amount = request.form['amount']
        user = request.form['username']
        
        r = dbm.bring(rawproductid,amount,user)
        
        if r:
            return resp_ok()
        else:
            return resp_failure('Error')

@app.route('/list_stations/',methods=['POST','GET'])
def stations():
    stations = ["Station1","Station2"]
    return resp_ok(stations=stations)

def getBalance(user):
    
    rslt = dbm.selectUserBalances(user)
    ret = []
    
    #print rslt
    for x,y in enumerate(rslt):
        retz = {}
        retz['id'] = y[0]
        retz['groupName'] = y[2]
        ret.append(retz)
        
    return ret
    

if __name__ == '__main__':
    app.debug = True

    app.run(host='0.0.0.0')
