import psycopg2
#Should be standard stuff in most python installations
from xml.dom.minidom import parseString


class psqldb:
	
	#Constructor
	#db: 	Database to be used
	#xml: 	XML-file containig SQL queries
	def __init__(self,db,user):
		self.dbname = db
		self.user = user
		self.connect = "dbname=" + db + " user=" + user
		self.xmldb = 'dbqueries.xml'
		self.parseXML(xml)
		print "DB Initialized."	
	
	
	def __enter__(self):
		self.dbconnect()
		return self
	
	def __exit__(self,*args):
		self.dbclose()
		
	
	def parseXML(self,xmlfile):
		file = open(xmlfile,'r')
		data = file.read()
		file.close()

		#parse the data
		self.pxml = parseString(data)
	
	def dbconnect(self):
		self.con = psycopg2.connect(self.connect) 
		self.cur = self.con.cursor()
	
		
	def dbclose(self):
		self.cur.close()
		self.con.close()
		

	def getQuery(self,qname):
		dom  =	self.pxml
		for subelem in dom.getElementsByTagName('entry'):
			if (subelem.getAttribute("key")== qname):
				return subelem.firstChild.nodeValue


	#Checks if the user has been created in the database (has registered).
	
	def login(self,user):
	
		
		q = self.getQuery("isUserRegistered")
		
		cur = self.con.cursor()

		
		
		try:
			cur.execute(q,(user,))		
		except  Exception ,e:
			self.con.rollback()
			print e
		
		print cur.rowcount
		if (cur.rowcount > 0):
			r = cur.fetchone()[0]
			
			
		cur.close()	
		self.con.commit()


		print "u",user
		
		if r == 'true':
			return True
		else: 
			return False
	
		
	#Creates new user
	def register(self,user, given, family):
			
		q = self.getQuery("insertUserdata")
		
		admin = False
		lang = 'eng'
		reg = 1
		
		
		if (not(user and family and given)):
			return False
		
		try:
			self.cur.execute(q, (user, given, family, reg, lang, admin))
		except Exception, exc:
			self.con.rollback()
			return exc
		else:
			self.con.commit()
			return True


	def deleteUser(self,user):
		
		q = self.getQuery("deleteUser")
		
		
		try:
			self.cur.execute(q, (user,))
		except Exception, exc:
			self.con.rollback()
			return exc
		else:
			self.con.commit()
			return True
		
	
	#New importable product
	def insertImportProduct(self,groupid,value,sizeunit,finname,engname,svename):
	
		q = self.getQuery("insertImportProduct")
		reg = 1
		admin = False
		lang = 'eng'
		try:
			self.cur.execute(q, (groupid, value,sizeunit, finname, engname, svename))
		except  Exception ,e:
			self.con.rollback()
			return e
		self.con.commit()
		return True


	#
	def insertProductGroup(self,finname, engname, svename):
			
		q = self.getQuery("insertProductGroup")

		try:
			self.cur.execute(q, (finname,engname,svename))
		except  Exception ,e:
			self.con.rollback()
			return e
		self.con.commit()
		return True	
	
	def insertExportProduct(self,groupid, value,finname, engname, swename):
			
		q = self.getQuery("insertExportProduct")

		try:
			self.cur.execute(q, (groupid, value,finname, engname, swename))
		except  Exception ,e:
			self.con.rollback()			
			return e
		self.con.commit()
		return True
	
	def selectFinProductNames(self):
		
		q = self.getQuery("selectEProductNames")		

		try:
			self.cur.execute(q)		
		except  Exception ,e:
			self.con.rollback()			
			print e
			
		result = self.cur.fetchall()
		
		self.con.commit()
		return result
	
	def selectRawProductNames(self):
		
		q = self.getQuery("selectIProductNames")		

		try:
			self.cur.execute(q)		
		except  Exception ,e:
			self.con.rollback()			
			print e
			
		result = self.cur.fetchall()
		
		self.con.commit()
		return result
	
	def selectProductsizes(self):
		
		
		q = self.getQuery("selectProductsizes")		

		try:
			self.cur.execute(q)		
		except  Exception ,e:
			self.con.rollback()			
			print e
			
		result = self.cur.fetchall()
		
		self.con.commit()
		return result
		
	
	def selectUserBalances(self,user):
		
		q = self.getQuery("selectUserBalances")		

		try:
			self.cur.execute(q,(user,))		
		except  Exception ,e:
			self.con.rollback()			
			print e
			
		result = self.cur.fetchall()
		
		self.con.commit()
		return result
	
	def getUserBalanceById(self,id,user):
		
		q = self.getQuery("getUserbalanceByIds")		

		try:
			self.cur.execute(q,(id,user,))		
		except  Exception ,e:
			self.con.rollback()			
			print e
			
		result = self.cur.fetchall()
		
		self.con.commit()
		return result
		
	
	def insertUserBalances(self,balance,group,user):
			
		q = self.getQuery("insertUserBalances")

		try:
			self.cur.execute(q, (user,group,balance))
		except  Exception ,e:
			self.con.rollback()		
			return e
		
		self.con.commit()
		return True	
	
	def deleteUserBalances(self,user):
		
		q = self.getQuery("deleteUserBalance")

		try:
			self.cur.execute(q, (user,))
		except  Exception ,e:
			self.con.rollback()		
			return e
		
		self.con.commit()
		return True	
		
	
	def checkBalanceItem(self,groupid,user):
		
		q = self.getQuery("getUserbalanceByIds")		

		try:
			self.cur.execute(q,(groupid,user))		
		except  Exception ,e:
			self.con.rollback()			
			print e
		
		
		if (self.cur.rowcount > 0):
			self.con.commit()
			return
		
		else:
			print "Saldo Created"
			self.con.commit()
			self.insertUserBalances(0, groupid, user)
	

		
		
	
	def updateUserBalances(self,balance,group,user):
		
		self.checkBalanceItem(group, user)	
		
		
		q = self.getQuery("updateUserBalances")

		try:
			self.cur.execute(q, (balance,group,user))
		except  Exception ,e:
			self.con.rollback()			
			return e
		self.con.commit()
		return True
	
	
	def updateUserBalancesDeduct(self,balance,group,user):
		
		
		self.checkBalanceItem(group, user)
			
		q = self.getQuery("updateUserBalancesDeduct")

		try:
			self.cur.execute(q, (balance,group,user))
		except  Exception ,e:
			self.con.rollback()
			return e		
		#self.con.commit()
		return True
	
	def listUsernames(self):
		
		q = self.getQuery("selectUsers")		

		try:
			self.cur.execute(q)		
		except  Exception ,e:
			self.con.rollback()			
			print e
			
		result = self.cur.fetchall()
		
		self.con.commit()
		return result
	
	def selectUserData(self,user):
		
		q = self.getQuery("selectUserData")		
		try:
			self.cur.execute(q,(user,))		
		except  Exception ,e:
			self.con.rollback()			
			print e
			
		result = self.cur.fetchone()
				
		self.con.commit()
		return result
		
	
	def selectUserBalances(self,user):
		
		q = self.getQuery("selectUserBalances")	
			
		try:
			self.cur.execute(q,(user,))		
		except  Exception ,e:
			self.con.rollback()			
			print e
			
		result = self.cur.fetchall()
		
		self.con.commit()
		return result
	
	def getFinalproducts(self):
		
		q = self.getQuery("getFinalproducts")		
		try:
			self.cur.execute(q)		
		except  Exception ,e:
			self.con.rollback()			
			print e
			
		result = self.cur.fetchall()
		
		self.con.commit()
		return result
	
	def decGroupBalanceById(self,group,dec):
		
		q = self.getQuery("decGroupbalanceById")
		print q

		try:
			self.cur.execute(q, (dec,group,))
		except  Exception ,e:
			self.con.rollback()		
			return e
		
		#self.con.commit()
		return True
		
	def selectExportProductData(self,product):
	
		q = self.getQuery("selectExportProductData")
		
		try:
			self.cur.execute(q, (product))
		except	Exception ,e:
			self.con.rollback()			
			return e
		data = self.cur.fetchone()
		
		datadict = {"final_id":data[0], "group_id":data[1], "value":data[2], "fin":data[3], "eng":data[4], "swe":data[5], "deacttime":data[6]}
		
		self.con.commit()
		return datadict
		
	def buy(self,product,amount,user):
		
		if(not self.login(user)):
			return False
			
		data = self.selectExportProductData(product)
		
		print data		

		group = data["group_id"]
		amount = float(amount) * data["value"]
		self.decGroupBalanceById(group, amount)
		self.updateUserBalancesDeduct(amount, group, user)
		self.insertBuy(user,product,amount)
		
		self.con.commit()
		
		return True
	
	def bring(self,sizeid,rawproduct,amount,user):
		
		if(not self.login(user)):
			return False
		
		q_rawdata = self.getQuery("selectImportProductData")
		try:
			self.cur.execute(q_rawdata, (rawproduct,))
		except	 Exception ,e:
			self.con.rollback()			
			print e
			
		raw = self.cur.fetchone()
		raw_value = raw[2]
		group = raw[1]
		
		print "raw_value, group",raw_value, group
		
		q_value = self.getQuery("selectSizesInIProduct")
		try:
			self.cur.execute(q_value, (rawproduct,))		
		except  Exception ,e:
			print e
		
		size = self.cur.fetchone()
		sizeid = size[2] 
		value = size[1]
		
		print type(value)
		
		amount = float(amount)* float(raw_value) * value
		print "amount", amount
			
		q_gbal = self.getQuery("incGroupbalanceById")
		try:
			self.cur.execute(q_gbal, (amount,group))
		except  Exception ,e:
			self.con.rollback()		
			return e
		
		q_ubal = self.getQuery("updateUserBalancesAdd")
		try:
			self.cur.execute(q_ubal, (amount,group,user,))
		except  Exception ,e:
			self.con.rollback()			
			return e
			
		self.con.commit()
			
		self.insertBring(user,rawproduct,sizeid,amount)
		return True
		
	def insertBuy(self,user,finalproduct,value):
		q = self.getQuery("insertProductout")
		try:
			self.cur.execute(q, (user,finalproduct,value,))
		except  Exception ,e:
			self.con.rollback()			
			return e
		self.con.commit()
	
	def insertBring(self,user,rawproduct,productsize,value):
		q = self.getQuery("insertProductin")
		try:
			self.cur.execute(q, (user,rawproduct,productsize,value,))
		except  Exception ,e:
			self.con.rollback()			
			return e
		self.con.commit()


		
		
