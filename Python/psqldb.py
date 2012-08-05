import psycopg2
#Should be standard stuff in most python installations
from xml.dom.minidom import parseString


class psqldb:
	
	#Constructor
	#db: 	Database to be used
	#xml: 	XML-file containig SQL queries
	def __init__(self,db,user,xml):
		self.dbname = db
		self.user = user
		self.connect = "dbname=" + db + " user=" + user
		self.xmldb =  xml
		self.parseXML(xml)
		print "DB Initialized."

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
		

	def getQuery(self,qname):
		dom  =	self.pxml
		for subelem in dom.getElementsByTagName('entry'):
			if (subelem.getAttribute("key")== qname):
				return subelem.firstChild.nodeValue


	#Checks if the user has been created in the database (has registered).
	def login(self,user):
		
		q = self.getQuery("isUserRegistered")

		try:
			self.cur.execute(q,(user,))		
		except  Exception ,e:
			print e
		
		if self.cur.fetchone()[0] == 'true':
			return True
		else: 
			return False
	
		
	#Creates new user
	def register(self,user, given, family):
			
		q = self.getQuery("insertUserdata")
		print q
		admin = False
		lang = 'eng'
		reg = 1
		try:
			self.cur.execute(q, (user, given, family, reg, lang, admin))
		except Exception, exc:
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
			return e
		self.con.commit()
		return True


	#
	def insertProductGroup(self,finname, engname, svename):
			
		q = self.getQuery("insertProductGroup")

		try:
			self.cur.execute(q, (finname,engname,svename))
		except  Exception ,e:
			return e
		self.con.commit()
		return True	
	
	def insertExportProduct(self,groupid, value,finname, engname, swename):
			
		q = self.getQuery("insertExportProduct")

		try:
			self.cur.execute(q, (groupid, value,finname, engname, swename))
		except  Exception ,e:
			return e
		self.con.commit()
		return True
	
	def selectFinProductNames(self):
		
		q = self.getQuery("selectEProductNames")		

		try:
			self.cur.execute(q)		
		except  Exception ,e:
			print e
			
		result = self.cur.fetchall()
		return result
	
	def selectRawProductNames(self):
		
		q = self.getQuery("selectIProductNames")		

		try:
			self.cur.execute(q)		
		except  Exception ,e:
			print e
			
		result = self.cur.fetchall()
		return result
	
	def selectProductsizes(self):
		
		
		q = self.getQuery("selectProductsizes")		

		try:
			self.cur.execute(q)		
		except  Exception ,e:
			print e
			
		result = self.cur.fetchall()
		return result
		
	
	def selectUserBalances(self,user):
		
		q = self.getQuery("selectUserBalances")		

		try:
			self.cur.execute(q,(user,))		
		except  Exception ,e:
			print e
			
		result = self.cur.fetchall()
		return result
	
	def insertUserBalances(self,balance,group,user):
			
		q = self.getQuery("insertUserBalances")

		try:
			self.cur.execute(q, (balance,group,user))
		except  Exception ,e:
			return e
		self.con.commit()
		return True	
	
	
	def updateUserBalances(self,balance,group,user):
			
		q = self.getQuery("updateUserBalances")

		try:
			self.cur.execute(q, (balance,group,user))
		except  Exception ,e:
			return e
		self.con.commit()
		return True
	
	
	def updateUserBalancesDeduct(self,balance,group,user):
			
		q = self.getQuery("updateUserBalancesDeduct")

		try:
			self.cur.execute(q, (balance,group,user))
		except  Exception ,e:
			return e
		self.con.commit()
		return True
	
	def listUsernames(self):
		
		q = self.getQuery("selectUsers")		

		try:
			self.cur.execute(q)		
		except  Exception ,e:
			print e
			
		result = self.cur.fetchall()
		return result
	
	def selectUserBalances(self,user):
		
		q = self.getQuery("selectUserBalances")		
		try:
			self.cur.execute(q,(user,))		
		except  Exception ,e:
			print e
			
		result = self.cur.fetchall()
		return result
	
	def getFinalproducts(self):
		
		q = self.getQuery("getFinalproducts")		
		try:
			self.cur.execute(q)		
		except  Exception ,e:
			print e
			
		result = self.cur.fetchall()
		return result
	
	def decGroupBalanceById(self,group,dec):
		
		q = self.getQuery("decGroupbalanceById")
		print q

		try:
			self.cur.execute(q, (dec,group,))
		except  Exception ,e:
			return e
		self.con.commit()
		return True
		
	def selectExportProductData(self,product):
	
		q = self.getQuery("selectExportProductData")
		
		try:
			self.cur.execute(q, (product))
		except	Exception ,e:
			return e
		data = self.cur.fetchone()
		
		datadict = {"final_id":data[0], "group_id":data[1], "value":data[2], "fin":data[3], "eng":data[4], "swe":data[5], "deacttime":data[6]}
		
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
		
		return True
	
	def bring(self,rawproduct,amount,user):
		
		if(not self.login(user)):
			return False
		
		q_rawdata = self.getQuery("selectImportProductData")
		try:
			self.cur.execute(q_rawdata, (rawproduct,))
		except	 Exception ,e:
			print e
		rawsize = self.cur.fetchone()[2]
		group = self.cur.fetchone()[1]
		
		q_value = self.getQuery("selectSizesInIProduct")
		try:
			self.cur.execute(q_value, (rawproduct,))		
		except  Exception ,e:
			print e
		
		sizeid = self.cur.fetchone()[2] 
		value = self.cur.fetchone()[1]
		amount = amount * rawsize * value
			
		q_gbal = self.getQuery("incGroupBalanceById")
		try:
			self.cur.execute(q_gbal, (amount,group))
		except  Exception ,e:
			return e
		
		q_ubal = self.getQuery("updateUserBalancesAdd")
		try:
			self.cur.execute(q_ubal, (amount,group,user,))
		except  Exception ,e:
			return e
			
		self.insertBring(user,rawproduct,sizeid,amount)
		return True
		
	def insertBuy(self,user,finalproduct,value):
		q = self.getQuery("insertProductout")
		try:
			self.cur.execute(q, (user,finalproduct,value,))
		except  Exception ,e:
			return e
		self.con.commit()
	
	def insertBring(self,user,rawproduct,productsize,value):
		q = self.getQuery("insertProductin")
		try:
			self.cur.execute(q, (user,rawproduct,productsize,value,))
		except  Exception ,e:
			return e
		self.con.commit()

	def nukeTable(self,table):
		
		q = "TRUNCATE TABLE " + table +" CASCADE;"
		
		try:
			self.cur.execute(q)
		except  Exception ,e:
			return e
		self.con.commit()
		return True	
		
		
