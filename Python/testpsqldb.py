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
        
        r = self.dbmps.insertImportProduct(5,2.0,"1 dl","Naamakahvi","FaceCafe","LOLWUT")
        self.assertTrue(r)

    def TestInsertExportProduct(self):
        
        r = self.dbmps.insertExportProduct(5,1,"Kahvi1","Coffee1","HejaHeja")

    def TestRegister(self):
        
        #r = self.dbm.register("testuser1","Test0","User")
        r = self.dbmps.register("test1","Test0","User",1)
        self.assertTrue(r)
    
    def TestLogin(self):
        
        r = self.dbmps.login('test1')
        self.assertTrue(r)
       
    def TestListFinProducts(self):
        
        r = self.dbmps.selectFinProductNames()
        


if __name__ == '__main__':
    logging.basicConfig( stream=sys.stderr )
    logging.getLogger( "Testpsqldb.TestConnect" ).setLevel( logging.DEBUG )
    unittest.main()

