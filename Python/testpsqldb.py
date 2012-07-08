import unittest
import logging
import psqldb
import sys

class Testpsqldb(unittest.TestCase):
    
    
    
    def setUp(self):
        
        self.log = logging.getLogger( "Testpsqldb.TestConnect" )
        self.dbmps = psqldb.psqldb('naamakanta','sam','dbqueries.xml')
        
        self.dbmps.dbconnect()
        
    def TestInsertProductGroup(self):
        
        #self.dbmps.nukeTable("groupdata")
        r = self.dbmps.insertProductGroup("PG1","PG1en","PG1sve")
        self.assertTrue(r)

    
    def TestInsertImportProduct(self):
        
        #self.dbmps.nukeTable("rawproduct")
        r = self.dbmps.insertImportProduct(5,2.0,"1 dl","Naamakahvi","FaceCafe","LOLWUT")
        self.assertTrue(r)

    def TestInsertExportProduct(self):
        
        #self.dbmps.nukeTable("finalproduct")
        r = self.dbmps.insertExportProduct(5,1,"Kahvi1","Coffee1","HejaHeja")
        self.assertTrue(r)
            
    def TestRegister(self):
        
        #self.dbmps.nukeTable("userdata")
        #r = self.dbm.register("testuser1","Test0","User")
        r = self.dbmps.register("test1","Test0","User",60)
        self.assertTrue(r)
    
    def TestLogin(self):
        
        r = self.dbmps.login('test1')
        self.assertTrue(r)
       
    def TestListFinProducts(self):
        
        r = self.dbmps.selectFinProductNames()
        
    def TestInsertUserBalances(self):
        
        #self.dbmps.nukeTable("userbalance")
        r = self.dbmps.insertUserBalances("test1",5,3.0)
        self.assertTrue(r)    
        
    def TestUpdateUserBalances(self):
        
        r = self.dbmps.updateUserBalances(7.0,5,"test1")
        self.assertTrue(r)


if __name__ == '__main__':
    logging.basicConfig( stream=sys.stderr )
    logging.getLogger( "Testpsqldb.TestConnect" ).setLevel( logging.DEBUG )
    unittest.main()

