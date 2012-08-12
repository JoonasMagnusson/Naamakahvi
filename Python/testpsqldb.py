import unittest
import logging
import psqldb
import sys

class Testpsqldb(unittest.TestCase):
    
    
    
    def setUp(self):
        
        self.log = logging.getLogger( "Testpsqldb.TestConnect" )
        self.db = psqldb.psqldb('naamakanta','sam','dbqueries.xml')
        
        self.db.dbconnect()
                
    def TestUserCreation(self):
        
        #Create normal user
        r = self.db.register("demo","Testi","Test")
        self.assertTrue(r)
        
        
        #Try to create user with insufficent data
        r = self.db.register("ABBA","","")
        self.assertFalse(r)
        
                
        #Log in the created user
        r = self.db.login("demo")
        self.assertTrue(r)
        
        #Try to log in 
        r = self.db.login('NotActuallyUser')
        self.assertFalse(r)
        
        #Delete user
        r = self.db.deleteUser("demo")
        self.assertTrue(r)
        
    def TestUserBalance(self):
        
        #Create normal user
        r = self.db.register("demo","Testi","Test")
        self.assertTrue(r)
        
        
        r = self.db.selectUserBalances("demo")
        print r
        self.assertTrue(False)
       
       
       
    '''    
    def TestInsertUserBalances(self):
        
        #self.dbmps.nukeTable("userbalance")
        r = self.db.insertUserBalances("test1",5,3.0)
        self.assertTrue(r)    
        
    def TestUpdateUserBalances(self):
        
        r = self.db.updateUserBalances(7.0,5,"test1")
        self.assertTrue(r)
    '''
      

        
if __name__ == '__main__':
    logging.basicConfig( stream=sys.stderr )
    logging.getLogger( "Testpsqldb.TestConnect" ).setLevel( logging.DEBUG )
    unittest.main()

