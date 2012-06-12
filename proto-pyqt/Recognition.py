from PyQt4 import uic
from Balance import BalanceWindow


FormClass, BaseClass = uic.loadUiType("recognition.ui")


class RecognitionWindow(BaseClass, FormClass):
    def __init__(self, parent=None):
        super(BaseClass, self).__init__(parent)
         
        self.setupUi(self)
        self.show()

    def showBalance(self):
    	self.balance = BalanceWindow()
    	self.close()

    def backToStart(self):
    	self.close