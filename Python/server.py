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
savefile = "testdat2a.pkl"

if os.path.exists(savefile):
    cvm.loadData(savefile)

stations = {"Station1":dbm}

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

#@app.before_request
#def before_request(station):
#    stations[station].dbconnect()
    
@app.teardown_request
def teardown_request(exception,station):
    stations[station].dbclose()

#Returns available products and other useful stuff. (not yet implemented,obviously)
@app.route('/')
def mainpage():
    return 'Naamakahvi server v. 666'

#Trains the opencv-plugin.
#Input: name of uploaded file.
@app.route('/train/',methods=['POST','GET'])
def train():
    if request.method == 'POST':
        user = request.form['username']
        filename = request.form['filename']
        cvm.train(filename,user)
        cvm.saveData(savefile)
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
            id,prob = cvm.identify(secure_filename(file.filename))
            print id,prob
            
            if(prob == False):
                return resp_ok(username=None)
            else:
                return resp_ok(username=id[0])
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
            cvm.computeNets()
            cvm.saveData(savefile)
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
        
        for db in stations:
            if(not stations[db].login(user)):
                if r:
                    return resp_ok(username=user)
            	else:
                    return resp_failure('UserCreationFailed')        
            else:
            	return resp_failure('UserAlreadyExistsError')
    else:
        return resp_failure('Error')


#Logs user in. Good for checking if user exists.
@app.route('/get_user/',methods=['POST','GET'])
def login():
    if request.method == 'POST':
        user = request.form['username']
        station = request.form['station_name']
        stations[station].dbconnect()
        if(stations[station].login(user)):
            resp = {}
            
            udata = stations[station].selectUserData(user)
            print "############"
            print udata
            bal = getBalance(user,station)
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

    station = request.args["station_name"]
    stations[station].dbconnect()
    ret = []
    rslt = stations[station].selectFinProductNames()
    for x,y in enumerate(rslt):
        ret.append(({"product_name":y[2],"product_id":y[0],"group_id":y[4],"product_price":y[5]}))

    print ret
    return resp_ok(buyable_products=ret)


#Lists raw products
@app.route('/list_raw_products/',methods=['POST','GET'])
def rawsizes():
    
    station = request.args["station_name"]
    stations[station].dbconnect()
    ret = []
    rslt = stations[station].selectProductsizes()
    print rslt
    for x,y in enumerate(rslt):
        z = y[1]*y[8]
        n = str(y[2]) +" "+ str(y[10]) + " " + y[12]
        ret.append(({"product_name":n,"size_id":y[0],"product_id":y[3],"group_id":y[6],"product_price":z}))

    
    return resp_ok(raw_products=ret)



#Lists all usernames
@app.route('/list_usernames/',methods=['POST','GET'])
def listUsernames():
	
    station = request.args["station_name"]
    stations[station].dbconnect()
    ret= []    
    rslt = stations[station].listUsernames()

    for x,y in enumerate(rslt):
	ret.append(y[0]) 

    return resp_ok(usernames=ret)


@app.route('/list_product_prices/',methods=['POST','GET'])
def productPrices():
    station = request.args["station_name"]
    stations[station].dbconnect()
    rslt = stations[station].getFinalproducts()
    return resp_ok(product_prices=rslt)



#Allows the user to buy products.
#input : username,productid,amount
@app.route('/buy_product/',methods=['POST','GET'])
def buy():
    if request.method == 'POST':
        product = request.form['product_id']
        user = request.form['username']
        amount = request.form['amount']
        station = request.form['station_name']
        stations[station].dbconnect()
        
        r = stations[station].buy(product,amount,user)
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
        
        rawproductid = request.form['product_id']
        sizeid = request.form['size_id']

        station = request.form['station_name']

        amount = request.form['amount']
        user = request.form['username']
        station = request.form['station_name']
        stations[station].dbconnect()
        
        r = stations[station].bring(sizeid,rawproductid,amount,user)
        
        if r:
            return resp_ok()
        else:
            return resp_failure('Error')

@app.route('/list_stations/',methods=['POST','GET'])
def stations():
    #stations = ["Station1","Station2"]
    return resp_ok(stations=stations)

def getBalance(user,station):

    stations[station].dbconnect()
    
    rslt = stations[station].selectUserBalances(user)
    ret = []
    print "Saldos"
    print rslt
    for x,y in enumerate(rslt):
        retz = {}
        retz['group_id'] = y[0]
        retz['saldo'] = y[4]
        retz['groupName'] = y[2]
        ret.append(retz)
        
    return ret
    

if __name__ == '__main__':
    app.debug = True

    app.run(host='0.0.0.0')
